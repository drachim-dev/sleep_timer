package dr.achim.sleep_timer.presentation.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dr.achim.sleep_timer.common.combine
import dr.achim.sleep_timer.domain.model.QuickLaunchApp
import dr.achim.sleep_timer.domain.usecase.CheckTimerPermissionsUseCase
import dr.achim.sleep_timer.domain.usecase.ControlTimerUseCase
import dr.achim.sleep_timer.domain.usecase.GetSettingsUseCase
import dr.achim.sleep_timer.domain.usecase.GetTimerStatusUseCase
import dr.achim.sleep_timer.domain.usecase.ManageHueUseCase
import dr.achim.sleep_timer.domain.usecase.ManageQuickLaunchUseCase
import dr.achim.sleep_timer.domain.usecase.ManageTimerActionsUseCase
import dr.achim.sleep_timer.domain.usecase.SetMediaVolumeUseCase
import dr.achim.sleep_timer.model.ActionPermissions
import dr.achim.sleep_timer.model.TimerActionSource
import dr.achim.sleep_timer.model.TimerActionType
import dr.achim.sleep_timer.model.TimerState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.InjectedParam
import dr.achim.sleep_timer.presentation.timer.TimerUiAction as Action

sealed interface TimerUiEvent {
    data class NavigateToRoomSelection(val source: TimerActionSource) : TimerUiEvent
    object RequestReview : TimerUiEvent
}

