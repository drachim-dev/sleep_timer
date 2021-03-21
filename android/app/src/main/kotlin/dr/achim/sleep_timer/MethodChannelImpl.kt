package dr.achim.sleep_timer

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.AlarmClock
import android.provider.MediaStore
import android.util.Base64
import androidx.core.app.NotificationManagerCompat
import dr.achim.sleep_timer.Messages.*
import io.flutter.Log
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.HashMap


class MethodChannelImpl(private val context: Context) : HostTimerApi {
    companion object {
        private val TAG = MethodChannelImpl::class.java.toString()
    }

    override fun init(arg: InitializationRequest) {
        Log.d(TAG, "init")
        CallbackHelper.setHandle(context, arg.callbackHandle)
    }

    private fun startForegroundService(arg: RunningNotificationRequest) {
        val intent = Intent(context, AlarmService::class.java).apply {
            action = AlarmService.ACTION_START
            putExtra(NotificationReceiver.KEY_SHOW_NOTIFICATION, arg.toMap() as HashMap)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    private fun stopForegroundService() {
        if (AlarmService.isRunning) {
            val intent = Intent(context, AlarmService::class.java)
            context.stopService(intent)
        }
    }

    override fun showRunningNotification(arg: RunningNotificationRequest): NotificationResponse {
        stopForegroundService()
        val response = NotificationResponse().apply {
            timerId = arg.timerId
            success = true
        }

        if (response.timerId == null) {
            return response.apply { success = false }
        }

        Log.d(TAG, "Request to show running notification for timer with id ${response.timerId}")
        startForegroundService(arg)
        // Notification will be triggered by broadcast when foreground service is ready
        return response
    }

    override fun showPausingNotification(arg: TimeNotificationRequest): NotificationResponse {
        val response = NotificationResponse().apply {
            timerId = arg.timerId
            success = true
        }

        if (response.timerId == null) {
            return response.apply { success = false }
        }

        Log.d(TAG, "Request to show pause notification for timer with id ${response.timerId}")
        stopForegroundService()
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = NotificationReceiver.ACTION_PAUSE_NOTIFICATION
            putExtra(NotificationReceiver.KEY_PAUSE_NOTIFICATION, arg.toMap() as HashMap)
        }
        context.sendBroadcast(intent)

        return response
    }

    override fun showElapsedNotification(arg: NotificationRequest): NotificationResponse {
        val response = NotificationResponse().apply {
            timerId = arg.timerId
            success = true
        }

        if (response.timerId == null) {
            return response.apply { success = false }
        }

        Log.d(TAG, "Request to show elapsed notification for timer with id ${response.timerId}")
        stopForegroundService()
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = NotificationReceiver.ACTION_ELAPSED_NOTIFICATION
            putExtra(NotificationReceiver.KEY_ELAPSED_NOTIFICATION, arg.toMap() as HashMap)
        }
        context.sendBroadcast(intent)

        return response
    }

    override fun cancelTimer(arg: CancelRequest): CancelResponse {
        val response = CancelResponse().apply {
            timerId = arg.timerId
            success = true
        }

        if (response.timerId == null) {
            return response.apply { success = false }
        }
        Log.d(TAG, "Request to cancel notification for timer with id ${response.timerId}")
        stopForegroundService()
        NotificationManagerCompat.from(context).cancel(NotificationReceiver.NOTIFICATION_ID)

        return response
    }

    override fun getInstalledPlayerApps() : InstalledAppsResponse {
        val manager = context.packageManager
        val uriAudio = Uri.withAppendedPath(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, "1")
        val intentAudio = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uriAudio, "audio/*")
        }
        val audioList = manager.queryIntentActivities(intentAudio, 0)

        val uriVideo = Uri.withAppendedPath(MediaStore.Video.Media.INTERNAL_CONTENT_URI, "1")
        val intentVideo = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uriVideo, "video/*")
        }
        val videoList = manager.queryIntentActivities(intentVideo, 0)

        val intentMedia = Intent(Intent.ACTION_MEDIA_BUTTON)
        val mediaList = manager.queryBroadcastReceivers(intentMedia, 0)
        val apps: Set<Package> = getAppsFromResolveInfo(audioList)
                .union(getAppsFromResolveInfo(videoList))
                .union(getAppsFromResolveInfo(mediaList))

        val packageNames: List<String> =
                arrayListOf("com.netflix.mediaclient", "com.google.android.apps.podcasts")
        for (packageName in packageNames) {
            try {
                val myPackage = manager.getPackageInfo(packageName, 0)
                val applicationInfo = myPackage.applicationInfo
                if (applicationInfo != null && applicationInfo.enabled) {
                    val app = getAppFromApplicationInfo(applicationInfo)
                    apps.plus(app)
                }
            } catch (ignored: PackageManager.NameNotFoundException) { }
        }

        val packages = apps.distinctBy { it.packageName }.mapNotNull { it.toMap() }.toList()
        return InstalledAppsResponse().apply {
            this.apps = packages
        }
    }

    override fun getInstalledAlarmApps() : InstalledAppsResponse {
        val showAlarmsIntent = Intent(AlarmClock.ACTION_SHOW_ALARMS)
        val alarmList = context.packageManager.queryIntentActivities(showAlarmsIntent, 0)
        val apps = getAppsFromResolveInfo(alarmList).distinctBy { it.packageName }

        val packages = apps.mapNotNull { it.toMap() }.toList()
        return InstalledAppsResponse().apply {
            this.apps = packages
        }
    }

    private fun getAppsFromResolveInfo(infoList: List<ResolveInfo>): List<Package> {
        val apps: MutableList<Package> = ArrayList()
        for (info in infoList) {
            val applicationInfo = info.activityInfo.applicationInfo
            val app = getAppFromApplicationInfo(applicationInfo)
            apps.add(app)
        }
        return apps
    }

    private fun getAppFromApplicationInfo(applicationInfo: ApplicationInfo): Package {
        val manager = context.packageManager

        // https://stackoverflow.com/questions/51368075/how-can-i-get-android-drawables-in-flutter
        val bitmap = getBitmapFromDrawable(applicationInfo.loadIcon(manager))
        val encoded = getBase64FromBitmap(bitmap)
        return Package().apply {
            packageName = applicationInfo.packageName
            title = applicationInfo.loadLabel(manager).toString()
            icon = encoded
        }
    }

    override fun launchApp(request: LaunchAppRequest) {
        val manager = context.packageManager
        val intent = manager.getLaunchIntentForPackage(request.packageName!!)
        context.startActivity(intent)
    }

    // https://stackoverflow.com/questions/44447056/convert-adaptiveicondrawable-to-bitmap-in-android-o-preview
    private fun getBitmapFromDrawable(drawable: Drawable): Bitmap {
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    // https://stackoverflow.com/questions/9224056/android-bitmap-to-base64-string/9224180#9224180
    private fun getBase64FromBitmap(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    override fun dummyApp(): Package? {
        return null
    }

}