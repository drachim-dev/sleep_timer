package dr.achim.sleep_timer.data

import android.Manifest
import android.app.NotificationManager
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
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

    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun hasNearbyPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true

        val nearbyWifi = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.NEARBY_WIFI_DEVICES
        ) == PackageManager.PERMISSION_GRANTED

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CINNAMON_BUN) {
            nearbyWifi && ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_LOCAL_NETWORK
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            nearbyWifi
        }
    }

    fun getNearbyPermissions(): Array<String> = buildSet {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.NEARBY_WIFI_DEVICES)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CINNAMON_BUN) {
            add(Manifest.permission.ACCESS_LOCAL_NETWORK)
        }
    }.toTypedArray()

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
