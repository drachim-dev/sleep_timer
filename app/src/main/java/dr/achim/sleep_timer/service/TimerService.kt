package dr.achim.sleep_timer.service

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
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
import dr.achim.sleep_timer.receiver.TimerReceiver
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import kotlin.time.Duration.Companion.seconds

class TimerService : LifecycleService() {

    private val manageTimerActionsUseCase: ManageTimerActionsUseCase by inject()
    private val getSettingsUseCase: GetSettingsUseCase by inject()
    private val sensorManager: SensorManager by inject()
    private val vibrator: Vibrator by inject()
    private val timerRepository: TimerRepository by inject()
    private val timerActionExecutor: TimerActionExecutor by inject()
    private var timerJob: Job? = null
    private lateinit var shakeDetector: ShakeDetector

    private var targetTimeMillis: Long = 0L
    private var isFinishing = false

    private val settingsFlow = getSettingsUseCase().stateIn(
        scope = lifecycleScope,
        started = SharingStarted.Eagerly,
        initialValue = AppSettings()
    )

    private val notificationManager by lazy {
        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    private val alarmManager by lazy {
        getSystemService(ALARM_SERVICE) as AlarmManager
    }

    private var currentActions: TimerActions = TimerActions()

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        observeTimerActions()
        setupShakeDetector()
    }

    private fun setupShakeDetector() {
        shakeDetector = ShakeDetector(
            onShakeDetected = {
                extendTimer(settingsFlow.value.extendOnShakeMinutes)
                vibrate()
            },
        )

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

    private fun observeTimerActions() {
        lifecycleScope.launch {
            manageTimerActionsUseCase.observeTimerActions().collect { actions ->
                currentActions = actions
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
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
                // Ensure service is in foreground if triggered while app/service is killed
                // and to guarantee completion of end actions without system throttling.
                ensureForeground()
                onTimerFinished()
            }
        }
        return START_NOT_STICKY
    }

    private fun startTimer(durationMillis: Long) {
        isFinishing = false
        timerJob?.cancel()
        timerRepository.setTotalTime(durationMillis)
        timerRepository.setRemainingTime(durationMillis)
        timerRepository.setRunning(true)

        lifecycleScope.launch {
            // Ensure actions are loaded from persistence before applying
            val actions = manageTimerActionsUseCase.observeTimerActions().first()
            currentActions = actions
            timerActionExecutor.applyStartActions(actions.startActions)
        }

        targetTimeMillis = SystemClock.elapsedRealtime() + durationMillis
        scheduleAlarm(durationMillis)

        ensureForeground()

        timerJob = lifecycleScope.launch {
            while (true) {
                if (timerRepository.timerState.value is TimerState.Running) {
                    val remaining = targetTimeMillis - SystemClock.elapsedRealtime()
                    if (remaining <= 0) {
                        timerRepository.setRemainingTime(0)
                        onTimerFinished()
                        break
                    }
                    timerRepository.setRemainingTime(remaining)
                    updateNotification()
                }
                delay(1.seconds)
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
        val remaining = timerRepository.timerState.value.remainingTimeMillis
        targetTimeMillis = SystemClock.elapsedRealtime() + remaining
        scheduleAlarm(remaining)
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
            targetTimeMillis = SystemClock.elapsedRealtime() + newRemaining
            scheduleAlarm(newRemaining)
        }
        updateNotification()
    }


    private fun vibrate() {
        val effect = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            vibrator.areAllPrimitivesSupported(
                VibrationEffect.Composition.PRIMITIVE_CLICK
            )
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
        timerRepository.setRunning(false)
        timerRepository.setRemainingTime(0)
        cancelAlarm()
    }

    private fun onTimerFinished() {
        if (isFinishing) return
        isFinishing = true
        stopTimer()
        lifecycleScope.launch {
            timerActionExecutor.applyEndActions(currentActions.endActions)
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
        val triggerAtMillis = SystemClock.elapsedRealtime() + remainingMillis

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            // Fallback to non-exact if permission is not granted
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
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

        val extend5Intent =
            Intent(this, TimerService::class.java).apply { action = ACTION_EXTEND_5 }
        val extend5PendingIntent =
            PendingIntent.getService(this, 4, extend5Intent, PendingIntent.FLAG_IMMUTABLE)
        val extend5Action = NotificationCompat.Action.Builder(
            android.R.drawable.ic_input_add,
            getString(R.string.notification_action_extend_5),
            extend5PendingIntent
        ).build()

        val extend20Intent =
            Intent(this, TimerService::class.java).apply { action = ACTION_EXTEND_20 }
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

    private fun ensureForeground() {
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
        sensorManager.unregisterListener(shakeDetector)
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
