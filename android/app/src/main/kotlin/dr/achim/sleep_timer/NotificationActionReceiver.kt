package dr.achim.sleep_timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dr.achim.sleep_timer.Messages.*
import io.flutter.embedding.engine.FlutterEngineCache
import java.util.*

class NotificationActionReceiver : BroadcastReceiver() {
    private lateinit var flutterTimerApi: FlutterTimerApi

    companion object {
        private val TAG = NotificationActionReceiver::class.java.toString()
    }

    override fun onReceive(context: Context, intent: Intent) {
        initializeFlutter()
        Log.wtf(TAG, intent.action)
        val map: HashMap<String, Any>?
        when (intent.action) {
            NotificationReceiver.ACTION_CONTINUE_REQUEST -> {
                map = intent.getSerializableExtra(NotificationReceiver.KEY_CONTINUE_REQUEST) as HashMap<String, Any>?
                if (map != null) {
                    flutterTimerApi!!.onContinueRequest(TimerRequest.fromMap(map)) { reply: Void? -> }
                }
            }
            NotificationReceiver.ACTION_PAUSE_REQUEST -> {
                map = intent.getSerializableExtra(NotificationReceiver.KEY_PAUSE_REQUEST) as HashMap<String, Any>?
                if (map != null) {
                    flutterTimerApi!!.onPauseRequest(TimerRequest.fromMap(map)) { }
                }
            }
            NotificationReceiver.ACTION_CANCEL_REQUEST -> {
                map = intent.getSerializableExtra(NotificationReceiver.KEY_CANCEL_REQUEST) as HashMap<String, Any>?
                if (map != null) {
                    flutterTimerApi!!.onCancelRequest(TimerRequest.fromMap(map)) { }
                }
            }
            NotificationReceiver.ACTION_EXTEND -> {
                map = intent.getSerializableExtra(NotificationReceiver.KEY_EXTEND_RESPONSE) as HashMap<String, Any>?
                if (map != null) {
                    val extendTimeResponse: ExtendTimeResponse = ExtendTimeResponse.fromMap(map)
                    flutterTimerApi!!.onExtendTime(extendTimeResponse) { }
                }
            }
            NotificationReceiver.ACTION_RESTART_REQUEST -> {
                map = intent.getSerializableExtra(NotificationReceiver.KEY_RESTART_REQUEST) as HashMap<String, Any>?
                if (map != null) {
                    flutterTimerApi!!.onRestartRequest(TimerRequest.fromMap(map)) { reply: Void? -> }
                }
            }
            else -> {
            }
        }
    }

    private fun initializeFlutter() {
        val flutterEngine = FlutterEngineCache.getInstance()[MainActivity.ENGINE_ID]
        if (flutterEngine != null) {
            flutterTimerApi = FlutterTimerApi(flutterEngine.dartExecutor.binaryMessenger)
        }
    }

}