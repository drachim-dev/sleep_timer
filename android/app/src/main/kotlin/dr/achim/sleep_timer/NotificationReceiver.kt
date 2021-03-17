package dr.achim.sleep_timer

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dr.achim.sleep_timer.Messages.*
import java.util.*
import kotlin.collections.HashMap

class NotificationReceiver : BroadcastReceiver() {
    companion object {
        private val TAG = AlarmService::class.java.toString()
        const val NOTIFICATION_CHANNEL_ID = "1"
        const val NOTIFICATION_ID = 1
        const val ACTION_SHOW_RUNNING = "ACTION_SHOW_RUNNING"
        const val ACTION_PAUSE_NOTIFICATION = "ACTION_PAUSE_NOTIFICATION"
        const val ACTION_ELAPSED_NOTIFICATION = "ACTION_ELAPSED_NOTIFICATION"
        const val ACTION_EXTEND = "ACTION_EXTEND"
        const val ACTION_COUNTDOWN = "ACTION_COUNTDOWN"
        const val ACTION_CONTINUE_REQUEST = "ACTION_CONTINUE_REQUEST"
        const val ACTION_PAUSE_REQUEST = "ACTION_PAUSE_REQUEST"
        const val ACTION_RESTART_REQUEST = "ACTION_RESTART_REQUEST"
        const val ACTION_CANCEL_REQUEST = "ACTION_CANCEL_REQUEST"
        const val KEY_SHOW_NOTIFICATION = "KEY_SHOW_NOTIFICATION"
        const val KEY_PAUSE_NOTIFICATION = "KEY_PAUSE_NOTIFICATION"
        const val KEY_ELAPSED_NOTIFICATION = "KEY_ELAPSED_NOTIFICATION"
        const val KEY_OPEN_REQUEST = "KEY_OPEN_REQUEST"
        const val KEY_CONTINUE_REQUEST = "KEY_CONTINUE_REQUEST"
        const val KEY_PAUSE_REQUEST = "KEY_PAUSE_REQUEST"
        const val KEY_CANCEL_REQUEST = "KEY_CANCEL_REQUEST"
        const val KEY_EXTEND_RESPONSE = "KEY_EXTEND_RESPONSE"
        const val KEY_COUNTDOWN_REQUEST = "KEY_COUNTDOWN_REQUEST"
        const val KEY_RESTART_REQUEST = "KEY_RESTART_REQUEST"
        const val KEY_TIMER_ID = "KEY_TIMER_ID"
        private const val REQUEST_CODE_OPEN = 100
        private const val REQUEST_CODE_EXTEND = 500
        private const val REQUEST_CODE_CONTINUE_REQUEST = 200
        private const val REQUEST_CODE_PAUSE_REQUEST = 300
        private const val REQUEST_CODE_RESTART_REQUEST = 150
        private const val REQUEST_CODE_CANCEL_REQUEST = 400
    }

    private var context: Context? = null

    override fun onReceive(context: Context, intent: Intent) {
        this.context = context
        Log.d(TAG, "intent action: ${intent.action}")
        val map: HashMap<*, *>?
        when (intent.action) {
            ACTION_SHOW_RUNNING -> {
                map = intent.getSerializableExtra(KEY_SHOW_NOTIFICATION) as HashMap<String, Any?>?
                showRunningNotification(TimeNotificationRequest.fromMap(map))
            }
            ACTION_PAUSE_NOTIFICATION -> {
                map = intent.getSerializableExtra(KEY_PAUSE_NOTIFICATION) as HashMap<String, Any?>?
                showPausingNotification(TimeNotificationRequest.fromMap(map))
            }
            ACTION_ELAPSED_NOTIFICATION -> {
                map = intent.getSerializableExtra(KEY_ELAPSED_NOTIFICATION) as HashMap<String, Any?>?
                showElapsedNotification(NotificationRequest.fromMap(map))
            }
        }
    }

    private fun showNotification(notification: Notification) {
        NotificationManagerCompat.from(context!!).notify(NOTIFICATION_ID, notification)
    }

