package dr.achim.sleep_timer;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import dr.achim.sleep_timer.Messages.*;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import io.flutter.Log;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;

public class AlarmService extends Service {
    private static final String TAG = AlarmService.class.toString();

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
    private static final int REQUEST_CODE_ELAPSE_NOTIFICATION = 600;
    private static final int REQUEST_CODE_RESTART_REQUEST = 250;
    private static final int REQUEST_CODE_ALARM = 700;

    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_CONTINUE_REQUEST = "ACTION_CONTINUE";
    public static final String ACTION_CONTINUE_NOTIFICATION = "ACTION_CONTINUE_NOTIFICATION";
    public static final String ACTION_PAUSE_REQUEST = "ACTION_PAUSE_REQUEST";
    public static final String ACTION_PAUSE_NOTIFICATION = "ACTION_PAUSE_NOTIFICATION";
    public static final String ACTION_CANCEL_REQUEST = "ACTION_CANCEL_REQUEST";
    public static final String ACTION_CANCEL_NOTIFICATION = "ACTION_CANCEL_NOTIFICATION";
    public static final String ACTION_EXTEND = "ACTION_EXTEND";
    public static final String ACTION_ELAPSE_NOTIFICATION = "ACTION_ELAPSE_NOTIFICATION";
    public static final String ACTION_RESTART_REQUEST = "ACTION_RESTART_REQUEST";

    public static final String KEY_TIMER_ID = "KEY_TIMER_ID";
    public static final String KEY_DURATION = "KEY_DURATION";
    public static final String KEY_OPEN_REQUEST = "KEY_OPEN_REQUEST";
    public static final String KEY_SHOW_NOTIFICATION = "KEY_SHOW_NOTIFICATION";
    public static final String KEY_CONTINUE_REQUEST = "KEY_CONTINUE_REQUEST";
    public static final String KEY_PAUSE_REQUEST = "KEY_PAUSE_REQUEST";
    public static final String KEY_PAUSE_NOTIFICATION = "KEY_PAUSE_NOTIFICATION";
    public static final String KEY_CANCEL_REQUEST = "KEY_CANCEL_REQUEST";
    public static final String KEY_CANCEL_NOTIFICATION= "KEY_CANCEL_NOTIFICATION";
    public static final String KEY_EXTEND_RESPONSE = "KEY_EXTEND_RESPONSE";
    public static final String KEY_ELAPSE_NOTIFICATION = "KEY_ELAPSE_NOTIFICATION";
    public static final String KEY_RESTART_REQUEST = "KEY_RESTART_REQUEST";

    private FlutterTimerApi flutterTimerApi;

