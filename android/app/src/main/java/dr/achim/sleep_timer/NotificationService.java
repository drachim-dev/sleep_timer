package dr.achim.sleep_timer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import dr.achim.sleep_timer.Messages.*;
import java.util.HashMap;
import java.util.List;

import io.flutter.Log;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.view.FlutterCallbackInformation;
import io.flutter.view.FlutterMain;

public class NotificationService extends Service {
    private static final String TAG = NotificationService.class.toString();

    private static final String NOTIFICATION_CHANNEL_ID = "0";
    private static final int NOTIFICATION_ID = 1;

    private static final int REQUEST_CODE_OPEN = 100;
    private static final int REQUEST_CODE_START = 200;
    private static final int REQUEST_CODE_CONTINUE_REQUEST = 210;
    private static final int REQUEST_CODE_PAUSE_REQUEST = 310;
    private static final int REQUEST_CODE_PAUSE_NOTIFICATION = 300;
    private static final int REQUEST_CODE_CANCEL_REQUEST = 310;
    private static final int REQUEST_CODE_EXTEND_5 = 510;
    private static final int REQUEST_CODE_EXTEND_20 = 520;

    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_CONTINUE_REQUEST = "ACTION_CONTINUE";
    public static final String ACTION_CONTINUE_NOTIFICATION = "ACTION_CONTINUE_NOTIFICATION";
    public static final String ACTION_PAUSE_REQUEST = "ACTION_PAUSE_REQUEST";
    public static final String ACTION_PAUSE_NOTIFICATION = "ACTION_PAUSE_NOTIFICATION";
    public static final String ACTION_CANCEL_REQUEST = "ACTION_CANCEL_REQUEST";
    public static final String ACTION_CANCEL_NOTIFICATION = "ACTION_CANCEL_NOTIFICATION";
    public static final String ACTION_EXTEND = "ACTION_EXTEND";

    public static final String KEY_TIMER_ID = "KEY_TIMER_ID";
    public static final String KEY_OPEN_REQUEST = "KEY_OPEN_REQUEST";
    public static final String KEY_SHOW_NOTIFICATION = "KEY_SHOW_NOTIFICATION";
    public static final String KEY_CONTINUE_REQUEST = "KEY_CONTINUE_REQUEST";
    public static final String KEY_PAUSE_REQUEST = "KEY_PAUSE_REQUEST";
    public static final String KEY_PAUSE_NOTIFICATION = "KEY_PAUSE_NOTIFICATION";
    public static final String KEY_CANCEL_REQUEST = "KEY_CANCEL_REQUEST";
    public static final String KEY_CANCEL_NOTIFICATION= "KEY_CANCEL_NOTIFICATION";
    public static final String KEY_EXTEND_RESPONSE = "KEY_EXTEND_RESPONSE";

    private FlutterTimerApi flutterTimerApi;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initializeFlutter();