class TimerViewModel(
    getTimerStatusUseCase: GetTimerStatusUseCase,
    private val controlTimerUseCase: ControlTimerUseCase,
    private val manageTimerActionsUseCase: ManageTimerActionsUseCase,
    private val manageHueUseCase: ManageHueUseCase,
    private val manageQuickLaunchUseCase: ManageQuickLaunchUseCase,
    private val checkTimerPermissionsUseCase: CheckTimerPermissionsUseCase,
    private val setMediaVolumeUseCase: SetMediaVolumeUseCase,
    getSettingsUseCase: GetSettingsUseCase,
    @InjectedParam minutes: Int?
) : ViewModel() {

    private val _permissionsFlow = MutableStateFlow(checkTimerPermissionsUseCase())
    private val _quickLaunchApps = MutableStateFlow<List<QuickLaunchApp>>(emptyList())

    private val pendingHueActivations = mutableMapOf<TimerActionSource, Boolean>()

    private val _uiEvents = Channel<TimerUiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    val uiState: StateFlow<TimerUiState> = combine(
        getTimerStatusUseCase.timerState,
        getTimerStatusUseCase.observeTimerActions(),
        manageQuickLaunchUseCase.getSelectedApps(),
        _permissionsFlow,
        _quickLaunchApps,
        getSettingsUseCase()
    ) { timerState, timerActions, selectedApps, permissions, apps, settings ->
        TimerUiState(
            timerState = timerState,
            timerActions = timerActions,
            quickLaunchApps = apps,
            selectedApps = selectedApps,
            isDeviceAdminEnabled = permissions.isDeviceAdminEnabled,
            hasNotificationAccess = permissions.hasNotificationAccess,
            hasNearbyPermission = permissions.hasNearbyPermission,
            glowEnabled = settings.glowEffectEnabled,
            glowIntensity = settings.glowIntensity,
            timerStartCount = settings.timerStartCount,
            lastReviewTimestamp = settings.lastReviewTimestamp
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TimerUiState()
    )

    init {
        _quickLaunchApps.value = manageQuickLaunchUseCase.getApps()

        if (minutes != null) {
            startTimer(minutes * 60 * 1000L)
        }

        observeHueGroups(TimerActionSource.START)
        observeHueGroups(TimerActionSource.END)
    }

    private fun observeHueGroups(source: TimerActionSource) {
        viewModelScope.launch {
            manageHueUseCase.observeGroups(source).distinctUntilChanged().collect { groups ->
                if (groups.isEmpty()) {
                    manageTimerActionsUseCase.updateAction(TimerActionType.HUE_LIGHTS, source, false)
                    pendingHueActivations[source] = false
                } else if (pendingHueActivations[source] == true) {
                    manageTimerActionsUseCase.updateAction(TimerActionType.HUE_LIGHTS, source, true)
                    pendingHueActivations[source] = false
                }
            }
        }
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.ToggleAction -> toggleAction(action.type, action.source, action.enabled)
            is Action.SetVolumeLevel -> setVolumeLevel(action.source, action.level)
            is Action.OpenHueSettings -> viewModelScope.launch {
                pendingHueActivations[action.source] = true
                _uiEvents.send(TimerUiEvent.NavigateToRoomSelection(action.source)) 
            }
            is Action.SetRemainingTime -> setRemainingTime(action.millis)
            is Action.StartTimer -> startTimer(action.millis)
            is Action.StopTimer -> stopTimer()
            is Action.TogglePauseResume -> togglePauseResume()
            is Action.RefreshAdminStatus -> {
                val permissions = refreshPermissions()
                if (permissions.isDeviceAdminEnabled && action.autoEnable) {
                    toggleAction(TimerActionType.TURN_OFF_SCREEN, TimerActionSource.END, true)
                }
            }
            is Action.RefreshDndStatus -> {
                val permissions = refreshPermissions()
                if (permissions.hasNotificationAccess && action.autoEnable) {
                    toggleAction(TimerActionType.DND, TimerActionSource.START, true)
                }
            }
            is Action.RefreshPermissions -> refreshPermissions()
            is Action.OnResume -> onResume()
            is Action.AddMinutes -> addMinutes(action.minutes)
            is Action.SetQuickLaunchApp -> setQuickLaunchApp(action.index, action.packageName)
            is Action.SetMediaVolume -> setMediaVolume(action.level, action.flags)
        }
    }

    private fun onResume() {
        viewModelScope.launch {
            _uiEvents.send(TimerUiEvent.RequestReview)
        }
    }

    private fun refreshPermissions(): ActionPermissions {
        val permissions = checkTimerPermissionsUseCase()
        _permissionsFlow.value = permissions
        return permissions
    }

    private fun setMediaVolume(level: Int, flags: Int) {
        setMediaVolumeUseCase(level, flags)
    }

    private fun setQuickLaunchApp(index: Int, packageName: String?) {
        viewModelScope.launch {
            manageQuickLaunchUseCase.setQuickLaunchApp(index, packageName)
        }
    }

    private fun toggleAction(type: TimerActionType, source: TimerActionSource, enabled: Boolean) {
        viewModelScope.launch {
            if (type == TimerActionType.HUE_LIGHTS && enabled && !manageHueUseCase.isConfigured(source)) {
                pendingHueActivations[source] = true
                _uiEvents.send(TimerUiEvent.NavigateToRoomSelection(source))
            } else {
                if (type == TimerActionType.HUE_LIGHTS && !enabled) {
                    pendingHueActivations[source] = false
                }
                manageTimerActionsUseCase.updateAction(type, source, enabled)
            }
        }
    }

    private fun setVolumeLevel(source: TimerActionSource, level: Int?) {
        viewModelScope.launch {
            manageTimerActionsUseCase.setVolumeLevel(source, level)
        }
    }

    private fun setRemainingTime(millis: Long) {
        controlTimerUseCase.setRemainingTime(millis)
    }

    private fun startTimer(millis: Long) {
        controlTimerUseCase.start(millis)
    }

    private fun stopTimer() {
        controlTimerUseCase.stop()
    }

    private fun togglePauseResume() {
        when (uiState.value.timerState) {
            is TimerState.Idle -> return
            is TimerState.Running -> controlTimerUseCase.pause()
            is TimerState.Paused -> controlTimerUseCase.resume()
        }
    }

    private fun addMinutes(minutes: Long) {
        when (val state = uiState.value.timerState) {
            is TimerState.Idle -> startTimer(minutes * 60 * 1000)
            else -> {
                val newTime = state.remainingTimeMillis + (minutes * 60 * 1000)
                startTimer(newTime)
            }
        }
    }
}
