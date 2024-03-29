package dr.achim.sleep_timer

import android.content.Context
import android.content.Intent
import dr.achim.sleep_timer.Messages.FlutterTimerApi
import dr.achim.sleep_timer.Messages.HostTimerApi
import dr.achim.sleep_timer.Messages.OpenRequest
import io.flutter.Log
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache


class MainActivity : FlutterActivity() {
    private var flutterTimerApi: FlutterTimerApi? = null

    companion object {
        private val TAG = MainActivity::class.java.toString()
        const val ENGINE_ID = "SLEEP_TIMER_ENGINE_ID"
    }

    override fun provideFlutterEngine(context: Context): FlutterEngine? {
        return FlutterEngineCache.getInstance().get(ENGINE_ID)
    }

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        Log.d(TAG, "configureFlutterEngine")

        HostTimerApi.setUp(flutterEngine.dartExecutor, MethodChannelImpl(applicationContext))
        flutterTimerApi = FlutterTimerApi(flutterEngine.dartExecutor.binaryMessenger)

        // val factory = ListTileNativeAdFactory(layoutInflater)
        // GoogleMobileAdsPlugin.registerNativeAdFactory(flutterEngine, "listTileAdFactory", factory)
    }

    override fun cleanUpFlutterEngine(flutterEngine: FlutterEngine) {
        super.cleanUpFlutterEngine(flutterEngine)

        // GoogleMobileAdsPlugin.unregisterNativeAdFactory(flutterEngine, "listTileAdFactory")
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d(TAG, "onNewIntent: " + intent.action)
        when (intent.action) {
            Intent.ACTION_MAIN -> launchedByNotification(intent)
            else -> { }
        }
    }

    private fun launchedByNotification(intent: Intent) {
        flutterTimerApi = flutterEngine?.let { FlutterTimerApi(it.dartExecutor.binaryMessenger) }

        val map = intent.getSerializableExtra(NotificationReceiver.KEY_OPEN_REQUEST) as ArrayList<Any>?
        if (map != null) {
            val request: OpenRequest = OpenRequest.fromList(map)
            flutterTimerApi?.onOpen(request, object : Messages.VoidResult {
                override fun success() {}
                override fun error(error: Throwable) {}
            })
        }
    }
}