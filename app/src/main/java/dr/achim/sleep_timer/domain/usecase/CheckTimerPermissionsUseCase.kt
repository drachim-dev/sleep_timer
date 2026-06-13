package dr.achim.sleep_timer.domain.usecase

import dr.achim.sleep_timer.data.TimerController

class CheckTimerPermissionsUseCase(private val timerController: TimerController) {
    fun isDeviceAdminEnabled(): Boolean = timerController.isDeviceAdminEnabled()
    fun hasNotificationAccess(): Boolean = timerController.hasDndPermission()
}
