package dr.achim.sleep_timer.presentation.hue

sealed interface RoomSelectionUiAction {
    data class ToggleGroup(val groupId: String) : RoomSelectionUiAction
    data object Save : RoomSelectionUiAction
    data object Refresh : RoomSelectionUiAction
}
