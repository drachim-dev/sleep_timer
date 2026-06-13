package dr.achim.sleep_timer.presentation.home

sealed interface HomeUiAction {
    data class UpdateQuickTime(val index: Int, val minutes: Int) : HomeUiAction
    data class UpdateLastSelectedMinutes(val minutes: Int) : HomeUiAction
    data object StopTimer : HomeUiAction
}
