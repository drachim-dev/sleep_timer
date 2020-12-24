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

class NotificationReceiver : BroadcastReceiver() {
    companion object {
        private val TAG = AlarmService::class.java.toString()
        const val NOTIFICATION_CHANNEL_ID = "1"
        const val NOTIFICATION_ID = 1
        const val ACTION_SHOW_RUNNING = "ACTION_SHOW_RUNNING"
        const val ACTION_PAUSE_NOTIFICATION = "ACTION_PAUSE_NOTIFICATION"
        const val ACTION_ELAPSED_NOTIFICATION = "ACTION_ELAPSED_NOTIFICATION"
        const val ACTION_EXTEND = "ACTION_EXTEND"
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
        Log.wtf(TAG, intent.action)
        val map: HashMap<*, *>?
        when (intent.action) {
            ACTION_SHOW_RUNNING -> {
                map = intent.getSerializableExtra(KEY_SHOW_NOTIFICATION) as HashMap<*, *>?
                showRunningNotification(TimeNotificationRequest.fromMap(map))
            }
            ACTION_PAUSE_NOTIFICATION -> {
                map = intent.getSerializableExtra(KEY_PAUSE_NOTIFICATION) as HashMap<*, *>?
                showPausingNotification(TimeNotificationRequest.fromMap(map))
            }
            ACTION_ELAPSED_NOTIFICATION -> {
                map = intent.getSerializableExtra(KEY_ELAPSED_NOTIFICATION) as HashMap<*, *>?
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
                .setPriority(NotificationCompat.PRIORITY_HIGH)
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

    private fun buildNotificationActions(timerId: String?, restartAction: String?, pauseAction: String?, continueAction: String?, cancelAction: String?, extendActions: ArrayList<*>?): List<NotificationCompat.Action> {
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
        if (extendActions != null) {
            for (extendAction in extendActions as ArrayList<Int>) {
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
        val intent = Intent(context, MainActivity::class.java)
        intent.action = Intent.ACTION_MAIN
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val request = OpenRequest()
        request.timerId = timerId
        intent.putExtra(KEY_OPEN_REQUEST, request.toMap())
        return PendingIntent.getActivity(
                context,
                REQUEST_CODE_OPEN,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun createContinueRequestIntent(timerId: String?): PendingIntent {
        val intent = Intent(context, NotificationActionReceiver::class.java)
        intent.action = ACTION_CONTINUE_REQUEST
        val request = TimerRequest()
        request.timerId = timerId
        intent.putExtra(KEY_CONTINUE_REQUEST, request.toMap())
        return PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_CONTINUE_REQUEST,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun createPauseRequestIntent(timerId: String?): PendingIntent {
        val intent = Intent(context, NotificationActionReceiver::class.java)
        intent.action = ACTION_PAUSE_REQUEST
        val request = TimerRequest()
        request.timerId = timerId
        intent.putExtra(KEY_PAUSE_REQUEST, request.toMap())
        return PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_PAUSE_REQUEST,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun createCancelRequestIntent(timerId: String?): PendingIntent {
        val intent = Intent(context, NotificationActionReceiver::class.java)
        intent.action = ACTION_CANCEL_REQUEST
        val response = CancelResponse()
        response.timerId = timerId
        response.success = true
        intent.putExtra(KEY_CANCEL_REQUEST, response.toMap())
        return PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_CANCEL_REQUEST,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun createExtendTimeIntent(timerId: String?, additionalTime: Int): PendingIntent {
        val intent = Intent(context, NotificationActionReceiver::class.java)
        intent.action = ACTION_EXTEND
        val response = ExtendTimeResponse()
        response.timerId = timerId
        response.additionalTime = additionalTime.toLong()
        intent.putExtra(KEY_EXTEND_RESPONSE, response.toMap())
        return PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_EXTEND + additionalTime,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun createRestartRequestIntent(timerId: String?): PendingIntent {
        val intent = Intent(context, NotificationActionReceiver::class.java)
        intent.action = ACTION_RESTART_REQUEST
        val request = TimerRequest()
        request.timerId = timerId
        intent.putExtra(KEY_RESTART_REQUEST, request.toMap())
        return PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_RESTART_REQUEST,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT)
    }

}