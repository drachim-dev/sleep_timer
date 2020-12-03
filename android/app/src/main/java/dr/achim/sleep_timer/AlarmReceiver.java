package dr.achim.sleep_timer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;

import dr.achim.sleep_timer.Messages.*;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = AlarmReceiver.class.toString();
    private Messages.FlutterTimerApi flutterTimerApi;

    @Override
    public void onReceive(Context context, Intent intent) {
        initializeFlutter();

        Log.e(TAG, "onReceive()");

        final String timerId = intent.getStringExtra(NotificationReceiver.KEY_TIMER_ID);
        TimerRequest request = new TimerRequest();
        request.setTimerId(timerId);
        flutterTimerApi.onAlarm(request, reply -> {});
    }

    private void initializeFlutter() {
        final FlutterEngine flutterEngine = FlutterEngineCache.getInstance().get(MainActivity.ENGINE_ID);
        if(flutterEngine != null) {
            flutterTimerApi = new Messages.FlutterTimerApi(flutterEngine.getDartExecutor().getBinaryMessenger());
        }
    }
}