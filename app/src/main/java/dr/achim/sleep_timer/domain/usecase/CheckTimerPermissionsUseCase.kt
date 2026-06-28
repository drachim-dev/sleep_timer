package dr.achim.sleep_timer.domain.usecase

import dr.achim.sleep_timer.data.TimerController
import dr.achim.sleep_timer.model.ActionPermissions

class CheckTimerPermissionsUseCase(private val timerController: TimerController) {
    operator fun invoke() = ActionPermissions(
        isDeviceAdminEnabled = timerController.isDeviceAdminEnabled(),
        hasNotificationAccess = timerController.hasDndPermission(),
        hasNearbyPermission = timerController.hasNearbyPermission()
    )
}