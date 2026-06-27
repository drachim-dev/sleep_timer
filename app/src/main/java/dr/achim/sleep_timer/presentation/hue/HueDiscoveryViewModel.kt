package dr.achim.sleep_timer.presentation.hue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dr.achim.sleep_timer.common.combine
import dr.achim.sleep_timer.common.launchLoading
import dr.achim.sleep_timer.data.LinkResult
import dr.achim.sleep_timer.data.remote.hue.HueBridge
import dr.achim.sleep_timer.domain.usecase.ManageHueUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HueDiscoveryData(
    val bridges: List<HueBridge> = emptyList(),
    val pairedBridgeIp: String? = null,
    val isSearching: Boolean = false
)

sealed interface HueDiscoveryUiState {
    data object Loading : HueDiscoveryUiState
    data object Empty : HueDiscoveryUiState
    data class PermissionDenied(val shouldShowRationale: Boolean) : HueDiscoveryUiState
    data class Display(val data: HueDiscoveryData) : HueDiscoveryUiState
    data class Linking(
        val data: HueDiscoveryData,
        val bridge: HueBridge,
        val error: String? = null
    ) : HueDiscoveryUiState
}

sealed class HueNavEvent {
    data object LinkingSuccess : HueNavEvent()
}

class HueDiscoveryViewModel(private val manageHueUseCase: ManageHueUseCase) : ViewModel() {

    private val _discoveredBridges = MutableStateFlow<List<HueBridge>>(emptyList())
    private val _isSearching = MutableStateFlow(true)
    private val _linkingBridge = MutableStateFlow<HueBridge?>(null)
    private val _linkingError = MutableStateFlow<String?>(null)
    private val _hasPermission = MutableStateFlow(manageHueUseCase.hasNearbyPermission())
    private val _shouldShowRationale = MutableStateFlow(true)

    private var searchJob: Job? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _pairedData = manageHueUseCase.getPairedIp().map { ip -> ip }

    val uiState = combine(
        _discoveredBridges,
        _isSearching,
        _linkingBridge,
        _linkingError,
        _pairedData,
        _hasPermission,
        _shouldShowRationale
    ) { bridges, isSearching, linkingBridge, linkingError, pairedIp, hasPermission, shouldShowRationale ->

        if (!hasPermission) {
            return@combine HueDiscoveryUiState.PermissionDenied(shouldShowRationale)
        }

        val data = HueDiscoveryData(
            bridges = bridges,
            pairedBridgeIp = pairedIp,
            isSearching = isSearching
        )

        when {
            linkingBridge != null -> HueDiscoveryUiState.Linking(data, linkingBridge, linkingError)
            bridges.isEmpty() && !isSearching -> HueDiscoveryUiState.Empty
            else -> HueDiscoveryUiState.Display(data)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HueDiscoveryUiState.Loading
    )

    private val _navEvents = Channel<HueNavEvent>()
    val navEvents = _navEvents.receiveAsFlow()

    fun getNearbyPermissions() = manageHueUseCase.getNearbyPermissions()

    fun hasNearbyPermission() = manageHueUseCase.hasNearbyPermission()

    fun onAction(action: HueDiscoveryUiAction) {
        when (action) {
            HueDiscoveryUiAction.DiscoverBridges -> discoverBridges()
            is HueDiscoveryUiAction.StartLinking -> startLinking(action.bridge)
            is HueDiscoveryUiAction.ManualIpDiscovery -> discoverBridgeByIp(action.ip)
            HueDiscoveryUiAction.LinkBridge -> linkBridge()
            HueDiscoveryUiAction.UnlinkBridge -> unlinkBridge()
            HueDiscoveryUiAction.CancelLinking -> cancelLinking()
            HueDiscoveryUiAction.NavigateToRoomSelection -> navigateToRoomSelection()
            HueDiscoveryUiAction.PermissionGranted -> {
                _hasPermission.value = true
                discoverBridges()
            }
            is HueDiscoveryUiAction.PermissionDenied -> {
                _hasPermission.value = false
                _shouldShowRationale.value = action.shouldShowRationale
            }
        }
    }

    private fun discoverBridges() {
        searchJob = launchLoading(
            loadingState = _isSearching,
            previousJob = searchJob,
            block = { manageHueUseCase.discoverBridges() },
            onSuccess = { bridges ->
                _discoveredBridges.value = bridges
            }
        )
    }

    private fun discoverBridgeByIp(ip: String) {
        searchJob = launchLoading(
            loadingState = _isSearching,
            previousJob = searchJob,
            block = { manageHueUseCase.discoverBridgeByIp(ip) },
            onSuccess = { bridge ->
                if (bridge != null) {
                    _discoveredBridges.value += bridge
                    startLinking(bridge)
                }
            }
        )
    }

    private fun startLinking(bridge: HueBridge) {
        _linkingBridge.value = bridge
        _linkingError.value = null
    }

    private fun linkBridge() {
        val bridge = _linkingBridge.value ?: return

        viewModelScope.launch {
            _linkingError.value = null
            when (val result = manageHueUseCase.link(bridge)) {
                is LinkResult.Success -> {
                    _linkingBridge.value = null
                    navigateToRoomSelection()
                }

                is LinkResult.LinkButtonNotPressed -> {
                    _linkingError.value = "Link button not pressed. Please try again."
                }

                is LinkResult.Error -> {
                    _linkingError.value = result.message
                }
            }
        }
    }

    private fun unlinkBridge() {
        viewModelScope.launch {
            manageHueUseCase.unlink()
        }
    }

    private fun navigateToRoomSelection() {
        viewModelScope.launch {
            _navEvents.send(HueNavEvent.LinkingSuccess)
        }
    }

    private fun cancelLinking() {
        _linkingBridge.value = null
        _linkingError.value = null
    }
}
