package dr.achim.sleep_timer.service

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import dr.achim.sleep_timer.MainActivity
import dr.achim.sleep_timer.R
import dr.achim.sleep_timer.data.TimerActionExecutor
import dr.achim.sleep_timer.domain.repository.TimerRepository
import dr.achim.sleep_timer.domain.usecase.ManageTimerActionsUseCase
import dr.achim.sleep_timer.model.TimerActions
import dr.achim.sleep_timer.model.TimerState
import dr.achim.sleep_timer.receiver.TimerReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import kotlin.time.Duration.Companion.seconds

class TimerService : Service() {

    private val manageTimerActionsUseCase: ManageTimerActionsUseCase by inject()
    private val timerRepository: TimerRepository by inject()
    private val timerActionExecutor: TimerActionExecutor by inject()
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var timerJob: Job? = null

    private val notificationManager by lazy {
        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    private val alarmManager by lazy {
        getSystemService(ALARM_SERVICE) as AlarmManager
    }

    private var currentActions: TimerActions = TimerActions()

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        observeTimerActions()
    }

    private fun observeTimerActions() {
        serviceScope.launch {
            manageTimerActionsUseCase.observeTimerActions().collect { actions ->
                currentActions = actions
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val durationMillis = intent.getLongExtra(EXTRA_DURATION_MILLIS, 0L)
                startTimer(durationMillis)
            }

            ACTION_STOP -> {
                stopTimer()
                stopSelf()
            }

            ACTION_PAUSE -> {
                pauseTimer()
            }

            ACTION_RESUME -> {
                resumeTimer()
            }

            ACTION_EXTEND_5 -> {
                extendTimer(5)
            }

            ACTION_EXTEND_20 -> {
                extendTimer(20)
            }

            ACTION_FINISH -> {
                onTimerFinished()
            }
        }
        return START_NOT_STICKY
    }

    private fun startTimer(durationMillis: Long) {
        timerJob?.cancel()
        timerRepository.setTotalTime(durationMillis)
        timerRepository.setRemainingTime(durationMillis)
        timerRepository.setRunning(true)

        serviceScope.launch {
            // Ensure actions are loaded from persistence before applying
            val actions = manageTimerActionsUseCase.observeTimerActions().first()
            currentActions = actions
            timerActionExecutor.applyStartActions(actions.startActions)
        }

        scheduleAlarm(durationMillis)

        val initialContent = getNotificationContent()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NOTIFICATION_ID,
                createNotification(initialContent),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(NOTIFICATION_ID, createNotification(initialContent))
        }

