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
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.view.FlutterCallbackInformation;
import io.flutter.view.FlutterMain;

public class MainActivity extends FlutterActivity {
    private static final String TAG = MainActivity.class.toString();
    public static final String ENGINE_ID = "SLEEP_TIMER_ENGINE_ID";

    @Nullable
    @Override
    public FlutterEngine provideFlutterEngine(@NonNull Context context) {
        Log.wtf(TAG,"provideFlutterEngine");
        FlutterEngine cachedEngine = FlutterEngineCache.getInstance().get(ENGINE_ID);
        if(cachedEngine == null) {
            Log.wtf(TAG,"cachedEngine is null");
            cachedEngine = new FlutterEngine(this);
            FlutterEngineCache.getInstance().put(ENGINE_ID, cachedEngine);
       }
       return cachedEngine;
    }

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        Log.wtf(TAG,"configureFlutterEngine");
        super.configureFlutterEngine(flutterEngine);

        HostTimerApi.setup(flutterEngine.getDartExecutor(), new MethodChannelImpl(getApplicationContext()));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        launchedByNotification(getIntent());
    }

    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);

        launchedByNotification(intent);
    }

    private void launchedByNotification(final Intent intent) {
        FlutterEngine flutterEngine = FlutterEngineCache.getInstance().get(MainActivity.ENGINE_ID);
        if (flutterEngine == null) {
            Log.e(TAG, "FlutterEngine is null");
            FlutterMain.startInitialization(getApplicationContext());
            FlutterMain.ensureInitializationComplete(getApplicationContext(), new String[0]);

            long handle = CallbackHelper.getRawHandle(getApplicationContext());
            if (handle == CallbackHelper.NO_HANDLE) {
                Log.w(TAG, "Couldn't update widget because there is no handle stored!");
                return;
            }

            FlutterCallbackInformation callbackInfo = FlutterCallbackInformation.lookupCallbackInformation(handle);
            // You could also use a hard coded value to save you from all
            // the hassle with SharedPreferences, but alas when running your
            // app in release mode this would fail.
            String entryPointFunctionName = callbackInfo.callbackName;

            // Instantiate a FlutterEngine.
            flutterEngine = new FlutterEngine(getApplicationContext());
            DartExecutor.DartEntrypoint entryPoint = new DartExecutor.DartEntrypoint(FlutterMain.findAppBundlePath(), entryPointFunctionName);
            flutterEngine.getDartExecutor().executeDartEntrypoint(entryPoint);
        }

        final FlutterTimerApi flutterTimerApi = new FlutterTimerApi(flutterEngine.getDartExecutor());

        final HashMap<String,Object> hashMap = (HashMap<String, Object>) intent.getSerializableExtra(NotificationService.KEY_OPEN_REQUEST);
        if(hashMap != null) {
            final OpenRequest request = OpenRequest.fromMap(hashMap);
            flutterTimerApi.onOpen(request, reply -> {
            });
        }

    }
}