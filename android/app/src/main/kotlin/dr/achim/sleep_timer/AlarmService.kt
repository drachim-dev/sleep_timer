package dr.achim.sleep_timer

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dr.achim.sleep_timer.Messages.ExtendTimeResponse
import dr.achim.sleep_timer.Messages.RunningNotificationRequest
import dr.achim.sleep_timer.ShakeDetector.OnShakeListener
import java.util.*

class AlarmService : Service() {

    companion object {
        private val TAG = AlarmService::class.java.toString()
        var isRunning = false
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        private const val REQUEST_CODE_ALARM = 700
    }

    private var timer: Timer = Timer()
    private lateinit var notification: Notification

    private var sensorManager: SensorManager? = null
    private var shakeDetector: ShakeDetector? = null
    private var accelerometer: Sensor? = null
    private var pendingAlarmIntent: PendingIntent? = null

    override fun onCreate() {
        super.onCreate()
        initNotificationChannel()
        if(packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER)) {
            initShakeDetector()
        }
        notification = NotificationCompat.Builder(this, NotificationReceiver.NOTIFICATION_CHANNEL_ID).build()
        startForeground(NotificationReceiver.NOTIFICATION_ID, notification)
        Log.d(TAG, "AlarmService onCreate")
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
        if(accelerometer != null) {
            shakeDetector = ShakeDetector()
        }
    }



    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        isRunning = true
        Log.d(TAG, "intent action: ${intent?.action}")
        when (intent?.action) {
            ACTION_START -> {
                val map = intent.getSerializableExtra(NotificationReceiver.KEY_SHOW_NOTIFICATION) as HashMap<String, Any>?
                val request: RunningNotificationRequest = RunningNotificationRequest.fromMap(map)
                startAlarm(request)
                val showRunningIntent = Intent(this, NotificationReceiver::class.java).apply {
                    action = NotificationReceiver.ACTION_SHOW_RUNNING
                    putExtra(NotificationReceiver.KEY_SHOW_NOTIFICATION, map)
                }
                sendBroadcast(showRunningIntent)
                timer.scheduleAtFixedRate(object : TimerTask() {
                    override fun run() {
                        val response = Messages.CountDownRequest().apply {
                            timerId = request.timerId
                            newTime = request.remainingTime--
                        }
                        val countDownIntent = Intent(applicationContext, NotificationActionReceiver::class.java).apply {
                            action = NotificationReceiver.ACTION_COUNTDOWN
                            putExtra(NotificationReceiver.KEY_COUNTDOWN_REQUEST, response.toMap() as HashMap)
                        }
                        sendBroadcast(countDownIntent)
                    }
                }, 0, 1000)
            }
            ACTION_STOP -> {
                stopForeground(true)
                stopSelf()
                if (isRunning) {
                    isRunning = false
                    stopForeground(true)
                    stopSelf()
                }
            }
            else -> if (isRunning) {
                isRunning = false
                stopForeground(true)
                stopSelf()
            }
        }
        return START_STICKY
    }

    private fun startAlarm(request: RunningNotificationRequest) {
        val alarmIntent = Intent(this, AlarmReceiver::class.java)
        alarmIntent.putExtra(NotificationReceiver.KEY_TIMER_ID, request.timerId)
        pendingAlarmIntent = PendingIntent.getBroadcast(this, REQUEST_CODE_ALARM, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, Calendar.getInstance().timeInMillis + request.remainingTime!! * 1000, pendingAlarmIntent)

        if(request.shakeToExtend != null && request.shakeToExtend) {
            shakeDetector?.setOnShakeListener(object : OnShakeListener {
                override fun onShake(count: Int) {
                    val response = ExtendTimeResponse()
                    response.timerId = request.timerId

                    val intent = Intent(applicationContext, NotificationActionReceiver::class.java).apply {
                        action = NotificationReceiver.ACTION_EXTEND
                        putExtra(NotificationReceiver.KEY_EXTEND_RESPONSE, response.toMap() as HashMap)
                    }
                    sendBroadcast(intent)

                    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        vibrator.vibrate(500)
                    }
                }
            })
        }


        sensorManager?.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_UI)
    }

    private fun stopAlarm() {
        if (pendingAlarmIntent != null) {
            val manager = getSystemService(ALARM_SERVICE) as AlarmManager
            manager.cancel(pendingAlarmIntent)
            timer.cancel()
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy Service")
        stopAlarm()
        timer.cancel()
        sensorManager?.unregisterListener(shakeDetector)
        isRunning = false
        super.onDestroy()
    }

}