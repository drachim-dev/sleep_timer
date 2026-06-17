package dr.achim.sleep_timer.data

import android.app.NotificationManager
import android.app.admin.DevicePolicyManager
import android.bluetooth.BluetoothManager
import android.content.ComponentName
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import dr.achim.sleep_timer.model.EndActions
import dr.achim.sleep_timer.model.StartActions
import dr.achim.sleep_timer.receiver.SleepTimerAdminReceiver
import kotlinx.coroutines.flow.firstOrNull

class TimerActionExecutor(
    private val context: Context,
    private val notificationManager: NotificationManager,
    private val devicePolicyManager: DevicePolicyManager,
    private val audioManager: AudioManager,
    private val hueRepository: HueRepository,
    private val settingsRepository: SettingsRepository
) {
    private val adminComponent = ComponentName(context, SleepTimerAdminReceiver::class.java)
    private var originalVolume: Int = -1

    suspend fun applyStartActions(actions: StartActions) {
        if (actions.adjustVolume) {
            actions.volumeLevel?.let { level ->
                originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                val targetVolume = (maxVolume * level / 100f).toInt()
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, targetVolume, 0)
            }
        }

        if (actions.enableDnd && notificationManager.isNotificationPolicyAccessGranted) {
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE)
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

    suspend fun applyEndActions(actions: EndActions, startActions: StartActions) {
        if (actions.stopMedia) {
            stopMedia()
        }

        if (actions.adjustVolume) {
            actions.volumeLevel?.let { level ->
                val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                val targetVolume = (maxVolume * level / 100f).toInt()
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, targetVolume, 0)
            } ?: run {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0)
            }
        }

        if (actions.turnOffScreen) {
            if (devicePolicyManager.isAdminActive(adminComponent)) {
                devicePolicyManager.lockNow()
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

        // Restore DND if it was enabled and we have permission
        if (startActions.enableDnd && notificationManager.isNotificationPolicyAccessGranted) {
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
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

    private fun stopMedia() {
        val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAcceptsDelayedFocusGain(true)
            .setOnAudioFocusChangeListener { }
            .build()
        audioManager.requestAudioFocus(focusRequest)
        audioManager.abandonAudioFocusRequest(focusRequest)
    }
}
