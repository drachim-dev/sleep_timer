package dr.achim.sleep_timer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.HashMap;

import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;

import static dr.achim.sleep_timer.NotificationReceiver.*;

import dr.achim.sleep_timer.Messages.*;

public class NotificationActionReceiver extends BroadcastReceiver {
    private static final String TAG = NotificationActionReceiver.class.toString();
    private Context context;
    private Messages.FlutterTimerApi flutterTimerApi;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        initializeFlutter();

        Log.wtf(TAG, intent.getAction());
        final HashMap<String,Object> map;
        switch (intent.getAction()) {
            case ACTION_CONTINUE_REQUEST:
                map = (HashMap) intent.getSerializableExtra(KEY_CONTINUE_REQUEST);
                if(map != null) {
                    flutterTimerApi.onContinueRequest(TimerRequest.fromMap(map), reply -> {});
                }
                break;

            case ACTION_PAUSE_REQUEST:
                map = (HashMap) intent.getSerializableExtra(KEY_PAUSE_REQUEST);
                if(map != null) {
                    flutterTimerApi.onPauseRequest(TimerRequest.fromMap(map), reply -> {});
                }
                break;

            case ACTION_CANCEL_REQUEST:
                map = (HashMap) intent.getSerializableExtra(KEY_CANCEL_REQUEST);
                if(map != null) {
                    flutterTimerApi.onCancelRequest(TimerRequest.fromMap(map), reply -> {});
                }
                break;

            case ACTION_EXTEND:
                map = (HashMap) intent.getSerializableExtra(KEY_EXTEND_RESPONSE);
                if(map != null) {
                    final ExtendTimeResponse extendTimeResponse = ExtendTimeResponse.fromMap(map);
                    flutterTimerApi.onExtendTime(extendTimeResponse, reply -> {});
                }
                break;

            case ACTION_RESTART_REQUEST:
                map = (HashMap) intent.getSerializableExtra(KEY_RESTART_REQUEST);
                if(map != null) {
                    flutterTimerApi.onRestartRequest(TimerRequest.fromMap(map), reply -> {});
                }
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
