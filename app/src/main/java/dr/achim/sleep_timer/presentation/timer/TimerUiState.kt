package dr.achim.sleep_timer.presentation.timer

import dr.achim.sleep_timer.domain.model.QuickLaunchApp
import dr.achim.sleep_timer.model.TimerActions
import dr.achim.sleep_timer.model.TimerState

data class TimerUiState(
    val timerState: TimerState = TimerState.Idle(),
    val timerActions: TimerActions = TimerActions(),
    val quickLaunchApps: List<QuickLaunchApp> = emptyList(),
    val selectedApps: List<QuickLaunchApp?> = listOf(null, null),
    val isDeviceAdminEnabled: Boolean = false,
    val hasNotificationAccess: Boolean = false,
    val glowEnabled: Boolean = false,
    val glowIntensity: Float = 0f
)
