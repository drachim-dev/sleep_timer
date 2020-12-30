package dr.achim.sleep_timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dr.achim.sleep_timer.Messages.FlutterTimerApi
import io.flutter.embedding.engine.FlutterEngineCache

class WidgetReceiver : BroadcastReceiver() {
    companion object {
        private val TAG = WidgetReceiver::class.java.toString()
    }

    private lateinit var flutterTimerApi: FlutterTimerApi

    override fun onReceive(context: Context, intent: Intent) {
        initializeFlutter()
        Log.d(TAG, "intent action: ${intent.action}")
        when (intent.action) {
            TimerWidget.ACTION_CODE_WIDGET_START_TIMER -> flutterTimerApi.onWidgetStartTimer { }
            TimerWidget.ACTION_CODE_WIDGET_SETTINGS -> { }
            else -> { }
        }
    }

    private fun initializeFlutter() {
        val flutterEngine = FlutterEngineCache.getInstance()[MainActivity.ENGINE_ID]
        if (flutterEngine != null) {
            flutterTimerApi = FlutterTimerApi(flutterEngine.dartExecutor.binaryMessenger)
        }
    }

}