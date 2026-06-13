package dr.achim.sleep_timer.data

import android.app.NotificationManager
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import dr.achim.sleep_timer.receiver.SleepTimerAdminReceiver
import dr.achim.sleep_timer.service.TimerService

class TimerController(
    private val context: Context,
    private val notificationManager: NotificationManager,
    private val devicePolicyManager: DevicePolicyManager
) {
    private val adminComponent = ComponentName(context, SleepTimerAdminReceiver::class.java)

    fun isDeviceAdminEnabled(): Boolean = devicePolicyManager.isAdminActive(adminComponent)

    fun removeActiveAdmin() {
        devicePolicyManager.removeActiveAdmin(adminComponent)
    }

    fun hasDndPermission(): Boolean = notificationManager.isNotificationPolicyAccessGranted

    fun startTimer(millis: Long) {
        val intent = Intent(context, TimerService::class.java).apply {
            action = TimerService.ACTION_START
            putExtra(TimerService.EXTRA_DURATION_MILLIS, millis)
        }
        context.startForegroundService(intent)
    }

    fun stopTimer() {
        val intent = Intent(context, TimerService::class.java).apply {
            action = TimerService.ACTION_STOP
        }
        context.startService(intent)
    }

    fun pauseTimer() {
        val intent = Intent(context, TimerService::class.java).apply {
            action = TimerService.ACTION_PAUSE
        }
        context.startService(intent)
    }

    fun resumeTimer() {
        val intent = Intent(context, TimerService::class.java).apply {
            action = TimerService.ACTION_RESUME
        }
        context.startService(intent)
    }
}
