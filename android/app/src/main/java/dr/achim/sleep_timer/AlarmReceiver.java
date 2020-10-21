package dr.achim.sleep_timer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = AlarmReceiver.class.toString();
    private Context context;
    private Messages.FlutterTimerApi flutterTimerApi;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        
        initializeFlutter();

        Log.e(TAG, "onReceive()");

        final String timerId = intent.getStringExtra(AlarmService.KEY_TIMER_ID);
        Messages.AlarmRequest request = new Messages.AlarmRequest();
        request.setTimerId(timerId);
        flutterTimerApi.onAlarm(request, reply -> {});
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
