package dr.achim.sleep_timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import io.flutter.Log
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant
import io.flutter.view.FlutterCallbackInformation
import io.flutter.view.FlutterMain

const val NOTIF_ACTION_OPEN = "NOTIF_ACTION_OPEN";
const val NOTIF_ACTION_CANCEL = "NOTIF_ACTION_CANCEL";
const val NOTIF_ACTION_DISMISS = "NOTIF_ACTION_DISMISS";
const val NOTIF_ACTION_PAUSE = "NOTIF_ACTION_PAUSE"
const val NOTIF_ACTION_CONTINUE = "NOTIF_ACTION_CONTINUE"
const val NOTIF_ACTION_EXTEND_5 = "NOTIF_ACTION_EXTEND_5"
const val NOTIF_ACTION_EXTEND_20 = "NOTIF_ACTION_EXTEND_20"
const val NOTIF_ACTION_RESTART = "NOTIF_ACTION_RESTART"

class NotificationReceiver : BroadcastReceiver() {
    private val TAG = this::class.java.simpleName

    companion object {
        private var channel: MethodChannel? = null;
    }

    private var context: Context? = null

    override fun onReceive(context: Context, intent: Intent) {
        this.context = context

        initializeFlutter()

        val id = intent.getIntExtra("id", -1)
        val arguments = mapOf("id" to id)

        when (intent.action) {
            NOTIF_ACTION_OPEN -> {
                channel?.invokeMethod(intent.action.toString(), arguments)
            }
            NOTIF_ACTION_CANCEL -> {
                NotificationManagerCompat.from(context).cancel(id)
                channel?.invokeMethod("cancelTimer", arguments)
            }
            NOTIF_ACTION_DISMISS -> {
                NotificationManagerCompat.from(context).cancel(id)
                channel?.invokeMethod("dismissTimer", arguments)
            }
            NOTIF_ACTION_PAUSE -> {
                channel?.invokeMethod(intent.action.toString(), arguments)
            }
            NOTIF_ACTION_CONTINUE -> {
                channel?.invokeMethod(intent.action.toString(), arguments)
            }
            NOTIF_ACTION_EXTEND_5 -> {
                channel?.invokeMethod(intent.action.toString(), arguments)
            }
            NOTIF_ACTION_EXTEND_20 -> {
                channel?.invokeMethod(intent.action.toString(), arguments)
            }
            NOTIF_ACTION_RESTART -> {
                channel?.invokeMethod(intent.action.toString(), arguments)
            }
        }
    }

    private fun initializeFlutter() {
        if (context == null) {
            Log.e(TAG, "Context is null")
            return
        }
        if (channel == null) {
            FlutterMain.startInitialization(context!!)
            FlutterMain.ensureInitializationComplete(context!!, arrayOf())

            val handle = EntryPointCallbackHelper.getRawHandle(context!!)
            if (handle == EntryPointCallbackHelper.NO_HANDLE) {
                Log.w(TAG, "Couldn't update widget because there is no handle stored!")
                return
            }

            val callbackInfo = FlutterCallbackInformation.lookupCallbackInformation(handle)
            // You could also use a hard coded value to save you from all
            // the hassle with SharedPreferences, but alas when running your
            // app in release mode this would fail.
            val entryPointFunctionName = callbackInfo.callbackName

            // Instantiate a FlutterEngine.
            val engine = FlutterEngine(context!!.applicationContext)
            val entryPoint = DartExecutor.DartEntrypoint(FlutterMain.findAppBundlePath(), entryPointFunctionName)
            engine.dartExecutor.executeDartEntrypoint(entryPoint)

            // Register Plugins when in background. When there
            // is already an engine running, this will be ignored (although there will be some
            // warnings in the log).
            GeneratedPluginRegistrant.registerWith(engine)

            channel = MethodChannel(engine.dartExecutor.binaryMessenger, CHANNEL_NAME)
        }
    }

}