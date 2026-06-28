package dr.achim.sleep_timer.presentation.timer

import dr.achim.sleep_timer.model.TimerActionSource
import dr.achim.sleep_timer.model.TimerActionType

sealed interface TimerUiAction {
    data class ToggleAction(val type: TimerActionType, val source: TimerActionSource, val enabled: Boolean) : TimerUiAction
    data class SetVolumeLevel(val source: TimerActionSource, val level: Int?) : TimerUiAction
    data class OpenHueSettings(val source: TimerActionSource) : TimerUiAction
    data class SetRemainingTime(val millis: Long) : TimerUiAction
    data class StartTimer(val millis: Long) : TimerUiAction
    object StopTimer : TimerUiAction
    object TogglePauseResume : TimerUiAction
    data class RefreshAdminStatus(val autoEnable: Boolean) : TimerUiAction
    data class RefreshDndStatus(val autoEnable: Boolean) : TimerUiAction
    object RefreshPermissions : TimerUiAction
    object OnResume : TimerUiAction
    data class AddMinutes(val minutes: Long) : TimerUiAction
    data class SetQuickLaunchApp(val index: Int, val packageName: String?) : TimerUiAction
    data class SetMediaVolume(val level: Int, val flags: Int = 0) : TimerUiAction
}
