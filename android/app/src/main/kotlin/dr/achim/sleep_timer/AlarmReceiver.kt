package dr.achim.sleep_timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dr.achim.sleep_timer.Messages.FlutterTimerApi
import dr.achim.sleep_timer.Messages.TimerRequest
import io.flutter.embedding.engine.FlutterEngineCache

class AlarmReceiver : BroadcastReceiver() {
    companion object {
        private val TAG = AlarmReceiver::class.java.toString()
    }

    private lateinit var flutterTimerApi: FlutterTimerApi

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive()")
        initializeFlutter()
        val request = TimerRequest().apply {
            timerId = intent.getStringExtra(NotificationReceiver.KEY_TIMER_ID)
        }
        flutterTimerApi.onAlarm(request) { }
    }

    private fun initializeFlutter() {
        FlutterEngineCache.getInstance()[MainActivity.ENGINE_ID]?.let {
            flutterTimerApi = FlutterTimerApi(it.dartExecutor.binaryMessenger)
        }
    }

}