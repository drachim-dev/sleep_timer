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
import dr.achim.sleep_timer.domain.usecase.VolumeType
import dr.achim.sleep_timer.model.HueActionSource
import dr.achim.sleep_timer.model.TimerState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.InjectedParam
import dr.achim.sleep_timer.presentation.timer.TimerUiAction as Action

sealed class TimerNavEvent {
    data class NavigateToRoomSelection(val source: HueActionSource) : TimerNavEvent()
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

    private var isPendingStartHueActivation = false
    private var isPendingEndHueActivation = false

    private val _navEvents = Channel<TimerNavEvent>()
    val navEvents = _navEvents.receiveAsFlow()

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
            glowEnabled = settings.glowEffectEnabled,
            glowIntensity = settings.glowIntensity
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
            manageHueUseCase.getStartGroups().collect { groups ->
                if (isPendingStartHueActivation && groups.isNotEmpty()) {
                    manageTimerActionsUseCase.setStartHueLights(true)
                    isPendingStartHueActivation = false
                }
            }
        }
        viewModelScope.launch {
            manageHueUseCase.getEndGroups().collect { groups ->
                if (isPendingEndHueActivation && groups.isNotEmpty()) {
                    manageTimerActionsUseCase.setEndHueLights(true)
                    isPendingEndHueActivation = false
                }
            }
        }
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.ToggleStartVolume -> toggleStartVolume(action.enabled)
            is Action.SetStartVolumeLevel -> setStartVolumeLevel(action.level)
            is Action.ToggleDnd -> toggleDnd(action.enabled)
            is Action.ToggleHueLights -> toggleHueLights(action.enabled)
            is Action.ToggleEndHueLights -> toggleEndHueLights(action.enabled)
            is Action.OpenHueSettings -> viewModelScope.launch { 
                _navEvents.send(TimerNavEvent.NavigateToRoomSelection(action.source)) 
            }
            is Action.ToggleStopMedia -> toggleStopMedia(action.enabled)
            is Action.ToggleEndVolume -> toggleEndVolume(action.enabled)
            is Action.SetEndVolumeLevel -> setEndVolumeLevel(action.level)
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
            is Action.AddMinutes -> addMinutes(action.minutes)
            is Action.SetQuickLaunchApp -> setQuickLaunchApp(action.index, action.packageName)
            is Action.SetMediaVolume -> setMediaVolume(action.level, action.flags)
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

    private fun toggleStartVolume(enabled: Boolean) {
        viewModelScope.launch {
            manageTimerActionsUseCase.setStartAdjustVolume(enabled)
        }
    }

    private fun setStartVolumeLevel(level: Int?) {
        viewModelScope.launch {
            manageTimerActionsUseCase.setVolumeLevel(VolumeType.START, level)
        }
    }

    private fun toggleDnd(enabled: Boolean) {
        viewModelScope.launch {
            manageTimerActionsUseCase.setStartEnableDnd(enabled)
        }
    }

    private fun toggleHueLights(enabled: Boolean) {
        viewModelScope.launch {
            if (enabled && !manageHueUseCase.isConfigured(HueActionSource.START)) {
                isPendingStartHueActivation = true
                _navEvents.send(TimerNavEvent.NavigateToRoomSelection(HueActionSource.START))
            } else {
                if (!enabled) isPendingStartHueActivation = false
                manageTimerActionsUseCase.setStartHueLights(enabled)
            }
        }
    }

    private fun toggleEndHueLights(enabled: Boolean) {
        viewModelScope.launch {
            if (enabled && !manageHueUseCase.isConfigured(HueActionSource.END)) {
                isPendingEndHueActivation = true
                _navEvents.send(TimerNavEvent.NavigateToRoomSelection(HueActionSource.END))
            } else {
                if (!enabled) isPendingEndHueActivation = false
                manageTimerActionsUseCase.setEndHueLights(enabled)
            }
        }
    }

    private fun toggleStopMedia(enabled: Boolean) {
        viewModelScope.launch {
            manageTimerActionsUseCase.setEndStopMedia(enabled)
        }
    }

    private fun toggleEndVolume(enabled: Boolean) {
        viewModelScope.launch {
            manageTimerActionsUseCase.setEndAdjustVolume(enabled)
        }
    }

    private fun setEndVolumeLevel(level: Int?) {
        viewModelScope.launch {
            manageTimerActionsUseCase.setVolumeLevel(VolumeType.END, level)
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
