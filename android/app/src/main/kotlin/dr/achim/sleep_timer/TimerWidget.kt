package dr.achim.sleep_timer

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant
import io.flutter.view.FlutterCallbackInformation
import io.flutter.view.FlutterMain

class TimerWidget : AppWidgetProvider(), MethodChannel.Result {
    private val TAG = this::class.java.simpleName

    companion object {
        private var channel: MethodChannel? = null;
    }

    private var context: Context? = null

    override fun onUpdate(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetIds: IntArray
    ) {
        this.context = context

        initializeFlutter()

        for (appWidgetId in appWidgetIds) {
            updateWidget("onUpdate ${Math.random()}", appWidgetId, context)
            // Pass over the id so we can update it later...
            channel?.invokeMethod("updateWidget", appWidgetId, this)
        }

        // Perform this loop procedure for each App Widget that belongs to this provider
        appWidgetIds.forEach { appWidgetId ->
            // Create an Intent to launch MainActivity
            val pendingIntentApp: PendingIntent = Intent(context, MainActivity::class.java)
                    .let { intent ->
                        PendingIntent.getActivity(context, 0, intent, 0)
                    }

            // Get the layout for the Widget and attach an OnClickListener
            val views: RemoteViews = RemoteViews(
                    context.packageName,
                    R.layout.widget_layout
            ).apply {
                //setOnClickPendingIntent(R.id.btn_timer_start, pendingIntentApp)
            }

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
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

            channel = MethodChannel(engine.dartExecutor.binaryMessenger, "dr.achim/time_widget")
        }
    }

    override fun success(result: Any?) {
        Log.d(TAG, "success $result")

        val args = result as HashMap<*, *>
        val id = args["id"] as Int
        val value = args["value"] as Double

        updateWidget("onDart $value", id, context!!)
    }

    override fun notImplemented() {
        Log.d(TAG, "notImplemented")
    }

    override fun error(errorCode: String?, errorMessage: String?, errorDetails: Any?) {
        Log.d(TAG, "onError $errorCode")
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
        channel = null
    }
}

internal fun updateWidget(text: String, id: Int, context: Context) {
    val views = RemoteViews(context.packageName, R.layout.widget_layout).apply {
        setTextViewText(R.id.tv_timer_duration, text)
    }

    val manager = AppWidgetManager.getInstance(context)
    manager.updateAppWidget(id, views)
}