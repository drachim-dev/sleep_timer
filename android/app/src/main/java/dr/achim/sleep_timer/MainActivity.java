package dr.achim.sleep_timer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import dr.achim.sleep_timer.Messages.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;

import io.flutter.Log;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;

public class MainActivity extends FlutterActivity {
    private static final String TAG = MainActivity.class.toString();
    public static final String ENGINE_ID = "SLEEP_TIMER_ENGINE_ID";
    private FlutterEngine flutterEngine;

    @Nullable
    @Override
    public FlutterEngine provideFlutterEngine(@NonNull Context context) {
        Log.wtf(TAG,"provideFlutterEngine");
        flutterEngine = FlutterEngineCache.getInstance().get(ENGINE_ID);
        if(flutterEngine == null) {
            Log.wtf(TAG,"cachedEngine is null");
            flutterEngine = new FlutterEngine(this);
            FlutterEngineCache.getInstance().put(ENGINE_ID, flutterEngine);
       }
       return flutterEngine;
    }

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        Log.wtf(TAG,"configureFlutterEngine");
        super.configureFlutterEngine(flutterEngine);
        FlutterEngineCache.getInstance().put(ENGINE_ID, flutterEngine);

        HostTimerApi.setup(flutterEngine.getDartExecutor(), new MethodChannelImpl(getApplicationContext()));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);

        Log.wtf(TAG, "onNewIntent: " + intent.getAction());

        launchedByNotification(intent);
    }

    private void launchedByNotification(final Intent intent) {
        final FlutterTimerApi flutterTimerApi = new FlutterTimerApi(flutterEngine.getDartExecutor().getBinaryMessenger());

        final HashMap<String,Object> map = (HashMap) intent.getSerializableExtra(NotificationReceiver.KEY_OPEN_REQUEST);
        if(map != null) {
            final OpenRequest request = OpenRequest.fromMap(map);
            flutterTimerApi.onOpen(request, reply -> {});
        }
    }
}