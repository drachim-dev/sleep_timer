package dr.achim.sleep_timer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import dr.achim.sleep_timer.Messages.*;

import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;

import static dr.achim.sleep_timer.TimerWidget.ACTION_CODE_WIDGET_SETTINGS;
import static dr.achim.sleep_timer.TimerWidget.ACTION_CODE_WIDGET_START_TIMER;

public class WidgetReceiver extends BroadcastReceiver {
    private static final String TAG = WidgetReceiver.class.toString();
    private Context context;
    private FlutterTimerApi flutterTimerApi;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        initializeFlutter();

        Log.wtf(TAG, intent.getAction());

        switch (intent.getAction()) {
            case ACTION_CODE_WIDGET_START_TIMER:
                flutterTimerApi.onWidgetStartTimer(reply -> {});
                break;

            case ACTION_CODE_WIDGET_SETTINGS:

                // flutterTimerApi.onWidgetSettings(reply -> {});
                break;
            default:

                break;
        }


    }

    private void initializeFlutter() {
        if (context == null) {
            Log.e(TAG, "Context is null");
            return;
        }

        final FlutterEngine flutterEngine = FlutterEngineCache.getInstance().get(MainActivity.ENGINE_ID);
        if(flutterEngine != null) {
            flutterTimerApi = new Messages.FlutterTimerApi(flutterEngine.getDartExecutor().getBinaryMessenger());
        }
    }
}