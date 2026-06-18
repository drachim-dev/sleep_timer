package dr.achim.sleep_timer.presentation.home

import dr.achim.sleep_timer.common.Constants.DEFAULT_SELECTED_MINUTES
import dr.achim.sleep_timer.model.TimerState

data class HomeUiState(
    val glowEnabled: Boolean = false,
    val glowIntensity: Float = 0f,
    val quickTimes: List<Int> = emptyList(),
    val lastSelectedMinutes: Int = DEFAULT_SELECTED_MINUTES,
    val timerState: TimerState = TimerState.Idle(),
    val timerStartCount: Int = 0
)
