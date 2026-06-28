package dr.achim.sleep_timer.presentation.timer

import dr.achim.sleep_timer.model.HueActionSource

sealed interface TimerUiAction {
    data class ToggleStartVolume(val enabled: Boolean) : TimerUiAction
    data class SetStartVolumeLevel(val level: Int?) : TimerUiAction
    data class ToggleDnd(val enabled: Boolean) : TimerUiAction
    data class ToggleHueLights(val enabled: Boolean) : TimerUiAction
    data class ToggleEndHueLights(val enabled: Boolean) : TimerUiAction
    data class OpenHueSettings(val source: HueActionSource) : TimerUiAction
    data class ToggleStopMedia(val enabled: Boolean) : TimerUiAction
    data class ToggleEndVolume(val enabled: Boolean) : TimerUiAction
    data class SetEndVolumeLevel(val level: Int?) : TimerUiAction
    data class ToggleScreenOff(val enabled: Boolean) : TimerUiAction
    data class ToggleBluetooth(val enabled: Boolean) : TimerUiAction
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
