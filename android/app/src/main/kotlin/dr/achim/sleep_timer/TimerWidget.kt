package dr.achim.sleep_timer

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import dr.achim.sleep_timer.Messages.FlutterTimerApi
import dr.achim.sleep_timer.Messages.WidgetUpdateResponse
import io.flutter.FlutterInjector
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor.DartEntrypoint
import io.flutter.view.FlutterCallbackInformation

class TimerWidget : AppWidgetProvider() {

    private var flutterTimerApi: FlutterTimerApi? = null
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        initializeFlutter(context)
        for (appWidgetId in appWidgetIds) {
            updateWidget("onUpdate \${Math.random()}", appWidgetId, context)
            // Pass over the id so we can update it later...
            flutterTimerApi?.onWidgetUpdate { reply: WidgetUpdateResponse -> updateWidget(reply.title + appWidgetId, appWidgetId, context) }
            val startTimerIntent = Intent(context, WidgetReceiver::class.java)
            startTimerIntent.action = ACTION_CODE_WIDGET_START_TIMER
            val startTimerPendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_START_WIDGET, startTimerIntent, PendingIntent.FLAG_IMMUTABLE)
            val widgetSettingsIntent = Intent(context, WidgetReceiver::class.java)
            widgetSettingsIntent.action = ACTION_CODE_WIDGET_SETTINGS
            val widgetSettingsPendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_WIDGET_SETTINGS, widgetSettingsIntent, PendingIntent.FLAG_IMMUTABLE)

            // Get the layout for the Widget and attach an OnClickListener
            val views = RemoteViews(
                    context.packageName,
                    R.layout.widget_layout
            )
            views.setOnClickPendingIntent(R.id.btn_widget_start, startTimerPendingIntent)
            views.setOnClickPendingIntent(R.id.btn_widget_settings, widgetSettingsPendingIntent)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    private fun initializeFlutter(context: Context) {
        var flutterEngine = FlutterEngineCache.getInstance()[MainActivity.ENGINE_ID]
        val flutterLoader = FlutterInjector.instance().flutterLoader()
        flutterLoader.startInitialization(context)
        flutterLoader.ensureInitializationComplete(context, null)
        val handle = CallbackHelper.getRawHandle(context)
        if (handle == CallbackHelper.NO_HANDLE) {
            Log.e(TAG, "Couldn't update widget because there is no handle stored!")
            return
        }
        val callbackInfo = FlutterCallbackInformation.lookupCallbackInformation(handle)
        // You could also use a hard coded value to save you from all
        // the hassle with SharedPreferences, but alas when running your
        // app in release mode this would fail.
        if (flutterEngine == null) {
            flutterEngine = FlutterEngine(context.applicationContext)
            val entryPointFunctionName = callbackInfo.callbackName
            val entryPoint = DartEntrypoint(flutterLoader.findAppBundlePath(), entryPointFunctionName)
            flutterEngine.dartExecutor.executeDartEntrypoint(entryPoint)
        }
        flutterTimerApi = FlutterTimerApi(flutterEngine.dartExecutor.binaryMessenger)
    }

    private fun updateWidget(text: String, id: Int, context: Context) {
        val views = RemoteViews(context.packageName, R.layout.widget_layout)
        views.setTextViewText(R.id.tv_timer_duration, text)
        val manager = AppWidgetManager.getInstance(context)
        manager.updateAppWidget(id, views)
    }

    companion object {
        private val TAG = TimerWidget::class.java.toString()
        private const val REQUEST_CODE_START_WIDGET = 1000
        private const val REQUEST_CODE_WIDGET_SETTINGS = 2000
        const val ACTION_CODE_WIDGET_START_TIMER = "ACTION_CODE_START_WIDGET"
        const val ACTION_CODE_WIDGET_SETTINGS = "ACTION_CODE_WIDGET_SETTINGS"
    }
}