    @Override
    public void onCreate() {
        super.onCreate();

        initNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initializeFlutter();

        if (intent != null) {
            String action = intent.getAction();
            if(action != null) {
                Log.wtf(TAG, action);

                final HashMap<String,Object> map;
                switch (action) {
                case ACTION_START:
                    
                    showRunningNotification(intent);
                    
                    map = (HashMap) intent.getSerializableExtra(KEY_SHOW_NOTIFICATION);
                    if(map != null) {
                        final ShowRunningNotificationRequest request = ShowRunningNotificationRequest.fromMap(map);
                        final Intent alarmIntent = new Intent(this, AlarmReceiver.class);
                        alarmIntent.putExtra(KEY_TIMER_ID, request.getTimerId());
                        final PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(this, REQUEST_CODE_ALARM, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        final AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + request.getRemainingTime() * 1000, pendingAlarmIntent);
                    }

                    
                    break;

                case ACTION_CONTINUE_REQUEST:
                    map = (HashMap) intent.getSerializableExtra(KEY_CONTINUE_REQUEST);
                    if(map != null) {
                        flutterTimerApi.onContinueRequest(ContinueRequest.fromMap(map), reply -> {});
                    }
                    break;

                case ACTION_CONTINUE_NOTIFICATION:
                    showRunningNotification(intent);
                    break;

                case ACTION_PAUSE_REQUEST:
                    map = (HashMap) intent.getSerializableExtra(KEY_PAUSE_REQUEST);
                    if(map != null) {
                        flutterTimerApi.onPauseRequest(PauseRequest.fromMap(map), reply -> {});
                    }
                    break;

                case ACTION_PAUSE_NOTIFICATION:
                    showPausingNotification(intent);
                    stopForeground(false);
                    break;

                case ACTION_CANCEL_REQUEST:
                    map = (HashMap) intent.getSerializableExtra(KEY_CANCEL_REQUEST);
                    if(map != null) {
                        flutterTimerApi.onCancelRequest(CancelRequest.fromMap(map), reply -> {});
                    }
                    break;

                case ACTION_CANCEL_NOTIFICATION:
                    stopForeground(true);
                    stopSelf();
                    break;

                case ACTION_EXTEND:
                    map = (HashMap) intent.getSerializableExtra(KEY_EXTEND_RESPONSE);
                    if(map != null) {
                        final ExtendTimeResponse extendTimeResponse = ExtendTimeResponse.fromMap(map);
                        flutterTimerApi.onExtendTime(extendTimeResponse, reply -> {});
                    }
                    break;

                case ACTION_ELAPSE_NOTIFICATION:
                    showElapsedNotification(intent);
                    stopForeground(false);
                    break;

                case ACTION_RESTART_REQUEST:
                    map = (HashMap) intent.getSerializableExtra(KEY_RESTART_REQUEST);
                    if(map != null) {
                        flutterTimerApi.onRestartRequest(RestartRequest.fromMap(map), reply -> {});
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
        final HashMap<String, Object> map = (HashMap) intent.getSerializableExtra(KEY_SHOW_NOTIFICATION);
        if(map != null) {
            final ShowRunningNotificationRequest request = ShowRunningNotificationRequest.fromMap(map);

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
        final HashMap<String,Object> map = (HashMap) intent.getSerializableExtra(KEY_PAUSE_NOTIFICATION);
        if(map != null) {
            final ShowPausingNotificationRequest request = ShowPausingNotificationRequest.fromMap(map);

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

    private void showElapsedNotification(final Intent intent) {
        final HashMap<String,Object> map = (HashMap) intent.getSerializableExtra(KEY_ELAPSE_NOTIFICATION);
        if(map != null) {
            final ShowElapsedNotificationRequest request = ShowElapsedNotificationRequest.fromMap(map);

            final String timerId = request.getTimerId();
            final List<String> actions = request.getActions();

            Notification notification = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                    .setContentTitle(request.getTitle())
                    .setContentText(request.getDescription())
                    .setSmallIcon(R.drawable.ic_hourglass_full)
                    .setShowWhen(false)
                    .setUsesChronometer(false)
                    .setContentIntent(createOpenIntent(timerId))
                    .addAction(R.drawable.ic_baseline_replay_24, actions.get(0).toUpperCase(), createRestartRequestIntent(timerId))
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
        final Intent intent = new Intent(this, AlarmService.class);
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
        final Intent intent = new Intent(this, AlarmService.class);
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
        final Intent intent = new Intent(this, AlarmService.class);
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
        final Intent intent = new Intent(this, AlarmService.class);
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

    private PendingIntent createRestartRequestIntent(final String timerId) {
        final Intent intent = new Intent(this, AlarmService.class);
        intent.setAction(ACTION_RESTART_REQUEST);

        final RestartRequest request = new RestartRequest();
        request.setTimerId(timerId);
        intent.putExtra(KEY_RESTART_REQUEST, request.toMap());

        return PendingIntent.getService(
                this,
                REQUEST_CODE_RESTART_REQUEST,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void initializeFlutter() {
        if (getApplicationContext() == null) {
            Log.e(TAG, "Context is null");
            return;
        }

        final FlutterEngine flutterEngine = FlutterEngineCache.getInstance().get(MainActivity.ENGINE_ID);
        if(flutterEngine != null) {
            flutterTimerApi = new FlutterTimerApi(flutterEngine.getDartExecutor().getBinaryMessenger());
        }
    }
}
