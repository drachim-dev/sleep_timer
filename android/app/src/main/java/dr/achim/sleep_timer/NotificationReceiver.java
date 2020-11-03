package dr.achim.sleep_timer;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.ArrayList;
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
    private static final int REQUEST_CODE_EXTEND = 500;
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
                showRunningNotification(TimeNotificationRequest.fromMap(map));
                break;

            case ACTION_PAUSE_NOTIFICATION:
                map = (HashMap) intent.getSerializableExtra(KEY_PAUSE_NOTIFICATION);
                showPausingNotification(TimeNotificationRequest.fromMap(map));
                break;

            case ACTION_ELAPSED_NOTIFICATION:
                map = (HashMap) intent.getSerializableExtra(KEY_ELAPSED_NOTIFICATION);
                showElapsedNotification(NotificationRequest.fromMap(map));
                break;
        }
    }

    private void showNotification(final Notification notification) {
        NotificationManagerCompat.from(context).notify(NotificationReceiver.NOTIFICATION_ID, notification);
    }

    private void showRunningNotification(final TimeNotificationRequest request) {
        final String timerId = request.getTimerId();

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("" + request.getRemainingTime() * 1000)
                .setContentText(request.getTitle())
                .setSmallIcon(R.drawable.ic_hourglass_full)
                .setContentIntent(createOpenIntent(timerId))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setShowWhen(false)
                .setUsesChronometer(false)
                .setOngoing(true)
                .setAutoCancel(false);
                //.setWhen(System.currentTimeMillis() + request.getRemainingTime() * 1000);

        final List<NotificationCompat.Action> actions = buildNotificationActions(timerId, request.getRestartAction(), request.getPauseAction(), request.getContinueAction(), request.getCancelAction(), request.getExtendActions());
        for (NotificationCompat.Action action : actions) {
            builder.addAction(action);
        }

        final Notification notification = builder.build();
        showNotification(notification);
    }

    private List<NotificationCompat.Action> buildNotificationActions(String timerId, String restartAction, String pauseAction, String continueAction, String cancelAction, ArrayList extendActions) {
        List<NotificationCompat.Action> actions = new ArrayList<>();

        if(restartAction != null && !restartAction.isEmpty()) {
            final PendingIntent intent = createRestartRequestIntent(timerId);
            final NotificationCompat.Action action = new NotificationCompat.Action(R.drawable.ic_baseline_replay_24, restartAction.toUpperCase(), intent);
            actions.add(action);
        }

        if(pauseAction != null && !pauseAction.isEmpty()) {
            final PendingIntent intent = createPauseRequestIntent(timerId);
            final NotificationCompat.Action action = new NotificationCompat.Action(R.drawable.ic_baseline_pause_24, pauseAction.toUpperCase(), intent);
            actions.add(action);
        }

        if(continueAction != null && !continueAction.isEmpty()) {
            final PendingIntent intent = createContinueRequestIntent(timerId);
            final NotificationCompat.Action action = new NotificationCompat.Action(R.drawable.ic_baseline_replay_24, continueAction.toUpperCase(), intent);
            actions.add(action);
        }

        if(cancelAction != null && !cancelAction.isEmpty()) {
            final PendingIntent intent = createCancelRequestIntent(timerId);
            final NotificationCompat.Action action = new NotificationCompat.Action(R.drawable.ic_baseline_clear_24, cancelAction.toUpperCase(), intent);
            actions.add(action);
        }

        if(extendActions != null) {
            for (int extendAction : (List<Integer>) extendActions) {
                final PendingIntent intent = createExtendTimeIntent(timerId, extendAction * 60);
                final NotificationCompat.Action action = new NotificationCompat.Action(R.drawable.ic_baseline_replay_5_24, "+" + extendAction, intent);
                actions.add(action);
            }
        }

        return actions;
    }

    private void showPausingNotification(final TimeNotificationRequest request) {
        final String timerId = request.getTimerId();

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(request.getTitle())
                .setContentText(request.getDescription())
                .setSmallIcon(R.drawable.ic_hourglass_full)
                .setShowWhen(false)
                .setUsesChronometer(false)
                .setContentIntent(createOpenIntent(timerId));

        final List<NotificationCompat.Action> actions = buildNotificationActions(timerId, request.getRestartAction(), request.getPauseAction(), request.getContinueAction(), request.getCancelAction(), request.getExtendActions());
        for (NotificationCompat.Action action : actions) {
            builder.addAction(action);
        }

        final Notification notification = builder.build();
        showNotification(notification);
    }

    private void showElapsedNotification(final NotificationRequest request) {
        final String timerId = request.getTimerId();

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(request.getTitle())
                .setContentText(request.getDescription())
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(request.getDescription()))
                .setSmallIcon(R.drawable.ic_hourglass_full)
                .setShowWhen(false)
                .setUsesChronometer(false)
                .setContentIntent(createOpenIntent(timerId));

        final List<NotificationCompat.Action> actions = buildNotificationActions(timerId, request.getRestartAction(), request.getPauseAction(), request.getContinueAction(), request.getCancelAction(), request.getExtendActions());
        for (NotificationCompat.Action action : actions) {
            builder.addAction(action);
        }

        final Notification notification = builder.build();
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

        final TimerRequest request = new TimerRequest();
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

        final TimerRequest request = new TimerRequest();
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

        final CancelResponse response = new CancelResponse();
        response.setTimerId(timerId);
        response.setSuccess(true);
        intent.putExtra(KEY_CANCEL_REQUEST, response.toMap());

        return PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_CANCEL_REQUEST,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent createExtendTimeIntent(final String timerId, final int additionalTime) {
        final Intent intent = new Intent(context, NotificationActionReceiver.class);
        intent.setAction(ACTION_EXTEND);

        final ExtendTimeResponse response = new ExtendTimeResponse();
        response.setTimerId(timerId);
        response.setAdditionalTime((long) additionalTime);
        intent.putExtra(KEY_EXTEND_RESPONSE, response.toMap());

        return PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_EXTEND + additionalTime,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent createRestartRequestIntent(final String timerId) {
        final Intent intent = new Intent(context, NotificationActionReceiver.class);
        intent.setAction(ACTION_RESTART_REQUEST);

        final TimerRequest request = new TimerRequest();
        request.setTimerId(timerId);
        intent.putExtra(KEY_RESTART_REQUEST, request.toMap());

        return PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_RESTART_REQUEST,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
