package dr.achim.sleep_timer.presentation.home

import dr.achim.sleep_timer.model.TimerState

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Content(
        val glowEnabled: Boolean = false,
        val glowIntensity: Float = 0f,
        val quickTimes: List<Int> = emptyList(),
        val lastSelectedMinutes: Int = 0,
        val timerState: TimerState = TimerState.Idle(),
        val timerStartCount: Int = 0,
        val showNotificationRationale: Boolean = false,
        val showNotificationSettingsPrompt: Boolean = false
    ) : HomeUiState
}