        if (intent != null) {
            String action = intent.getAction();
            if(action != null) {
                Log.wtf(TAG, action);

                final HashMap<String,Object> hashMap;
                switch (action) {
                case ACTION_START:
                    showRunningNotification(intent);
                    break;

                case ACTION_CONTINUE_REQUEST:
                    hashMap = (HashMap<String, Object>) intent.getSerializableExtra(KEY_CONTINUE_REQUEST);
                    if(hashMap != null) {
                        flutterTimerApi.onContinueRequest(ContinueRequest.fromMap(hashMap), reply -> {
                        });
                    }
                    break;

                case ACTION_CONTINUE_NOTIFICATION:
                    showRunningNotification(intent);
                    break;

                case ACTION_PAUSE_REQUEST:
                    hashMap = (HashMap<String, Object>) intent.getSerializableExtra(KEY_PAUSE_REQUEST);
                    if(hashMap != null) {
                        flutterTimerApi.onPauseRequest(PauseRequest.fromMap(hashMap), reply -> {
                        });
                    }
                    break;

                case ACTION_PAUSE_NOTIFICATION:
                    showPausingNotification(intent);
                    break;

                case ACTION_CANCEL_REQUEST:
                    hashMap = (HashMap<String, Object>) intent.getSerializableExtra(KEY_CANCEL_REQUEST);
                    if(hashMap != null) {
                        flutterTimerApi.onCancelRequest(CancelRequest.fromMap(hashMap), reply -> {
                        });
                    }
                    break;

                case ACTION_CANCEL_NOTIFICATION:
                    stopForegroundService();
                    break;

                case ACTION_EXTEND:
                    hashMap = (HashMap<String, Object>) intent.getSerializableExtra(KEY_EXTEND_RESPONSE);
                    if(hashMap != null) {
                        final ExtendTimeResponse extendTimeResponse = ExtendTimeResponse.fromMap(hashMap);
                        flutterTimerApi.onExtendTime(extendTimeResponse, reply -> {
                        });
                    }
                    break;
            }}
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void stopForegroundService() {
        Log.d(TAG, "Stop foreground service.");

        stopForeground(true);
        stopSelf();
    }

    private void initNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final int importance = NotificationManager.IMPORTANCE_DEFAULT;
            final NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Active timer", importance);
            notificationChannel.setDescription("Notify about running or pausing timers");
            NotificationManagerCompat.from(getApplicationContext()).createNotificationChannel(notificationChannel);
        }
    }

    private void showRunningNotification(final Intent intent) {
        final HashMap<String,Object> hashMap = (HashMap<String, Object>) intent.getSerializableExtra(KEY_SHOW_NOTIFICATION);
        if(hashMap != null) {
            initNotificationChannel();

            final ShowRunningNotificationRequest request = ShowRunningNotificationRequest.fromMap(hashMap);

            final String timerId = request.getTimerId();
            final List<String> actions = request.getActions();

            Notification notification = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                    .setContentTitle(request.getTitle())
                    .setContentText(request.getDescription())
                    .setSmallIcon(R.drawable.ic_hourglass_full)
                    .setContentIntent(createOpenIntent(timerId))
                    .addAction(R.drawable.ic_baseline_pause_24, actions.get(0).toUpperCase(), createPauseRequestIntent(timerId))
                    .addAction(R.drawable.ic_baseline_replay_5_24, actions.get(1).toUpperCase(), createExtendTimeIntent(timerId, 5 * 60, REQUEST_CODE_EXTEND_5))
                    .addAction(R.drawable.ic_baseline_replay_10_24, actions.get(2).toUpperCase(), createExtendTimeIntent(timerId, 20 * 60, REQUEST_CODE_EXTEND_20))
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setShowWhen(true)
                    .setUsesChronometer(true)
                    .setOngoing(false) // TODO: set to true
                    .setAutoCancel(false)
                    .setWhen(System.currentTimeMillis() + request.getRemainingTime() * 1000)
                    .build();

            startForeground(NOTIFICATION_ID, notification);
        }
    }

    private void showPausingNotification(final Intent intent) {
        final HashMap<String,Object> hashMap = (HashMap<String, Object>) intent.getSerializableExtra(KEY_PAUSE_NOTIFICATION);
        if(hashMap != null) {
            initNotificationChannel();

            final ShowPausingNotificationRequest request = ShowPausingNotificationRequest.fromMap(hashMap);

            final String timerId = request.getTimerId();
            final List<String> actions = request.getActions();

            Notification notification = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                    .setContentTitle(request.getTitle())
                    .setContentText(request.getDescription())
                    .setSmallIcon(R.drawable.ic_hourglass_full)
                    .setShowWhen(false)
                    .setUsesChronometer(false)
                    .setContentIntent(createOpenIntent(timerId))
                    .addAction(R.drawable.ic_baseline_clear_24, actions.get(0).toUpperCase(), createCancelRequestIntent(timerId))
                    .addAction(R.drawable.ic_baseline_replay_24, actions.get(1).toUpperCase(), createContinueRequestIntent(timerId))
                    .build();

            startForeground(NOTIFICATION_ID, notification);
        }

    }

    private PendingIntent createOpenIntent(final String timerId) {
        final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        final Messages.OpenRequest request = new Messages.OpenRequest();
        request.setTimerId(timerId);
        intent.putExtra(KEY_OPEN_REQUEST, request.toMap());


        return PendingIntent.getActivity(
                getApplicationContext(),
                REQUEST_CODE_OPEN,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent createContinueRequestIntent(final String timerId) {
        final Intent intent = new Intent(this, NotificationService.class);
        intent.setAction(ACTION_CONTINUE_REQUEST);

        final ContinueRequest request = new ContinueRequest();
        request.setTimerId(timerId);
        intent.putExtra(KEY_CONTINUE_REQUEST, request.toMap());

        return PendingIntent.getService(
                this,
                REQUEST_CODE_CONTINUE_REQUEST,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent createPauseRequestIntent(final String timerId) {
        final Intent intent = new Intent(this, NotificationService.class);
        intent.setAction(ACTION_PAUSE_REQUEST);

        final PauseRequest request = new PauseRequest();
        request.setTimerId(timerId);
        intent.putExtra(KEY_PAUSE_REQUEST, request.toMap());

        return PendingIntent.getService(
                this,
                REQUEST_CODE_PAUSE_REQUEST,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent createCancelRequestIntent(final String timerId) {
        final Intent intent = new Intent(this, NotificationService.class);
        intent.setAction(ACTION_CANCEL_REQUEST);

        final CancelNotificationResponse response = new CancelNotificationResponse();
        response.setTimerId(timerId);
        response.setSuccess(true);
        intent.putExtra(KEY_CANCEL_REQUEST, response.toMap());

        return PendingIntent.getService(
                this,
                REQUEST_CODE_CANCEL_REQUEST,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent createExtendTimeIntent(final String timerId, final int additionalTime, final int requestCode) {
        final Intent intent = new Intent(this, NotificationService.class);
        intent.setAction(ACTION_EXTEND);

        final ExtendTimeResponse response = new ExtendTimeResponse();
        response.setTimerId(timerId);
        response.setAdditionalTime((long) additionalTime);
        intent.putExtra(KEY_EXTEND_RESPONSE, response.toMap());

        return PendingIntent.getService(
                this,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void initializeFlutter() {
        if (getApplicationContext() == null) {
            Log.e(TAG, "Context is null");
            return;
        }

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
        flutterTimerApi = new FlutterTimerApi(flutterEngine.getDartExecutor());
    }
}
