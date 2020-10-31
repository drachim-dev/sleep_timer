package dr.achim.sleep_timer;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import io.flutter.FlutterInjector;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.embedding.engine.loader.FlutterLoader;
import io.flutter.view.FlutterCallbackInformation;

import dr.achim.sleep_timer.Messages.*;

public class TimerWidget extends AppWidgetProvider {
    private static final String TAG = TimerWidget.class.toString();

    private static final int REQUEST_CODE_START_WIDGET = 1000;
    private static final int REQUEST_CODE_WIDGET_SETTINGS = 2000;

    public static final String ACTION_CODE_WIDGET_START_TIMER = "ACTION_CODE_START_WIDGET";
    public static final String ACTION_CODE_WIDGET_SETTINGS = "ACTION_CODE_WIDGET_SETTINGS";

    private Context context;
    private FlutterTimerApi flutterTimerApi;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        this.context = context;

        initializeFlutter();

        for (int appWidgetId : appWidgetIds) {
            updateWidget("onUpdate ${Math.random()}", appWidgetId, context);
            // Pass over the id so we can update it later...
            flutterTimerApi.onWidgetUpdate(reply -> {
                updateWidget(reply.getTitle() + appWidgetId, appWidgetId, context);
            });

            final Intent startTimerIntent = new Intent(context, WidgetReceiver.class);
            startTimerIntent.setAction(ACTION_CODE_WIDGET_START_TIMER);
            final PendingIntent startTimerPendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_START_WIDGET, startTimerIntent, 0);

            final Intent widgetSettingsIntent = new Intent(context, WidgetReceiver.class);
            widgetSettingsIntent.setAction(ACTION_CODE_WIDGET_SETTINGS);
            final PendingIntent widgetSettingsPendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_WIDGET_SETTINGS, widgetSettingsIntent, 0);

            // Get the layout for the Widget and attach an OnClickListener
            final RemoteViews views = new RemoteViews(
                    context.getPackageName(),
                    R.layout.widget_layout
            );
            views.setOnClickPendingIntent(R.id.btn_widget_start, startTimerPendingIntent);
            views.setOnClickPendingIntent(R.id.btn_widget_settings, widgetSettingsPendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

    }

    private void initializeFlutter() {
        FlutterEngine flutterEngine = FlutterEngineCache.getInstance().get(MainActivity.ENGINE_ID);
        final FlutterLoader flutterLoader = FlutterInjector.instance().flutterLoader();
        flutterLoader.startInitialization(context);
        flutterLoader.ensureInitializationComplete(context, null);

        long handle = CallbackHelper.getRawHandle(context);
        if (handle == CallbackHelper.NO_HANDLE) {
            Log.e(TAG, "Couldn't update widget because there is no handle stored!");
            return;
        }
        final FlutterCallbackInformation callbackInfo = FlutterCallbackInformation.lookupCallbackInformation(handle);
        // You could also use a hard coded value to save you from all
        // the hassle with SharedPreferences, but alas when running your
        // app in release mode this would fail.
        if (flutterEngine == null) {
            flutterEngine = new FlutterEngine(context.getApplicationContext());
            final String entryPointFunctionName = callbackInfo.callbackName;
            final DartExecutor.DartEntrypoint entryPoint = new DartExecutor.DartEntrypoint(flutterLoader.findAppBundlePath(), entryPointFunctionName);
            flutterEngine.getDartExecutor().executeDartEntrypoint(entryPoint);
        }

        flutterTimerApi = new FlutterTimerApi(flutterEngine.getDartExecutor().getBinaryMessenger());
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    private void updateWidget(String text, int id, Context context) {
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        views.setTextViewText(R.id.tv_timer_duration, text);

        final AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(id, views);
    }

}