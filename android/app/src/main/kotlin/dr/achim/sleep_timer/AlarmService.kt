package dr.achim.sleep_timer

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dr.achim.sleep_timer.Messages.ExtendTimeRequest
import dr.achim.sleep_timer.Messages.RunningNotificationRequest
import dr.achim.sleep_timer.ShakeDetector.OnShakeListener
import java.util.*
import kotlin.math.max

class AlarmService : Service() {

    companion object {
        private val TAG = AlarmService::class.java.toString()
        var isRunning = false
        const val ACTION_START = "ACTION_START"
        const val ACTION_TOGGLE_EXTEND_BY_SHAKE = "ACTION_TOGGLE_EXTEND_BY_SHAKE"
        const val KEY_ENABLE_EXTEND_BY_SHAKE = "KEY_ENABLE_EXTEND_BY_SHAKE"
        private const val REQUEST_CODE_ALARM = 700
    }

    private var timerId: String? = null
    private var timer: Timer = Timer()
    private var notification: Notification? = null

    private var sensorManager: SensorManager? = null
    private var shakeDetector: ShakeDetector? = null
    private var accelerometer: Sensor? = null
    private var pendingAlarmIntent: PendingIntent? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")

        if (notification == null) {
            initNotificationChannel()
            notification = NotificationCompat.Builder(this, NotificationReceiver.NOTIFICATION_CHANNEL_ID).build()
        }
        startForeground(NotificationReceiver.NOTIFICATION_ID, notification)

        if (packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER)) {
            initShakeDetector()
        }
    }

    private fun initNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_LOW
            val notificationChannel = NotificationChannel(NotificationReceiver.NOTIFICATION_CHANNEL_ID, "Active timer", importance)
            notificationChannel.description = "Notify about running or pausing timers"
            NotificationManagerCompat.from(this).createNotificationChannel(notificationChannel)
        }
    }

    private fun initShakeDetector() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (accelerometer != null) {
            shakeDetector = ShakeDetector()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        isRunning = true
        Log.d(TAG, "intent action: ${intent?.action}")
        when (intent?.action) {
            ACTION_START -> {
                val map = intent.getSerializableExtra(NotificationReceiver.KEY_SHOW_NOTIFICATION) as ArrayList<Any>?

                if (map != null) {
                    val request: RunningNotificationRequest = RunningNotificationRequest.fromList(map)
                    startAlarm(request)

                    timer.scheduleAtFixedRate(object : TimerTask() {
                        override fun run() {
                            val response = Messages.CountDownRequest().apply {
                                timerId = request.timerId

                                request.remainingTime = (request.remainingTime ?: 0L) - 1L
                                newTime = max(request.remainingTime!!, 0L)
                            }

                            val countDownIntent = Intent(applicationContext, NotificationActionReceiver::class.java).apply {
                                action = NotificationReceiver.ACTION_COUNTDOWN
                                putExtra(NotificationReceiver.KEY_COUNTDOWN_REQUEST, response.toList())
                            }
                            sendBroadcast(countDownIntent)

                            val showRunningIntent = Intent(applicationContext, NotificationReceiver::class.java).apply {
                                action = NotificationReceiver.ACTION_SHOW_RUNNING
                                putExtra(NotificationReceiver.KEY_SHOW_NOTIFICATION, request.toList())
                            }
                            sendBroadcast(showRunningIntent)
                        }
                    }, 0, 1000)
                }
            }

            ACTION_TOGGLE_EXTEND_BY_SHAKE -> {
                val enable = intent.getBooleanExtra(KEY_ENABLE_EXTEND_BY_SHAKE, false)

                if (isRunning) {
                    if (enable) {
                        startShakeListener(timerId)
                    } else {
                        sensorManager?.unregisterListener(shakeDetector)
                    }
                }
            }

            else -> {
                isRunning = false
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_STICKY
    }

    private fun startAlarm(request: RunningNotificationRequest) {
        val alarmIntent = Intent(this, AlarmReceiver::class.java)
        alarmIntent.putExtra(NotificationReceiver.KEY_TIMER_ID, request.timerId)

        val manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        pendingAlarmIntent = PendingIntent.getBroadcast(this, REQUEST_CODE_ALARM, alarmIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        pendingAlarmIntent?.let { intent ->
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || manager.canScheduleExactAlarms()) {
                manager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    Calendar.getInstance().timeInMillis + request.remainingTime!! * 1000,
                    intent,
                )
            } else {
                manager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    Calendar.getInstance().timeInMillis + request.remainingTime!! * 1000,
                    intent,
                )
            }
        }

        timerId = request.timerId
        if (request.shakeToExtend == true) {
            startShakeListener(timerId)
        }
    }

    private fun startShakeListener(timerId: String?) {
        shakeDetector?.setOnShakeListener(object : OnShakeListener {
            override fun onShake(count: Int) {
                val response = ExtendTimeRequest()
                response.timerId = timerId ?: this@AlarmService.timerId

                val intent = Intent(applicationContext, NotificationActionReceiver::class.java).apply {
                    action = NotificationReceiver.ACTION_EXTEND
                    putExtra(NotificationReceiver.KEY_EXTEND_RESPONSE, response.toList())
                }
                sendBroadcast(intent)

                val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                    vibratorManager.defaultVibrator
                } else {
                    @Suppress("DEPRECATION")
                    getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(500)
                }
            }
        })
        sensorManager?.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_UI)
    }

    private fun stopAlarm() {
        Log.d(TAG, "stopAlarm")
        pendingAlarmIntent?.let {
            val manager = getSystemService(ALARM_SERVICE) as AlarmManager
            manager.cancel(it)
        }
        timer.cancel()
        timerId = null
        sensorManager?.unregisterListener(shakeDetector)
        isRunning = false
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        stopAlarm()
        super.onDestroy()
    }

}