package dr.achim.sleep_timer.data

import android.app.NotificationManager
import android.app.admin.DevicePolicyManager
import android.bluetooth.BluetoothManager
import android.content.ComponentName
import android.content.Context
import android.os.Build
import dr.achim.sleep_timer.domain.repository.AudioRepository
import dr.achim.sleep_timer.model.EndActions
import dr.achim.sleep_timer.model.StartActions
import dr.achim.sleep_timer.receiver.SleepTimerAdminReceiver
import kotlinx.coroutines.flow.firstOrNull

class TimerActionExecutor(
    private val context: Context,
    private val notificationManager: NotificationManager,
    private val devicePolicyManager: DevicePolicyManager,
    private val audioRepository: AudioRepository,
    private val hueRepository: HueRepository,
    private val settingsRepository: SettingsRepository
) {
    private val adminComponent = ComponentName(context, SleepTimerAdminReceiver::class.java)

    suspend fun applyStartActions(actions: StartActions) {
        if (actions.adjustVolume) {
            actions.volumeLevel?.let { level ->
                audioRepository.setMediaVolume(level)
            }
        }

        if (actions.enableDnd && notificationManager.isNotificationPolicyAccessGranted) {
            try {
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALARMS)
            } catch (_: SecurityException) {
                // Permission might have been revoked just now
            }
        }

        if (actions.hueLights) {
            val ip = settingsRepository.hueBridgeIp.firstOrNull()
            val user = settingsRepository.hueApiUser.firstOrNull()
            val groups = settingsRepository.hueStartGroups.firstOrNull() ?: emptySet()
            if (ip != null && user != null) {
                turnOffHueLights(ip, user, groups)
            }
        }
    }

    suspend fun applyEndActions(actions: EndActions) {
        if (actions.stopMedia) {
            audioRepository.stopMedia()
        }

        if (actions.adjustVolume) {
            actions.volumeLevel?.let { level ->
                audioRepository.setMediaVolume(level)
            } ?: run {
                audioRepository.setMediaVolume(0)
            }
        }

        if (actions.turnOffScreen) {
            if (devicePolicyManager.isAdminActive(adminComponent)) {
                try {
                    devicePolicyManager.lockNow()
                } catch (_: SecurityException) {
                    // Permission might have been revoked just now
                }
            }
        }

        if (actions.turnOffBluetooth) {
            turnOffBluetooth()
        }

        if (actions.hueLights) {
            val ip = settingsRepository.hueBridgeIp.firstOrNull()
            val user = settingsRepository.hueApiUser.firstOrNull()
            val groups = settingsRepository.hueEndGroups.firstOrNull() ?: emptySet()
            if (ip != null && user != null) {
                turnOffHueLights(ip, user, groups)
            }
        }
    }

    private suspend fun turnOffHueLights(ip: String, user: String, groups: Set<String>) {
        if (groups.isNotEmpty()) {
            groups.forEach { groupId ->
                hueRepository.turnOffGroup(ip, user, groupId)
            }
        } else {
            hueRepository.turnOffLights(ip, user)
        }
    }

    private fun turnOffBluetooth() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            try {
                val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
                val bluetoothAdapter = bluetoothManager.adapter
                if (bluetoothAdapter?.isEnabled == true) {
                    @Suppress("DEPRECATION")
                    bluetoothAdapter.disable()
                }
            } catch (_: SecurityException) {
                // Permission not granted
            }
        }
    }
}
