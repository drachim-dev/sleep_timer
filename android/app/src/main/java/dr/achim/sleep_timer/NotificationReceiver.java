package dr.achim.sleep_timer;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.HashMap;
import java.util.List;

import dr.achim.sleep_timer.Messages.*;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String TAG = AlarmService.class.toString();

    public static final String NOTIFICATION_CHANNEL_ID = "1";
    public static final int NOTIFICATION_ID = 1;

    public static final String ACTION_SHOW_RUNNING = "ACTION_SHOW_RUNNING";
    public static final String ACTION_PAUSE_NOTIFICATION = "ACTION_PAUSE_NOTIFICATION";
    public static final String ACTION_ELAPSED_NOTIFICATION = "ACTION_ELAPSED_NOTIFICATION";
    public static final String ACTION_EXTEND = "ACTION_EXTEND";

    public static final String ACTION_CONTINUE_REQUEST = "ACTION_CONTINUE_REQUEST";
    public static final String ACTION_PAUSE_REQUEST = "ACTION_PAUSE_REQUEST";
    public static final String ACTION_RESTART_REQUEST = "ACTION_RESTART_REQUEST";
    public static final String ACTION_CANCEL_REQUEST = "ACTION_CANCEL_REQUEST";

    public static final String KEY_SHOW_NOTIFICATION = "KEY_SHOW_NOTIFICATION";
    public static final String KEY_PAUSE_NOTIFICATION = "KEY_PAUSE_NOTIFICATION";
    public static final String KEY_ELAPSED_NOTIFICATION = "KEY_ELAPSED_NOTIFICATION";

    public static final String KEY_OPEN_REQUEST = "KEY_OPEN_REQUEST";
    public static final String KEY_CONTINUE_REQUEST = "KEY_CONTINUE_REQUEST";
    public static final String KEY_PAUSE_REQUEST = "KEY_PAUSE_REQUEST";
    public static final String KEY_CANCEL_REQUEST = "KEY_CANCEL_REQUEST";
    public static final String KEY_EXTEND_RESPONSE = "KEY_EXTEND_RESPONSE";
    public static final String KEY_RESTART_REQUEST = "KEY_RESTART_REQUEST";

    public static final String KEY_TIMER_ID = "KEY_TIMER_ID";

    private static final int REQUEST_CODE_OPEN = 100;
    private static final int REQUEST_CODE_EXTEND_5 = 505;
    private static final int REQUEST_CODE_EXTEND_20 = 520;
    private static final int REQUEST_CODE_CONTINUE_REQUEST = 200;
    private static final int REQUEST_CODE_PAUSE_REQUEST = 300;
    private static final int REQUEST_CODE_RESTART_REQUEST = 150;
    private static final int REQUEST_CODE_CANCEL_REQUEST = 400;

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        Log.wtf(TAG, intent.getAction());
        final HashMap map;
        switch (intent.getAction()) {
            case ACTION_SHOW_RUNNING:
                map = (HashMap) intent.getSerializableExtra(KEY_SHOW_NOTIFICATION);
                showRunningNotification(ShowRunningNotificationRequest.fromMap(map));
                break;

            case ACTION_PAUSE_NOTIFICATION:
                map = (HashMap) intent.getSerializableExtra(KEY_PAUSE_NOTIFICATION);
                showPausingNotification(ShowPausingNotificationRequest.fromMap(map));
                break;

            case ACTION_ELAPSED_NOTIFICATION:
                map = (HashMap) intent.getSerializableExtra(KEY_ELAPSED_NOTIFICATION);
                showElapsedNotification(ShowElapsedNotificationRequest.fromMap(map));
                break;
        }
    }

    private void showNotification(final Notification notification) {
        NotificationManagerCompat.from(context).notify(NotificationReceiver.NOTIFICATION_ID, notification);
    }

    private void showRunningNotification(final ShowRunningNotificationRequest request) {
        final String timerId = request.getTimerId();
        final List<String> actions = request.getActions();

        final Notification notification = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
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

        showNotification(notification);
    }

    private void showPausingNotification(final ShowPausingNotificationRequest request) {
        final String timerId = request.getTimerId();
        final List<String> actions = request.getActions();

        final Notification notification = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(request.getTitle())
                .setContentText(request.getDescription())
                .setSmallIcon(R.drawable.ic_hourglass_full)
                .setShowWhen(false)
                .setUsesChronometer(false)
                .setContentIntent(createOpenIntent(timerId))
                .addAction(R.drawable.ic_baseline_clear_24, actions.get(0).toUpperCase(), createCancelRequestIntent(timerId))
                .addAction(R.drawable.ic_baseline_replay_24, actions.get(1).toUpperCase(), createContinueRequestIntent(timerId))
                .build();

        showNotification(notification);
    }

    private void showElapsedNotification(final ShowElapsedNotificationRequest request) {
        final String timerId = request.getTimerId();
        final List<String> actions = request.getActions();

        final Notification notification = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(request.getTitle())
                .setContentText(request.getDescription())
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(request.getDescription()))
                .setSmallIcon(R.drawable.ic_hourglass_full)
                .setShowWhen(false)
                .setUsesChronometer(false)
                .setContentIntent(createOpenIntent(timerId))
                .addAction(R.drawable.ic_baseline_replay_24, actions.get(0).toUpperCase(), createRestartRequestIntent(timerId))
                .build();

        showNotification(notification);
    }

    private PendingIntent createOpenIntent(final String timerId) {
        final Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        final Messages.OpenRequest request = new Messages.OpenRequest();
        request.setTimerId(timerId);
        intent.putExtra(KEY_OPEN_REQUEST, request.toMap());


        return PendingIntent.getActivity(
                context,
                REQUEST_CODE_OPEN,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent createContinueRequestIntent(final String timerId) {
        final Intent intent = new Intent(context, NotificationActionReceiver.class);
        intent.setAction(ACTION_CONTINUE_REQUEST);

        final ContinueRequest request = new ContinueRequest();
        request.setTimerId(timerId);
        intent.putExtra(KEY_CONTINUE_REQUEST, request.toMap());

        return PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_CONTINUE_REQUEST,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent createPauseRequestIntent(final String timerId) {
        final Intent intent = new Intent(context, NotificationActionReceiver.class);
        intent.setAction(ACTION_PAUSE_REQUEST);

        final PauseRequest request = new PauseRequest();
        request.setTimerId(timerId);
        intent.putExtra(KEY_PAUSE_REQUEST, request.toMap());

        return PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_PAUSE_REQUEST,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent createCancelRequestIntent(final String timerId) {
        final Intent intent = new Intent(context, NotificationActionReceiver.class);
        intent.setAction(ACTION_CANCEL_REQUEST);

        final CancelNotificationResponse response = new CancelNotificationResponse();
        response.setTimerId(timerId);
        response.setSuccess(true);
        intent.putExtra(KEY_CANCEL_REQUEST, response.toMap());

        return PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_CANCEL_REQUEST,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent createExtendTimeIntent(final String timerId, final int additionalTime, final int requestCode) {
        final Intent intent = new Intent(context, NotificationActionReceiver.class);
        intent.setAction(ACTION_EXTEND);

        final ExtendTimeResponse response = new ExtendTimeResponse();
        response.setTimerId(timerId);
        response.setAdditionalTime((long) additionalTime);
        intent.putExtra(KEY_EXTEND_RESPONSE, response.toMap());

        return PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent createRestartRequestIntent(final String timerId) {
        final Intent intent = new Intent(context, NotificationActionReceiver.class);
        intent.setAction(ACTION_RESTART_REQUEST);

        final RestartRequest request = new RestartRequest();
        request.setTimerId(timerId);
        intent.putExtra(KEY_RESTART_REQUEST, request.toMap());

        return PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_RESTART_REQUEST,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
