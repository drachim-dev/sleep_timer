package dr.achim.sleep_timer;

import android.content.Context;
import android.content.Intent;

import dr.achim.sleep_timer.Messages.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;

import io.flutter.Log;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;

import static android.content.Intent.ACTION_MAIN;

public class MainActivity extends FlutterActivity {
    private static final String TAG = MainActivity.class.toString();
    public static final String ENGINE_ID = "SLEEP_TIMER_ENGINE_ID";

    final FlutterEngineCache cache = FlutterEngineCache.getInstance();

    private FlutterEngine flutterEngine;
    private FlutterTimerApi flutterTimerApi;

    @Nullable
    @Override
    public FlutterEngine provideFlutterEngine(@NonNull Context context) {
        Log.d(TAG,"provideFlutterEngine");

        flutterEngine = cache.get(ENGINE_ID);
        if(flutterEngine == null) {
            Log.d(TAG,"cachedEngine is null");
            flutterEngine = new FlutterEngine(this);
            cache.put(ENGINE_ID, flutterEngine);
       }
       return flutterEngine;
    }

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);

        Log.d(TAG,"configureFlutterEngine");

        HostTimerApi.setup(flutterEngine.getDartExecutor(), new MethodChannelImpl(getApplicationContext()));
        flutterTimerApi = new FlutterTimerApi(flutterEngine.getDartExecutor().getBinaryMessenger());
    }

    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent: " + intent.getAction());

        switch (intent.getAction()) {
            case ACTION_MAIN:
                launchedByNotification(intent);
                break;
            default:
                break;
        }
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