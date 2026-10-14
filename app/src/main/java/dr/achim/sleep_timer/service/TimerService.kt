package dr.achim.sleep_timer.service

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.hardware.Sensor
import android.hardware.SensorManager
import android.media.AudioAttributes
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.os.VibrationAttributes
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import dr.achim.sleep_timer.MainActivity
import dr.achim.sleep_timer.R
import dr.achim.sleep_timer.common.ShakeDetector
import dr.achim.sleep_timer.common.TAG
import dr.achim.sleep_timer.data.TimerActionExecutor
import dr.achim.sleep_timer.domain.repository.TimerRepository
import dr.achim.sleep_timer.domain.usecase.GetSettingsUseCase
import dr.achim.sleep_timer.domain.usecase.ManageTimerActionsUseCase
import dr.achim.sleep_timer.model.AppSettings
import dr.achim.sleep_timer.model.TimerActions
import dr.achim.sleep_timer.model.TimerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class TimerService : LifecycleService() {

    private val manageTimerActionsUseCase: ManageTimerActionsUseCase by inject()
    private val getSettingsUseCase: GetSettingsUseCase by inject()
    private val sensorManager: SensorManager by inject()
    private val vibrator: Vibrator by inject()
    private val timerRepository: TimerRepository by inject()
    private val timerActionExecutor: TimerActionExecutor by inject()

    private var timerJob: Job? = null
    private var lightsOffDelayJob: Job? = null
    private lateinit var shakeDetector: ShakeDetector

    private var targetTimeMillis: Long = 0L
    private var isFinishing = false

    private val alarmReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == ACTION_EXPIRED) {
                onTimerFinished()
            }
        }
    }

    private val isTimerActive: Boolean
        get() = timerRepository.timerState.value.isActive

    private val settingsFlow = getSettingsUseCase().stateIn(
        scope = lifecycleScope,
        started = SharingStarted.Eagerly,
        initialValue = AppSettings()
    )

    private val actionsFlow = manageTimerActionsUseCase.observeTimerActions().stateIn(
        scope = lifecycleScope,
        started = SharingStarted.Eagerly,
        initialValue = TimerActions()
    )

    private val notificationManager by lazy {
        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    private val alarmManager by lazy {
        getSystemService(ALARM_SERVICE) as AlarmManager
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        setupShakeDetector()

        lifecycleScope.launch {
            timerRepository.timerState
                .map { it.isActive }
                .dropWhile { !it } // Ignore initial Idle state before ACTION_START runs
                .distinctUntilChanged()
                .collect { active ->
                    if (active) {
                        promoteToForeground()
                    } else if (!isFinishing) {
                        stopSelf()
                    }
                }
        }

        ContextCompat.registerReceiver(
            this,
            alarmReceiver,
            IntentFilter(ACTION_EXPIRED),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    private fun setupShakeDetector() {
        shakeDetector = ShakeDetector {
            extendTimer(settingsFlow.value.extendOnShakeMinutes)
            vibrate()
        }

        lifecycleScope.launch {
            combine(
                settingsFlow.map { it.extendOnShake }.distinctUntilChanged(),
                timerRepository.timerState.map { it is TimerState.Running }.distinctUntilChanged()
            ) { extendOnShake, isRunning ->
                extendOnShake && isRunning
            }
                .distinctUntilChanged()
                .collect { shouldListen ->
                    if (shouldListen) {
                        val accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
                        if (accel == null) {
                            Log.e(TAG, "Accelerometer not available — shake-to-extend disabled")
                            return@collect
                        }
                        sensorManager.registerListener(
                            shakeDetector,
                            accel,
                            SensorManager.SENSOR_DELAY_NORMAL
                        )
                    } else {
                        sensorManager.unregisterListener(shakeDetector)
                    }
                }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        if (!promoteToForeground()) {
            stopSelf()
            return START_NOT_STICKY
        }

        handleAction(intent)

        if (!isTimerActive && !isFinishing) {
            stopSelf()
        }

        return START_NOT_STICKY
    }

    private fun handleAction(intent: Intent?) {
        val action = intent?.action ?: return

        when (action) {
            ACTION_START -> {
                val duration = intent.getLongExtra(EXTRA_DURATION_MILLIS, 0L)
                if (duration > 0L) startTimer(duration)
            }
            ACTION_STOP -> stopTimer()
            ACTION_PAUSE -> if (isTimerActive) pauseTimer()
            ACTION_RESUME -> if (isTimerActive) resumeTimer()
            ACTION_EXTEND_5 -> if (isTimerActive) extendTimer(5)
            ACTION_EXTEND_20 -> if (isTimerActive) extendTimer(20)
        }
    }

    private fun startTimer(durationMillis: Long) {
        isFinishing = false
        timerJob?.cancel()
        lightsOffDelayJob?.cancel()

        timerRepository.setLightsOffDelayProgress(0f)
        timerRepository.setTotalTime(durationMillis)
        timerRepository.setRemainingTime(durationMillis)
        timerRepository.setRunning(true)

        targetTimeMillis = SystemClock.elapsedRealtime() + durationMillis
        scheduleAlarm(durationMillis)
        updateNotification()

        lifecycleScope.launch {
            val actions = manageTimerActionsUseCase.observeTimerActions().first()
            timerActionExecutor.applyStartActions(actions.startActions)

            if (actions.startActions.hueLights && settingsFlow.value.lightsOffDelay) {
                startLightsOffDelay(settingsFlow.value.lightsOffDelaySeconds)
            }
        }

        timerJob = lifecycleScope.launch {
            while (isActive) {
                if (timerRepository.timerState.value is TimerState.Running) {
                    val remaining = (targetTimeMillis - SystemClock.elapsedRealtime()).coerceAtLeast(0L)
                    timerRepository.setRemainingTime(remaining)

                    if (remaining == 0L) {
                        onTimerFinished()
                        break
                    }
                    updateNotification()
                }
                delay(1.seconds)
            }
        }
    }

    private fun pauseTimer() {
        if (timerRepository.timerState.value !is TimerState.Running) return

        val exactRemaining = (targetTimeMillis - SystemClock.elapsedRealtime()).coerceAtLeast(0L)
        timerRepository.setRemainingTime(exactRemaining)
        timerRepository.setPaused(true)
        cancelAlarm()
        updateNotification()
    }

    private fun resumeTimer() {
        if (timerRepository.timerState.value !is TimerState.Paused) return

        val remaining = timerRepository.timerState.value.remainingTimeMillis
        targetTimeMillis = SystemClock.elapsedRealtime() + remaining
        timerRepository.setPaused(false)
        scheduleAlarm(remaining)
        updateNotification()
    }

    private fun extendTimer(minutes: Int) {
        val extraMillis = minutes * 60_000L
        val state = timerRepository.timerState.value

        val currentRemaining = if (state is TimerState.Running) {
            (targetTimeMillis - SystemClock.elapsedRealtime()).coerceAtLeast(0L)
        } else {
            state.remainingTimeMillis
        }

        val newRemaining = currentRemaining + extraMillis
        val newTotal = state.totalTimeMillis + extraMillis

        timerRepository.setTotalTime(newTotal)
        timerRepository.setRemainingTime(newRemaining)

        if (state is TimerState.Running) {
            targetTimeMillis = SystemClock.elapsedRealtime() + newRemaining
            scheduleAlarm(newRemaining)
        }
        updateNotification()
    }

    private fun vibrate() {
        val effect = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            vibrator.areAllPrimitivesSupported(VibrationEffect.Composition.PRIMITIVE_CLICK)
        ) {
            VibrationEffect.startComposition()
                .addPrimitive(VibrationEffect.Composition.PRIMITIVE_CLICK, 1.0f)
                .addPrimitive(VibrationEffect.Composition.PRIMITIVE_CLICK, 1.0f, 60)
                .addPrimitive(VibrationEffect.Composition.PRIMITIVE_CLICK, 0.8f, 40)
                .compose()
        } else {
            VibrationEffect.createWaveform(
                longArrayOf(0, 60, 50, 50, 50, 40),
                intArrayOf(0, 180, 0, 150, 0, 120),
                -1
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val attributes = VibrationAttributes.Builder()
                .setUsage(VibrationAttributes.USAGE_HARDWARE_FEEDBACK)
                .build()
            vibrator.vibrate(effect, attributes)
        } else {
            val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            @Suppress("DEPRECATION")
            vibrator.vibrate(effect, attributes)
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        lightsOffDelayJob?.cancel()
        cancelAlarm()
        timerRepository.setLightsOffDelayProgress(0f)
        timerRepository.setRemainingTime(0L)
        timerRepository.setRunning(false)
    }

    private fun startLightsOffDelay(delaySeconds: Int) {
        lightsOffDelayJob?.cancel()
        if (delaySeconds <= 0) {
            lifecycleScope.launch { timerActionExecutor.applyHueStartActions() }
            return
        }

        lightsOffDelayJob = lifecycleScope.launch {
            val totalMillis = delaySeconds * 1000L
            var remainingMillis = totalMillis
            val stepMillis = 100L

            while (remainingMillis > 0 && isActive) {
                if (timerRepository.timerState.value is TimerState.Running) {
                    remainingMillis = (remainingMillis - stepMillis).coerceAtLeast(0L)
                    val progress = (remainingMillis.toFloat() / totalMillis).coerceIn(0f, 1f)
                    timerRepository.setLightsOffDelayProgress(progress)
                }
                delay(stepMillis.milliseconds)
            }
            timerRepository.setLightsOffDelayProgress(0f)
            timerActionExecutor.applyHueStartActions()
        }
    }

    private fun onTimerFinished() {
        if (isFinishing) return
        isFinishing = true

        lifecycleScope.launch {
            withContext(NonCancellable) {
                stopTimer()
                timerActionExecutor.applyEndActions(actionsFlow.value.endActions)
                ServiceCompat.stopForeground(this@TimerService, ServiceCompat.STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
    }

    private fun getAlarmPendingIntent(): PendingIntent {
        val intent = Intent(ACTION_EXPIRED).setPackage(packageName)
        return PendingIntent.getBroadcast(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun scheduleAlarm(remainingMillis: Long) {
        val pendingIntent = getAlarmPendingIntent()
        val triggerAtMillis = SystemClock.elapsedRealtime() + remainingMillis

        val canScheduleExact = Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
                alarmManager.canScheduleExactAlarms()

        if (canScheduleExact) {
            try {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
                return
            } catch (e: SecurityException) {
                Log.w(TAG, "Exact alarm permission revoked, falling back to inexact alarm", e)
            }
        }

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            triggerAtMillis,
            pendingIntent
        )
    }

    private fun cancelAlarm() {
        alarmManager.cancel(getAlarmPendingIntent())
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

    private fun getForegroundServicePendingIntent(requestCode: Int, actionStr: String) =
        PendingIntent.getForegroundService(
            this,
            requestCode,
            Intent(this, TimerService::class.java).apply { action = actionStr },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

    private fun createNotification(content: String): Notification {
        val openIntent = Intent(this, MainActivity::class.java).apply {
            action = ACTION_OPEN_TIMER
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentPendingIntent = PendingIntent.getActivity(
            this, 0, openIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val isPaused = timerRepository.timerState.value is TimerState.Paused
        val pauseResumeAction = if (isPaused) {
            NotificationCompat.Action.Builder(
                android.R.drawable.ic_media_play,
                getString(R.string.notification_action_resume),
                getForegroundServicePendingIntent(2, ACTION_RESUME)
            ).build()
        } else {
            NotificationCompat.Action.Builder(
                android.R.drawable.ic_media_pause,
                getString(R.string.notification_action_pause),
                getForegroundServicePendingIntent(3, ACTION_PAUSE)
            ).build()
        }

        val extend5Action = NotificationCompat.Action.Builder(
            android.R.drawable.ic_input_add,
            getString(R.string.notification_action_extend_5),
            getForegroundServicePendingIntent(4, ACTION_EXTEND_5)
        ).build()

        val extend20Action = NotificationCompat.Action.Builder(
            android.R.drawable.ic_input_add,
            getString(R.string.notification_action_extend_20),
            getForegroundServicePendingIntent(5, ACTION_EXTEND_20)
        ).build()

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentIntent(contentPendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .addAction(pauseResumeAction)
            .addAction(extend5Action)
            .addAction(extend20Action)
            .build()
    }

    private fun updateNotification() {
        notificationManager.notify(NOTIFICATION_ID, createNotification(getNotificationContent()))
    }

    private fun promoteToForeground(): Boolean = try {
        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
        } else 0

        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            createNotification(getNotificationContent()),
            type
        )
        true
    } catch (e: Exception) {
        Log.e(TAG, "Failed to promote to foreground", e)
        false
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
        sensorManager.unregisterListener(shakeDetector)
        unregisterReceiver(alarmReceiver)
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        super.onDestroy()
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
        const val ACTION_EXPIRED = "ACTION_EXPIRED"
        const val EXTRA_DURATION_MILLIS = "EXTRA_DURATION_MILLIS"
    }
}