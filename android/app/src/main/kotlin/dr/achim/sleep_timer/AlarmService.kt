package dr.achim.sleep_timer

import android.app.*
import android.content.Context
import android.content.Intent
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
import dr.achim.sleep_timer.Messages.TimeNotificationRequest
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

    private lateinit var sensorManager: SensorManager

    private var shakeDetector: ShakeDetector? = null
    private var accelerometer: Sensor? = null
    private var pendingAlarmIntent: PendingIntent? = null

    override fun onCreate() {
        super.onCreate()
        initNotificationChannel()
        initShakeDetector()
        val notification = NotificationCompat.Builder(this, NotificationReceiver.NOTIFICATION_CHANNEL_ID).build()
        startForeground(NotificationReceiver.NOTIFICATION_ID, notification)
        Log.wtf(TAG, "AlarmService onCreate")
    }

    private fun initNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(NotificationReceiver.NOTIFICATION_CHANNEL_ID, "Active timer", importance)
            notificationChannel.description = "Notify about running or pausing timers"
            NotificationManagerCompat.from(this).createNotificationChannel(notificationChannel)
        }
    }

    private fun initShakeDetector() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        shakeDetector = ShakeDetector()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        isRunning = true
        Log.wtf(TAG, intent.action)
        when (intent.action) {
            ACTION_START -> {
                val map = intent.getSerializableExtra(NotificationReceiver.KEY_SHOW_NOTIFICATION) as HashMap<*, *>?
                val request: TimeNotificationRequest = TimeNotificationRequest.fromMap(map)
                startAlarm(request)
                val showRunningIntent = Intent(this, NotificationReceiver::class.java)
                showRunningIntent.action = NotificationReceiver.ACTION_SHOW_RUNNING
                showRunningIntent.putExtra(NotificationReceiver.KEY_SHOW_NOTIFICATION, map)
                sendBroadcast(showRunningIntent)
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

    private fun startAlarm(request: TimeNotificationRequest) {
        val alarmIntent = Intent(this, AlarmReceiver::class.java)
        alarmIntent.putExtra(NotificationReceiver.KEY_TIMER_ID, request.timerId)
        pendingAlarmIntent = PendingIntent.getBroadcast(this, REQUEST_CODE_ALARM, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, Calendar.getInstance().timeInMillis + request.remainingTime!! * 1000, pendingAlarmIntent)

        shakeDetector?.setOnShakeListener(object : OnShakeListener {
            override fun onShake(count: Int) {
                val intent = Intent(applicationContext, NotificationActionReceiver::class.java)
                intent.action = NotificationReceiver.ACTION_EXTEND

                val response = ExtendTimeResponse()
                response.timerId = request.timerId
                intent.putExtra(NotificationReceiver.KEY_EXTEND_RESPONSE, response.toMap())
                sendBroadcast(intent)

                val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    vibrator.vibrate(500)
                }
            }
        })

        sensorManager.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_UI)
    }

    private fun stopAlarm() {
        if (pendingAlarmIntent != null) {
            val manager = getSystemService(ALARM_SERVICE) as AlarmManager
            manager.cancel(pendingAlarmIntent)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        Log.wtf(TAG, "onDestroy Service")
        stopAlarm()
        sensorManager.unregisterListener(shakeDetector)
        isRunning = false
    }

}