        timerJob = serviceScope.launch {
            while (timerRepository.timerState.value.remainingTimeMillis > 0) {
                delay(1.seconds)
                if (timerRepository.timerState.value !is TimerState.Paused) {
                    val newRemaining = timerRepository.timerState.value.remainingTimeMillis - 1000
                    timerRepository.setRemainingTime(maxOf(0, newRemaining))
                    updateNotification()
                }
            }
        }
    }

    private fun pauseTimer() {
        timerRepository.setPaused(true)
        cancelAlarm()
        updateNotification()
    }

    private fun resumeTimer() {
        timerRepository.setPaused(false)
        scheduleAlarm(timerRepository.timerState.value.remainingTimeMillis)
        updateNotification()
    }

    private fun extendTimer(minutes: Int) {
        val extraMillis = minutes * 60 * 1000L
        val currentRemaining = timerRepository.timerState.value.remainingTimeMillis
        val currentTotal = timerRepository.timerState.value.totalTimeMillis
        
        val newRemaining = currentRemaining + extraMillis
        val newTotal = currentTotal + extraMillis
        
        timerRepository.setTotalTime(newTotal)
        timerRepository.setRemainingTime(newRemaining)
        
        if (timerRepository.timerState.value !is TimerState.Paused) {
            scheduleAlarm(newRemaining)
        }
        updateNotification()
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerRepository.setRunning(false)
        timerRepository.setRemainingTime(0)
        cancelAlarm()
    }

    private fun onTimerFinished() {
        stopTimer()
        serviceScope.launch {
            timerActionExecutor.applyEndActions(currentActions.endActions, currentActions.startActions)
            stopSelf()
        }
    }

    private fun scheduleAlarm(remainingMillis: Long) {
        val intent = Intent(this, TimerReceiver::class.java).apply {
            action = TimerReceiver.ACTION_TIMER_EXPIRED
        }
        val pendingIntent = PendingIntent.getBroadcast(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val triggerAtMillis = System.currentTimeMillis() + remainingMillis

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            // Fallback to non-exact if permission is not granted
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        }
    }

    private fun cancelAlarm() {
        val intent = Intent(this, TimerReceiver::class.java).apply {
            action = TimerReceiver.ACTION_TIMER_EXPIRED
        }
        val pendingIntent = PendingIntent.getBroadcast(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(content: String): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            action = ACTION_OPEN_TIMER
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val isPaused = timerRepository.timerState.value is TimerState.Paused
        val pauseResumeAction = if (isPaused) {
            val resumeIntent =
                Intent(this, TimerService::class.java).apply { action = ACTION_RESUME }
            val resumePendingIntent =
                PendingIntent.getService(this, 2, resumeIntent, PendingIntent.FLAG_IMMUTABLE)
            NotificationCompat.Action.Builder(
                android.R.drawable.ic_media_play,
                getString(R.string.notification_action_resume),
                resumePendingIntent
            ).build()
        } else {
            val pauseIntent = Intent(this, TimerService::class.java).apply { action = ACTION_PAUSE }
            val pausePendingIntent =
                PendingIntent.getService(this, 3, pauseIntent, PendingIntent.FLAG_IMMUTABLE)
            NotificationCompat.Action.Builder(
                android.R.drawable.ic_media_pause,
                getString(R.string.notification_action_pause),
                pausePendingIntent
            ).build()
        }

        val extend5Intent = Intent(this, TimerService::class.java).apply { action = ACTION_EXTEND_5 }
        val extend5PendingIntent =
            PendingIntent.getService(this, 4, extend5Intent, PendingIntent.FLAG_IMMUTABLE)
        val extend5Action = NotificationCompat.Action.Builder(
            android.R.drawable.ic_input_add,
            getString(R.string.notification_action_extend_5),
            extend5PendingIntent
        ).build()

        val extend20Intent = Intent(this, TimerService::class.java).apply { action = ACTION_EXTEND_20 }
        val extend20PendingIntent =
            PendingIntent.getService(this, 5, extend20Intent, PendingIntent.FLAG_IMMUTABLE)
        val extend20Action = NotificationCompat.Action.Builder(
            android.R.drawable.ic_input_add,
            getString(R.string.notification_action_extend_20),
            extend20PendingIntent
        ).build()

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .addAction(pauseResumeAction)
            .addAction(extend5Action)
            .addAction(extend20Action)
            .build()
    }

    private fun updateNotification() {
        notificationManager.notify(NOTIFICATION_ID, createNotification(getNotificationContent()))
    }

    private fun getNotificationContent(): String {
        val state = timerRepository.timerState.value
        return if (state is TimerState.Paused) {
            getString(R.string.notification_paused_prefix, state.formattedTime)
        } else {
            state.formattedTime
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val CHANNEL_ID = "timer_channel"
        const val NOTIFICATION_ID = 1
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_RESUME = "ACTION_RESUME"
        const val ACTION_EXTEND_5 = "ACTION_EXTEND_5"
        const val ACTION_EXTEND_20 = "ACTION_EXTEND_20"
        const val ACTION_OPEN_TIMER = "ACTION_OPEN_TIMER"
        const val ACTION_FINISH = "ACTION_FINISH"
        const val EXTRA_DURATION_MILLIS = "EXTRA_DURATION_MILLIS"
    }
}