    private fun showRunningNotification(request: TimeNotificationRequest) {
        val timerId = request.timerId

        val builder = NotificationCompat.Builder(context!!, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(request.title)
                .setContentText(request.description)
                .setSmallIcon(R.drawable.ic_hourglass_full)
                .setContentIntent(createOpenIntent(timerId))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setShowWhen(true)
                .setUsesChronometer(true)
                .setOngoing(true)
                .setAutoCancel(false)
                .setWhen(System.currentTimeMillis() + request.remainingTime!! * 1000)
        val actions = buildNotificationActions(timerId, request.restartAction, request.pauseAction, request.continueAction, request.cancelAction, request.extendActions)
        for (action in actions) {
            builder.addAction(action)
        }
        val notification = builder.build()
        showNotification(notification)
    }

    private fun buildNotificationActions(timerId: String?, restartAction: String?, pauseAction: String?, continueAction: String?, cancelAction: String?, extendActions: MutableList<Any>?): List<NotificationCompat.Action> {
        val actions: MutableList<NotificationCompat.Action> = ArrayList()
        if (restartAction != null && restartAction.isNotEmpty()) {
            val intent = createRestartRequestIntent(timerId)
            val action = NotificationCompat.Action(R.drawable.ic_baseline_replay_24, restartAction.toUpperCase(Locale.getDefault()), intent)
            actions.add(action)
        }
        if (pauseAction != null && pauseAction.isNotEmpty()) {
            val intent = createPauseRequestIntent(timerId)
            val action = NotificationCompat.Action(R.drawable.ic_baseline_pause_24, pauseAction.toUpperCase(Locale.getDefault()), intent)
            actions.add(action)
        }
        if (continueAction != null && continueAction.isNotEmpty()) {
            val intent = createContinueRequestIntent(timerId)
            val action = NotificationCompat.Action(R.drawable.ic_baseline_replay_24, continueAction.toUpperCase(Locale.getDefault()), intent)
            actions.add(action)
        }
        if (cancelAction != null && cancelAction.isNotEmpty()) {
            val intent = createCancelRequestIntent(timerId)
            val action = NotificationCompat.Action(R.drawable.ic_baseline_clear_24, cancelAction.toUpperCase(Locale.getDefault()), intent)
            actions.add(action)
        }
        if(extendActions != null) {
            for (extendAction in extendActions as MutableList<Int>) {
                val intent = createExtendTimeIntent(timerId, extendAction * 60)
                val action = NotificationCompat.Action(R.drawable.ic_baseline_replay_5_24, "+$extendAction", intent)
                actions.add(action)
            }
        }
        return actions
    }

    private fun showPausingNotification(request: TimeNotificationRequest) {
        val timerId = request.timerId

        val builder = NotificationCompat.Builder(context!!, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(request.title)
                .setContentText(request.description)
                .setSmallIcon(R.drawable.ic_hourglass_full)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setShowWhen(false)
                .setUsesChronometer(false)
                .setContentIntent(createOpenIntent(timerId))
        val actions = buildNotificationActions(timerId, request.restartAction, request.pauseAction, request.continueAction, request.cancelAction, request.extendActions)
        for (action in actions) {
            builder.addAction(action)
        }
        val notification = builder.build()
        showNotification(notification)
    }

    private fun showElapsedNotification(request: NotificationRequest) {
        val timerId = request.timerId

        val builder = NotificationCompat.Builder(context!!, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(request.title)
                .setContentText(request.description)
                .setStyle(NotificationCompat.BigTextStyle()
                        .bigText(request.description))
                .setSmallIcon(R.drawable.ic_hourglass_full)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setShowWhen(false)
                .setUsesChronometer(false)
                .setContentIntent(createOpenIntent(timerId))
        val actions = buildNotificationActions(timerId, request.restartAction, request.pauseAction, request.continueAction, request.cancelAction, request.extendActions)
        for (action in actions) {
            builder.addAction(action)
        }
        val notification = builder.build()
        showNotification(notification)
    }

    private fun createOpenIntent(timerId: String?): PendingIntent {
        val request = OpenRequest().apply {
            this.timerId = timerId
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(KEY_OPEN_REQUEST, request.toMap() as HashMap)
        }

        return PendingIntent.getActivity(
                context,
                REQUEST_CODE_OPEN,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun createContinueRequestIntent(timerId: String?): PendingIntent {
        val request = TimerRequest().apply {
            this.timerId = timerId
        }

        val intent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_CONTINUE_REQUEST
            putExtra(KEY_CONTINUE_REQUEST, request.toMap() as HashMap)
        }

        return PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_CONTINUE_REQUEST,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun createPauseRequestIntent(timerId: String?): PendingIntent {
        val request = TimerRequest().apply {
            this.timerId = timerId
        }

        val intent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_PAUSE_REQUEST
            putExtra(KEY_PAUSE_REQUEST, request.toMap() as HashMap)
        }

        return PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_PAUSE_REQUEST,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun createCancelRequestIntent(timerId: String?): PendingIntent {
        val response = CancelResponse().apply {
            this.timerId = timerId
            this.success = true
        }

        val intent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_CANCEL_REQUEST
            putExtra(KEY_CANCEL_REQUEST, response.toMap() as HashMap)
        }

        return PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_CANCEL_REQUEST,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun createExtendTimeIntent(timerId: String?, additionalTime: Int): PendingIntent {
        val response = ExtendTimeResponse().apply {
            this.timerId = timerId
            this.additionalTime = additionalTime.toLong()
        }

        val intent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_EXTEND
            putExtra(KEY_EXTEND_RESPONSE, response.toMap() as HashMap)
        }
        return PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_EXTEND + additionalTime,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun createRestartRequestIntent(timerId: String?): PendingIntent {
        val request = TimerRequest().apply {
            this.timerId = timerId
        }

        val intent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_RESTART_REQUEST
            putExtra(KEY_RESTART_REQUEST, request.toMap() as HashMap)
        }

        return PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_RESTART_REQUEST,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT)
    }

}