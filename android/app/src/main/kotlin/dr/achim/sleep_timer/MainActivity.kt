package dr.achim.sleep_timer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
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
    private val REQUEST_CODE_NOTIF_PAUSE = 120
    private val REQUEST_CODE_NOTIF_CONTINUE = 130
    private val REQUEST_CODE_NOTIF_EXTEND = 140
    private val REQUEST_CODE_ENABLE_ADMIN = 200

    private lateinit var channel: MethodChannel
    private var result: MethodChannel.Result? = null

    private lateinit var deviceManager: DevicePolicyManager
    private lateinit var deviceAdmin: ComponentName

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        this.channel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL_NAME)
        this.channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        this.result = result

        when(call.method) {
            "initialize" -> {
                if (call.arguments == null) return
                WidgetHelper.setHandle(this, call.arguments as Long)
            }
            "showNotification" -> {
                val id = call.argument<Int>("id")
                val title = call.argument<String>("title")
                val subtitle = call.argument<String>("subtitle")
                val seconds = call.argument<Int>("seconds")
                val actionTitle1 = call.argument<String>("actionTitle1")
                val actionTitle2 = call.argument<String>("actionTitle2")

                val success = showNotification(id, title, subtitle, seconds, actionTitle1, actionTitle2)

                if(success) {
                    result.success(true)
                } else {
                    result.error("UNAVAILABLE", "Not supported by platform", null)
                }
            }
            "pauseNotification" -> {
                val id = call.argument<Int>("id")
                val title = call.argument<String>("title")
                val subtitle = call.argument<String>("subtitle")
                val actionTitle1 = call.argument<String>("actionTitle1")

                val success = pauseNotification(id, title, subtitle, actionTitle1)
                
                if(success) {
                    result.success(true)
                } else {
                    result.error("UNAVAILABLE", "Not supported by platform", null)
                }
            }
            "cancelNotification" -> {
                val id = call.argument<Int>("id")

                if(cancelNotification(id)) {
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
            else -> result.notImplemented()
        }
    }

    private fun showNotification(id: Int?, title: String?, subtitle: String?, seconds: Int?, actionTitle1: String?, actionTitle2: String?): Boolean {
        val id = id ?: NOTIFICATION_ID

        createNotificationChannel()

        //val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        //val className = launchIntent?.component!!.className
        val openIntent = createNotificationIntent(id, NOTIF_ACTION_OPEN, REQUEST_CODE_NOTIF_OPEN)
        val pauseIntent = createNotificationIntent(id, NOTIF_ACTION_PAUSE, REQUEST_CODE_NOTIF_PAUSE)
        val extendIntent = createNotificationIntent(id, NOTIF_ACTION_EXTEND, REQUEST_CODE_NOTIF_EXTEND)

        val builder = NotificationCompat.Builder(context, NOTIF_CHANNEL_ID)
                .setShowWhen(true)
                .setUsesChronometer(true)
                .setOngoing(false) // TODO: set to true
                .setAutoCancel(false)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.ic_hourglass_full)
                .setContentIntent(openIntent)
                .addAction(android.R.drawable.ic_media_pause, "Pause".toUpperCase(), pauseIntent)

        if(actionTitle1 != null) builder.addAction(R.drawable.ic_more_time, actionTitle1.toUpperCase(), extendIntent)
        if(actionTitle2 != null) builder.addAction(R.drawable.ic_more_time, actionTitle2.toUpperCase(), extendIntent)
        if(seconds != null) builder.setWhen(System.currentTimeMillis() + seconds * 1000)
        if(title != null) builder.setContentTitle(title)
        if(subtitle != null) builder.setContentText(subtitle)

        NotificationManagerCompat.from(context).notify(id, builder.build())
        return true
    }

    private fun pauseNotification(id: Int?, title: String?, subtitle: String?, actionTitle1: String?): Boolean{
        val id = id ?: NOTIFICATION_ID

        createNotificationChannel()

        val openIntent = createNotificationIntent(id, NOTIF_ACTION_OPEN, REQUEST_CODE_NOTIF_OPEN)
        val cancelIntent = createNotificationIntent(id, NOTIF_ACTION_CANCEL, REQUEST_CODE_NOTIF_CANCEL)
        val continueIntent = createNotificationIntent(id, NOTIF_ACTION_CONTINUE, REQUEST_CODE_NOTIF_CONTINUE)

        val builder = NotificationCompat.Builder(context, NOTIF_CHANNEL_ID)
                .setShowWhen(false)
                .setUsesChronometer(false)
                .setSmallIcon(R.drawable.ic_hourglass_full)
                .setContentIntent(openIntent)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Cancel".toUpperCase(), cancelIntent)

        if(actionTitle1 != null) builder.addAction(android.R.drawable.ic_media_play, "Continue".toUpperCase(), continueIntent)
        if(title != null) builder.setContentTitle(title)
        if(subtitle != null) builder.setContentText(subtitle)

        NotificationManagerCompat.from(context).notify(id, builder.build())
        return true
    }

    private fun cancelNotification(id: Int?): Boolean {
        val id = id ?: NOTIFICATION_ID

        createNotificationChannel()
        
        NotificationManagerCompat.from(context).cancel(id)
        return true
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIF_CHANNEL_ID, "Active timer", importance)
            channel.description = "Notify about running or pausing timers"
            // Register the channel with the system
            NotificationManagerCompat.from(context).createNotificationChannel(channel)
        }
    }

    private fun createNotificationIntent(id: Int, action: String, requestCode: Int): PendingIntent {
        val intent = Intent(context, NotificationBroadcastReceiver::class.java).apply{
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_ENABLE_ADMIN -> {
                if (resultCode == RESULT_OK) {
                    result?.success(true)
                }
            }
        }
        return
    }
}