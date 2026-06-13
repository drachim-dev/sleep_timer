package dr.achim.sleep_timer.presentation.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dr.achim.sleep_timer.domain.usecase.CheckTimerPermissionsUseCase
import dr.achim.sleep_timer.domain.usecase.ControlTimerUseCase
import dr.achim.sleep_timer.domain.usecase.GetTimerStatusUseCase
import dr.achim.sleep_timer.domain.usecase.ManageQuickLaunchUseCase
import dr.achim.sleep_timer.domain.usecase.ManageTimerActionsUseCase
import dr.achim.sleep_timer.domain.usecase.VolumeType
import dr.achim.sleep_timer.model.TimerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.InjectedParam
import dr.achim.sleep_timer.presentation.timer.TimerUiAction as Action

class TimerViewModel(
    getTimerStatusUseCase: GetTimerStatusUseCase,
    private val controlTimerUseCase: ControlTimerUseCase,
    private val manageTimerActionsUseCase: ManageTimerActionsUseCase,
    private val manageQuickLaunchUseCase: ManageQuickLaunchUseCase,
    private val checkTimerPermissionsUseCase: CheckTimerPermissionsUseCase,
    @InjectedParam minutes: Int?
) : ViewModel() {

    private val _isDeviceAdminEnabled = MutableStateFlow(checkTimerPermissionsUseCase.isDeviceAdminEnabled())
    private val _hasNotificationAccess = MutableStateFlow(checkTimerPermissionsUseCase.hasNotificationAccess())

    val uiState: StateFlow<TimerUiState> = combine(
        getTimerStatusUseCase.timerState,
        getTimerStatusUseCase.observeTimerActions(),
        manageQuickLaunchUseCase.getSelectedApps(),
        _isDeviceAdminEnabled,
        _hasNotificationAccess
    ) { timerState, timerActions, selectedApps, isAdmin, hasDnd ->
        TimerUiState(
            timerState = timerState,
            timerActions = timerActions,
            quickLaunchApps = manageQuickLaunchUseCase.getApps(),
            selectedApps = selectedApps,
            isDeviceAdminEnabled = isAdmin,
            hasNotificationAccess = hasDnd
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TimerUiState()
    )

    init {
        if (minutes != null) {
            startTimer(minutes * 60 * 1000L)
        }
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.ToggleStartVolume -> toggleStartVolume(action.enabled)
            is Action.SetStartVolumeLevel -> setStartVolumeLevel(action.level)
            is Action.ToggleDnd -> toggleDnd(action.enabled)
            is Action.ToggleHueLights -> toggleHueLights(action.enabled)
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
                val enabled = checkTimerPermissionsUseCase.isDeviceAdminEnabled()
                _isDeviceAdminEnabled.value = enabled
                if (enabled && action.autoEnable) {
                    toggleScreenOff(true)
                }
            }
            is Action.RefreshDndStatus -> {
                val enabled = checkTimerPermissionsUseCase.hasNotificationAccess()
                _hasNotificationAccess.value = enabled
                if (enabled && action.autoEnable) {
                    toggleDnd(true)
                }
            }
            is Action.AddMinutes -> addMinutes(action.minutes)
            is Action.SetQuickLaunchApp -> setQuickLaunchApp(action.index, action.packageName)
        }
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
            manageTimerActionsUseCase.setStartHueLights(enabled)
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
