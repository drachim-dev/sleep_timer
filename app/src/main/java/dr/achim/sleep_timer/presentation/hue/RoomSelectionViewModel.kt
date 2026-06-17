package dr.achim.sleep_timer.presentation.hue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dr.achim.sleep_timer.data.remote.hue.HueGroup
import dr.achim.sleep_timer.domain.usecase.ManageHueUseCase
import dr.achim.sleep_timer.model.HueActionSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.InjectedParam

sealed interface RoomSelectionUiState {
    val source: HueActionSource

    data class Loading(override val source: HueActionSource) : RoomSelectionUiState
    data class Success(
        val groups: List<HueGroup>,
        val selectedGroups: Set<String>,
        override val source: HueActionSource
    ) : RoomSelectionUiState
}

class RoomSelectionViewModel(
    private val manageHueUseCase: ManageHueUseCase,
    @InjectedParam private val source: HueActionSource
) : ViewModel() {

    private val _selectedGroups = MutableStateFlow<Set<String>>(emptySet())
    private val _groups = MutableStateFlow<List<HueGroup>>(emptyList())
    private val _isLoading = MutableStateFlow(true)

    val uiState = combine(
        _selectedGroups,
        _groups,
        _isLoading
    ) { selected, groups, loading ->
        if (loading) {
            RoomSelectionUiState.Loading(source)
        } else {
            RoomSelectionUiState.Success(
                groups = groups,
                selectedGroups = selected,
                source = source
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RoomSelectionUiState.Loading(source)
    )

    init {
        viewModelScope.launch {
            val initial = when (source) {
                HueActionSource.START -> manageHueUseCase.getStartGroups().firstOrNull()
                HueActionSource.END -> manageHueUseCase.getEndGroups().firstOrNull()
            }
            _selectedGroups.value = initial.orEmpty()
        }

        viewModelScope.launch {
            combine(
                manageHueUseCase.getPairedIp(),
                manageHueUseCase.getPairedUser()
            ) { ip, user -> ip to user }.collect { (ip, user) ->
                if (ip != null && user != null) {
                    _isLoading.value = true
                    _groups.value = manageHueUseCase.fetchGroups(ip, user)
                    _isLoading.value = false
                } else {
                    _groups.value = emptyList()
                    _isLoading.value = false
                }
            }
        }
    }

    fun onAction(action: RoomSelectionUiAction) {
        when (action) {
            is RoomSelectionUiAction.ToggleGroup -> toggleGroup(action.groupId)
            RoomSelectionUiAction.Save -> save()
        }
    }

    private fun toggleGroup(groupId: String) {
        val current = _selectedGroups.value
        _selectedGroups.value = if (current.contains(groupId)) {
            current - groupId
        } else {
            current + groupId
        }
    }

    private fun save() {
        viewModelScope.launch {
            when (source) {
                HueActionSource.START -> manageHueUseCase.setStartGroups(_selectedGroups.value)
                HueActionSource.END -> manageHueUseCase.setEndGroups(_selectedGroups.value)
            }
        }
    }
}
