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
        initializeFlutter()
        Log.e(TAG, "onReceive()")
        val timerId = intent.getStringExtra(NotificationReceiver.KEY_TIMER_ID)
        val request = TimerRequest()
        request.timerId = timerId
        flutterTimerApi.onAlarm(request) { }
    }

    private fun initializeFlutter() {
        val flutterEngine = FlutterEngineCache.getInstance()[MainActivity.ENGINE_ID]
        if (flutterEngine != null) {
            flutterTimerApi = FlutterTimerApi(flutterEngine.dartExecutor.binaryMessenger)
        }
    }


}