package dr.achim.sleep_timer.presentation.hue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dr.achim.sleep_timer.common.launchLoading
import dr.achim.sleep_timer.data.remote.hue.HueGroup
import dr.achim.sleep_timer.domain.usecase.ManageHueUseCase
import dr.achim.sleep_timer.model.TimerActionSource
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.InjectedParam

sealed interface RoomSelectionUiState {
    val source: TimerActionSource

    data class Loading(override val source: TimerActionSource) : RoomSelectionUiState
    data class Success(
        val groups: List<HueGroup>,
        val selectedGroups: Set<String>,
        override val source: TimerActionSource
    ) : RoomSelectionUiState
}

class RoomSelectionViewModel(
    private val manageHueUseCase: ManageHueUseCase,
    @InjectedParam private val source: TimerActionSource
) : ViewModel() {

    private val _selectedGroups = MutableStateFlow<Set<String>>(emptySet())
    private val _groups = MutableStateFlow<List<HueGroup>>(emptyList())
    private val _isLoading = MutableStateFlow(true)

    private var loadJob: Job? = null

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
            combine(
                manageHueUseCase.getPairedIp(),
                manageHueUseCase.getPairedUser()
            ) { ip, user -> ip to user }
                .distinctUntilChanged()
                .collect {
                    loadData()
                }
        }
    }

    fun onAction(action: RoomSelectionUiAction) {
        when (action) {
            is RoomSelectionUiAction.ToggleGroup -> toggleGroup(action.groupId)
            RoomSelectionUiAction.Save -> save()
            RoomSelectionUiAction.Refresh -> loadData()
        }
    }

    private fun loadData() {
        loadJob = launchLoading(
            loadingState = _isLoading,
            previousJob = loadJob,
            block = {
                val ip = manageHueUseCase.getPairedIp().firstOrNull()
                val user = manageHueUseCase.getPairedUser().firstOrNull()

                val fetchedGroups = if (ip != null && user != null) {
                    manageHueUseCase.fetchGroups(ip, user)
                } else {
                    emptyList()
                }

                val initial = when (source) {
                    TimerActionSource.START -> manageHueUseCase.getStartGroups().firstOrNull()
                    TimerActionSource.END -> manageHueUseCase.getEndGroups().firstOrNull()
                }
                val initialSelected = initial.orEmpty()
                fetchedGroups to initialSelected
            },
            onSuccess = { (fetchedGroups, initialSelected) ->
                _groups.value = fetchedGroups
                _selectedGroups.value = initialSelected
            }
        )
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
                TimerActionSource.START -> manageHueUseCase.setStartGroups(_selectedGroups.value)
                TimerActionSource.END -> manageHueUseCase.setEndGroups(_selectedGroups.value)
            }
        }
    }
}
