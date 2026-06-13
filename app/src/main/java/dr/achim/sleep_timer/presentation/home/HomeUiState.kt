package dr.achim.sleep_timer.presentation.home

import dr.achim.sleep_timer.model.TimerState

data class HomeUiState(
    val glowEnabled: Boolean = false,
    val glowIntensity: Float = 0f,
    val quickTimes: List<Int> = emptyList(),
    val lastSelectedMinutes: Int = 50,
    val timerState: TimerState = TimerState.Idle()
)
