package dr.achim.sleep_timer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.admin.DevicePolicyManager
import android.content.*
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

const val CHANNEL_NAME = "dr.achim/sleep_timer"

class MainActivity: FlutterActivity(), MethodChannel.MethodCallHandler {
    private val NOTIFICATION_ID: Int = 0
    private val NOTIF_CHANNEL_ID: String = "0"
    private val REQUEST_CODE_NOTIF_OPEN = 100
    private val REQUEST_CODE_NOTIF_CANCEL = 110
    private val REQUEST_CODE_NOTIF_DISMISS = 115
    private val REQUEST_CODE_NOTIF_PAUSE = 120
    private val REQUEST_CODE_NOTIF_CONTINUE = 130
    private val REQUEST_CODE_NOTIF_EXTEND_5 = 140
    private val REQUEST_CODE_NOTIF_EXTEND_20 = 141
    private val REQUEST_CODE_NOTIF_RESTART = 150
    private val REQUEST_CODE_ENABLE_ADMIN = 200
    private val REQUEST_CODE_NOTIF_SETTINGS_ACCESS = 300

    private lateinit var channel: MethodChannel
    private var result: MethodChannel.Result? = null

    private lateinit var deviceManager: DevicePolicyManager
    private lateinit var deviceAdmin: ComponentName

    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationChannel: NotificationChannel

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        this.channel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL_NAME)
        this.channel.setMethodCallHandler(this)
        initNotificationChannel()
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        this.result = result;

        when(call.method) {
            "initMainActivityEntry" -> {
                if (call.arguments == null) return
                EntryPointCallbackHelper.setHandle(this, call.arguments as Long)
            }
            "showRunningNotification" -> {
                val id = call.argument<Int>("id")
                val title = call.argument<String>("title")
                val subtitle = call.argument<String>("subtitle")
                val seconds = call.argument<Int>("seconds")
                val actionTitle1 = call.argument<String>("actionTitle1")
                val actionTitle2 = call.argument<String>("actionTitle2")
                val actionTitle3 = call.argument<String>("actionTitle3")

                val success = showRunningNotification(id, title, subtitle, seconds, actionTitle1, actionTitle2, actionTitle3)

                if (success) {
                    result.success(true)
                } else {
                    result.error("UNAVAILABLE", "Not supported by platform", null)
                }
            }
            "showPauseNotification" -> {
                val id = call.argument<Int>("id")
                val title = call.argument<String>("title")
                val subtitle = call.argument<String>("subtitle")
                val actionTitle1 = call.argument<String>("actionTitle1")
                val actionTitle2 = call.argument<String>("actionTitle2")

                val success = showPauseNotification(id, title, subtitle, actionTitle1, actionTitle2)

                if (success) {
                    result.success(true)
                } else {
                    result.error("UNAVAILABLE", "Not supported by platform", null)
                }
            }
            "showElapsedNotification" -> {
                val id = call.argument<Int>("id")
                val title = call.argument<String>("title")
                val subtitle = call.argument<String>("subtitle")
                val actionTitle1 = call.argument<String>("actionTitle1")
                val actionTitle2 = call.argument<String>("actionTitle2")

                val success = showElapsedNotification(id, title, subtitle, actionTitle1, actionTitle2)

                if (success) {
                    result.success(true)
                } else {
                    result.error("UNAVAILABLE", "Not supported by platform", null)
                }
            }
            "cancelNotification" -> {
                val id = call.argument<Int>("id")

                if (cancelNotification(id)) {
                    result.success(true)
                } else {
                    result.error("UNAVAILABLE", "Not supported by platform", null)
                }

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

    private fun showRunningNotification(id: Int?, title: String?, subtitle: String?, seconds: Int?, actionTitle1: String?, actionTitle2: String?, actionTitle3: String?): Boolean {
        val id = id ?: NOTIFICATION_ID

        val openIntent = createNotificationIntent(id, NOTIF_ACTION_OPEN, REQUEST_CODE_NOTIF_OPEN)
        val pauseIntent = createNotificationIntent(id, NOTIF_ACTION_PAUSE, REQUEST_CODE_NOTIF_PAUSE)
        val extendIntent5 = createNotificationIntent(id, NOTIF_ACTION_EXTEND_5, REQUEST_CODE_NOTIF_EXTEND_5)
        val extendIntent20 = createNotificationIntent(id, NOTIF_ACTION_EXTEND_20, REQUEST_CODE_NOTIF_EXTEND_20)

        val builder = NotificationCompat.Builder(context, NOTIF_CHANNEL_ID)
                .setShowWhen(true)
                .setUsesChronometer(true)
                .setOngoing(false) // TODO: set to true
                .setAutoCancel(false)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.ic_hourglass_full)
                .setContentIntent(openIntent)

        if(actionTitle1 != null) builder.addAction(R.drawable.ic_baseline_pause_24, actionTitle1.toUpperCase(), pauseIntent)
        if(actionTitle2 != null) builder.addAction(R.drawable.ic_baseline_replay_5_24, actionTitle2.toUpperCase(), extendIntent5)
        if(actionTitle3 != null) builder.addAction(R.drawable.ic_baseline_replay_10_24, actionTitle3.toUpperCase(), extendIntent20)
        if(seconds != null) builder.setWhen(System.currentTimeMillis() + seconds * 1000)
        if(title != null) builder.setContentTitle(title)
        if(subtitle != null) builder.setContentText(subtitle)

        NotificationManagerCompat.from(context).notify(id, builder.build())
        return true
    }

    private fun showPauseNotification(id: Int?, title: String?, subtitle: String?, actionTitle1: String?, actionTitle2: String?): Boolean{
        val id = id ?: NOTIFICATION_ID

        val openIntent = createNotificationIntent(id, NOTIF_ACTION_OPEN, REQUEST_CODE_NOTIF_OPEN)
        val cancelIntent = createNotificationIntent(id, NOTIF_ACTION_CANCEL, REQUEST_CODE_NOTIF_CANCEL)
        val continueIntent = createNotificationIntent(id, NOTIF_ACTION_CONTINUE, REQUEST_CODE_NOTIF_CONTINUE)

        val builder = NotificationCompat.Builder(context, NOTIF_CHANNEL_ID)
                .setShowWhen(false)
                .setUsesChronometer(false)
                .setSmallIcon(R.drawable.ic_hourglass_full)
                .setContentIntent(openIntent)

        if(actionTitle1 != null) builder.addAction(R.drawable.ic_baseline_clear_24, actionTitle1.toUpperCase(), cancelIntent)
        if(actionTitle2 != null) builder.addAction(R.drawable.ic_baseline_play_arrow_24, actionTitle2.toUpperCase(), continueIntent)
        if(title != null) builder.setContentTitle(title)
        if(subtitle != null) builder.setContentText(subtitle)

        NotificationManagerCompat.from(context).notify(id, builder.build())
        return true
    }

    private fun showElapsedNotification(id: Int?, title: String?, subtitle: String?, actionTitle1: String?, actionTitle2: String?): Boolean{
        val id = id ?: NOTIFICATION_ID

        val openIntent = createNotificationIntent(id, NOTIF_ACTION_OPEN, REQUEST_CODE_NOTIF_OPEN)
        val dismissIntent = createNotificationIntent(id, NOTIF_ACTION_CANCEL, REQUEST_CODE_NOTIF_DISMISS)
        val restartIntent = createNotificationIntent(id, NOTIF_ACTION_CONTINUE, REQUEST_CODE_NOTIF_RESTART)

        val builder = NotificationCompat.Builder(context, NOTIF_CHANNEL_ID)
                .setShowWhen(false)
                .setUsesChronometer(false)
                .setSmallIcon(R.drawable.ic_hourglass_full)
                .setContentIntent(openIntent)

        if(actionTitle1 != null) builder.addAction(R.drawable.ic_baseline_clear_24, actionTitle1.toUpperCase(), dismissIntent)
        if(actionTitle2 != null) builder.addAction(R.drawable.ic_baseline_replay_24, actionTitle2.toUpperCase(), restartIntent)
        if(title != null) builder.setContentTitle(title)
        if(subtitle != null) builder.setContentText(subtitle)

        NotificationManagerCompat.from(context).notify(id, builder.build())
        return true
    }

    private fun cancelNotification(id: Int?): Boolean {
        val id = id ?: NOTIFICATION_ID
        NotificationManagerCompat.from(context).cancel(id)
        return true
    }

    private fun initNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            notificationChannel = NotificationChannel(NOTIF_CHANNEL_ID, "Active timer", importance).apply {
                description = "Notify about running or pausing timers"
            }
            NotificationManagerCompat.from(context).createNotificationChannel(notificationChannel)
        }
    }

    private fun createNotificationIntent(id: Int, action: String, requestCode: Int): PendingIntent {
        val intent = Intent(context, NotificationReceiver::class.java).apply{
            this.action = action
            putExtra("id", id)
        }
        return PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)
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