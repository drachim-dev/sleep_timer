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
        Log.d(TAG, "intent action: ${intent.action}")
        val map: ArrayList<Any>?
        when (intent.action) {
            NotificationReceiver.ACTION_CONTINUE_REQUEST -> {
                map = intent.getSerializableExtra(NotificationReceiver.KEY_CONTINUE_REQUEST) as ArrayList<Any>?
                if (map != null) {
                    flutterTimerApi.onContinueRequest(TimerRequest.fromList(map), object : VoidResult {
                        override fun success() {}
                        override fun error(error: Throwable) {}
                    })
                }
            }
            NotificationReceiver.ACTION_PAUSE_REQUEST -> {
                map = intent.getSerializableExtra(NotificationReceiver.KEY_PAUSE_REQUEST) as ArrayList<Any>?
                if (map != null) {
                    flutterTimerApi.onPauseRequest(TimerRequest.fromList(map), object : VoidResult {
                        override fun success() {}
                        override fun error(error: Throwable) {}
                    })
                }
            }
            NotificationReceiver.ACTION_CANCEL_REQUEST -> {
                map = intent.getSerializableExtra(NotificationReceiver.KEY_CANCEL_REQUEST) as ArrayList<Any>?
                if (map != null) {
                    flutterTimerApi.onCancelRequest(TimerRequest.fromList(map), object : VoidResult {
                        override fun success() {}
                        override fun error(error: Throwable) {}
                    })
                }
            }
            NotificationReceiver.ACTION_EXTEND -> {
                map = intent.getSerializableExtra(NotificationReceiver.KEY_EXTEND_RESPONSE) as ArrayList<Any>?
                if (map != null) {
                    val extendTimeResponse: ExtendTimeRequest = ExtendTimeRequest.fromList(map)
                    flutterTimerApi.onExtendTime(extendTimeResponse, object : VoidResult {
                        override fun success() {}
                        override fun error(error: Throwable) {}
                    })
                }
            }
            NotificationReceiver.ACTION_COUNTDOWN -> {
                map = intent.getSerializableExtra(NotificationReceiver.KEY_COUNTDOWN_REQUEST) as ArrayList<Any>?
                if (map != null) {
                    val countDownRequest: CountDownRequest = CountDownRequest.fromList(map)
                    flutterTimerApi.onCountDown(countDownRequest, object : VoidResult {
                        override fun success() {}
                        override fun error(error: Throwable) {}
                    })
                }
            }
            NotificationReceiver.ACTION_RESTART_REQUEST -> {
                map = intent.getSerializableExtra(NotificationReceiver.KEY_RESTART_REQUEST) as ArrayList<Any>?
                if (map != null) {
                    flutterTimerApi.onRestartRequest(TimerRequest.fromList(map), object : VoidResult {
                        override fun success() {}
                        override fun error(error: Throwable) {}
                    })
                }
            }
            else -> { }
        }
    }

    private fun initializeFlutter() {
        FlutterEngineCache.getInstance()[MainActivity.ENGINE_ID]?.let {
            flutterTimerApi = FlutterTimerApi(it.dartExecutor.binaryMessenger)
        }
    }

}