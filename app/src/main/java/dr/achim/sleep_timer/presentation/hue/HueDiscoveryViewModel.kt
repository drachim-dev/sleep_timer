package dr.achim.sleep_timer.presentation.hue

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dr.achim.sleep_timer.common.combine
import dr.achim.sleep_timer.data.PairResult
import dr.achim.sleep_timer.data.remote.hue.HueBridge
import dr.achim.sleep_timer.domain.usecase.ManageHueUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

data class HueDiscoveryData(
    val bridges: List<HueBridge> = emptyList(),
    val pairedBridgeIp: String? = null,
    val isSearching: Boolean = false
)

sealed interface HueDiscoveryUiState {
    data object Loading : HueDiscoveryUiState
    data object Empty : HueDiscoveryUiState
    data object PermissionDenied : HueDiscoveryUiState
    data class Display(val data: HueDiscoveryData) : HueDiscoveryUiState
    data class Pairing(
        val data: HueDiscoveryData,
        val bridge: HueBridge,
        val error: String? = null
    ) : HueDiscoveryUiState
}

sealed class HueNavEvent {
    data object PairingSuccess : HueNavEvent()
}

class HueDiscoveryViewModel(private val manageHueUseCase: ManageHueUseCase) : ViewModel() {

    private val _discoveredBridges = MutableStateFlow<List<HueBridge>>(emptyList())
    private val _isSearching = MutableStateFlow(false)
    private val _pairingBridge = MutableStateFlow<HueBridge?>(null)
    private val _pairingError = MutableStateFlow<String?>(null)
    private val _hasPermission = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _pairedData = manageHueUseCase.getPairedIp().map { ip -> ip }

    val uiState = combine(
        _discoveredBridges,
        _isSearching,
        _pairingBridge,
        _pairingError,
        _pairedData,
        _hasPermission
    ) { bridges, isSearching, pairingBridge, pairingError, pairedIp, hasPermission ->

        if (!hasPermission && Build.VERSION.SDK_INT >= 33) {
            return@combine HueDiscoveryUiState.PermissionDenied
        }

        val filteredBridges = bridges.filter { it.ipAddress != pairedIp }
        val data = HueDiscoveryData(
            bridges = filteredBridges,
            pairedBridgeIp = pairedIp,
            isSearching = isSearching
        )

        when {
            pairingBridge != null -> HueDiscoveryUiState.Pairing(data, pairingBridge, pairingError)
            filteredBridges.isEmpty() && !isSearching -> HueDiscoveryUiState.Empty
            else -> HueDiscoveryUiState.Display(data)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HueDiscoveryUiState.Loading
    )

    private val _navEvents = Channel<HueNavEvent>()
    val navEvents = _navEvents.receiveAsFlow()

    fun onAction(action: HueDiscoveryUiAction) {
        when (action) {
            HueDiscoveryUiAction.DiscoverBridges -> discoverBridges()
            is HueDiscoveryUiAction.StartPairing -> startPairing(action.bridge)
            is HueDiscoveryUiAction.ManualIpDiscovery -> discoverBridgeByIp(action.ip)
            HueDiscoveryUiAction.PairBridge -> pairBridge()
            HueDiscoveryUiAction.CancelPairing -> cancelPairing()
            HueDiscoveryUiAction.NavigateToRoomSelection -> navigateToRoomSelection()
            HueDiscoveryUiAction.PermissionGranted -> {
                _hasPermission.value = true
                discoverBridges()
            }
        }
    }

    private fun discoverBridges() {
        viewModelScope.launch {
            _isSearching.value = true
            val minDurationJob = launch { delay(400.milliseconds) }
            val bridges = manageHueUseCase.discoverBridges()
            minDurationJob.join()
            _discoveredBridges.value = bridges
            _isSearching.value = false
        }
    }

    private fun discoverBridgeByIp(ip: String) {
        viewModelScope.launch {
            _isSearching.value = true
            val bridge = manageHueUseCase.discoverBridgeByIp(ip)
            if (bridge != null) {
                _discoveredBridges.value += bridge
                startPairing(bridge)
            }
            _isSearching.value = false
        }
    }

    private fun startPairing(bridge: HueBridge) {
        _pairingBridge.value = bridge
        _pairingError.value = null
    }

    private fun pairBridge() {
        val bridge = _pairingBridge.value ?: return

        viewModelScope.launch {
            _pairingError.value = null
            when (val result = manageHueUseCase.pair(bridge)) {
                is PairResult.Success -> {
                    _pairingBridge.value = null
                    navigateToRoomSelection()
                }

                is PairResult.LinkButtonNotPressed -> {
                    _pairingError.value = "Link button not pressed. Please try again."
                }

                is PairResult.Error -> {
                    _pairingError.value = result.message
                }
            }
        }
    }

    private fun navigateToRoomSelection() {
        viewModelScope.launch {
            _navEvents.send(HueNavEvent.PairingSuccess)
        }
    }

    private fun cancelPairing() {
        _pairingBridge.value = null
        _pairingError.value = null
    }
}
