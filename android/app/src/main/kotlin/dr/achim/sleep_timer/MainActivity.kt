package dr.achim.sleep_timer

import android.app.NotificationManager
import android.app.admin.DevicePolicyManager
import android.content.*
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

const val CHANNEL_NAME = "dr.achim/sleep_timer"

class MainActivity2: FlutterActivity(), MethodChannel.MethodCallHandler {
    private val REQUEST_CODE_ENABLE_ADMIN = 200
    private val REQUEST_CODE_NOTIF_SETTINGS_ACCESS = 300

    private lateinit var channel: MethodChannel
    private var result: MethodChannel.Result? = null

    private lateinit var deviceManager: DevicePolicyManager
    private lateinit var deviceAdmin: ComponentName

    private lateinit var notificationManager: NotificationManager

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        this.channel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL_NAME)
        this.channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        this.result = result;

        when(call.method) {
            "initMainActivityEntry" -> {
                if (call.arguments == null) return
                EntryPointCallbackHelper.setHandle(this, call.arguments as Long)
            }
            "isDeviceAdminActive" -> {
                result.success(isDeviceAdminActive())
            }
            "toggleDeviceAdmin" -> {
                val enabled = call.argument<Boolean>("enabled")

                if (enabled != null && toggleDeviceAdmin(enabled)) {
                } else {
                    result.error("UNAVAILABLE", "Not supported by platform", null)
                }
            }
            "isNotificationSettingsAccessActive" -> {
                result.success(isNotificationSettingsAccessActive())
            }
            "toggleNotificationSettingsAccess" -> {
                val enabled = call.argument<Boolean>("enabled")

                if (enabled != null) {
                    toggleNotificationSettingsAccess(enabled)
                } else {
                    result.error("UNAVAILABLE", "Not supported by platform", null)
                }
            }
            else -> result.notImplemented()
        }
    }

    private fun isDeviceAdminActive() : Boolean {
        deviceManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        deviceAdmin = ComponentName(this, DeviceAdmin::class.java)
        return deviceManager.isAdminActive(deviceAdmin)
    }

    private fun toggleDeviceAdmin(enabled: Boolean) : Boolean {
        deviceManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        deviceAdmin = ComponentName(this, DeviceAdmin::class.java)

        if(enabled) {
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, deviceAdmin)
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "You should enable the app!")
            startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN)
        } else {
            deviceManager.removeActiveAdmin(deviceAdmin)
            result?.success(true)
        }
        return true
    }

    private fun isNotificationSettingsAccessActive() : Boolean {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return notificationManager.isNotificationPolicyAccessGranted
    }

    private fun toggleNotificationSettingsAccess(enabled: Boolean) {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
        startActivityForResult(intent, REQUEST_CODE_NOTIF_SETTINGS_ACCESS)

        val filter = IntentFilter(NotificationManager.ACTION_NOTIFICATION_POLICY_ACCESS_GRANTED_CHANGED)
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val active = isNotificationSettingsAccessActive()
                val success = enabled == active

                if(active) {
                    Toast.makeText(context, "Enabled", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Disabled", Toast.LENGTH_SHORT).show()
                }

                result?.success(success)
            }
        }
        registerReceiver(receiver, filter)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_ENABLE_ADMIN -> {
                if (resultCode == RESULT_OK) {
                    result?.success(true)
                }
            }
            REQUEST_CODE_NOTIF_SETTINGS_ACCESS -> {
                if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(context, "onActivityResult", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return
    }
}