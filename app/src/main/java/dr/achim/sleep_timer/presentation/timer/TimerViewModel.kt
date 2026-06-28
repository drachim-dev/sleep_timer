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
import dr.achim.sleep_timer.model.TimerActionSource
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

        viewModelScope.launch {
            manageHueUseCase.getStartGroups().distinctUntilChanged().collect { groups ->
                if (groups.isEmpty()) {
                    manageTimerActionsUseCase.setHueLights(TimerActionSource.START, false)
                    pendingHueActivations[TimerActionSource.START] = false
                } else if (pendingHueActivations[TimerActionSource.START] == true) {
                    manageTimerActionsUseCase.setHueLights(TimerActionSource.START, true)
                    pendingHueActivations[TimerActionSource.START] = false
                }
            }
        }
        viewModelScope.launch {
            manageHueUseCase.getEndGroups().distinctUntilChanged().collect { groups ->
                if (groups.isEmpty()) {
                    manageTimerActionsUseCase.setHueLights(TimerActionSource.END, false)
                    pendingHueActivations[TimerActionSource.END] = false
                } else if (pendingHueActivations[TimerActionSource.END] == true) {
                    manageTimerActionsUseCase.setHueLights(TimerActionSource.END, true)
                    pendingHueActivations[TimerActionSource.END] = false
                }
            }
        }
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.ToggleAdjustVolume -> toggleAdjustVolume(action.source, action.enabled)
            is Action.SetVolumeLevel -> setVolumeLevel(action.source, action.level)
            is Action.ToggleDnd -> toggleDnd(action.enabled)
            is Action.ToggleHueLights -> toggleHueLights(action.source, action.enabled)
            is Action.OpenHueSettings -> viewModelScope.launch {
                pendingHueActivations[action.source] = true
                _uiEvents.send(TimerUiEvent.NavigateToRoomSelection(action.source)) 
            }
            is Action.ToggleStopMedia -> toggleStopMedia(action.enabled)
            is Action.ToggleScreenOff -> toggleScreenOff(action.enabled)
            is Action.ToggleBluetooth -> toggleBluetooth(action.enabled)
            is Action.SetRemainingTime -> setRemainingTime(action.millis)
            is Action.StartTimer -> startTimer(action.millis)
            is Action.StopTimer -> stopTimer()
            is Action.TogglePauseResume -> togglePauseResume()
            is Action.RefreshAdminStatus -> {
                val permissions = checkTimerPermissionsUseCase()
                _permissionsFlow.value = permissions
                if (permissions.isDeviceAdminEnabled && action.autoEnable) {
                    toggleScreenOff(true)
                }
            }
            is Action.RefreshDndStatus -> {
                val permissions = checkTimerPermissionsUseCase()
                _permissionsFlow.value = permissions
                if (permissions.hasNotificationAccess && action.autoEnable) {
                    toggleDnd(true)
                }
            }
            is Action.RefreshPermissions -> {
                _permissionsFlow.value = checkTimerPermissionsUseCase()
            }
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

    private fun setMediaVolume(level: Int, flags: Int) {
        setMediaVolumeUseCase(level, flags)
    }

    private fun setQuickLaunchApp(index: Int, packageName: String?) {
        viewModelScope.launch {
            manageQuickLaunchUseCase.setQuickLaunchApp(index, packageName)
        }
    }

    private fun toggleAdjustVolume(source: TimerActionSource, enabled: Boolean) {
        viewModelScope.launch {
            manageTimerActionsUseCase.setAdjustVolume(source, enabled)
        }
    }

    private fun setVolumeLevel(source: TimerActionSource, level: Int?) {
        viewModelScope.launch {
            manageTimerActionsUseCase.setVolumeLevel(source, level)
        }
    }

    private fun toggleDnd(enabled: Boolean) {
        viewModelScope.launch {
            manageTimerActionsUseCase.setStartEnableDnd(enabled)
        }
    }

    private fun toggleHueLights(source: TimerActionSource, enabled: Boolean) {
        viewModelScope.launch {
            if (enabled && !manageHueUseCase.isConfigured(source)) {
                pendingHueActivations[source] = true
                _uiEvents.send(TimerUiEvent.NavigateToRoomSelection(source))
            } else {
                if (!enabled) pendingHueActivations[source] = false
                manageTimerActionsUseCase.setHueLights(source, enabled)
            }
        }
    }

    private fun toggleStopMedia(enabled: Boolean) {
        viewModelScope.launch {
            manageTimerActionsUseCase.setEndStopMedia(enabled)
        }
    }

    private fun toggleScreenOff(enabled: Boolean) {
        viewModelScope.launch {
            manageTimerActionsUseCase.setEndTurnOffScreen(enabled)
        }
    }

    private fun toggleBluetooth(enabled: Boolean) {
        viewModelScope.launch {
            manageTimerActionsUseCase.setEndTurnOffBluetooth(enabled)
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
