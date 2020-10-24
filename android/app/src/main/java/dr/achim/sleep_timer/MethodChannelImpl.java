package dr.achim.sleep_timer;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationManagerCompat;

import dr.achim.sleep_timer.Messages.*;
import io.flutter.Log;

import static dr.achim.sleep_timer.AlarmService.ACTION_START;

public class MethodChannelImpl implements HostTimerApi {
    private static final String TAG = MethodChannelImpl.class.toString();
    private final Context context;

    public MethodChannelImpl(Context context) {
        this.context = context;
    }

    @Override
    public void init(InitializationRequest arg) {
        CallbackHelper.setHandle(context, arg.getCallbackHandle());
    }

    private void _startForegroundService(final TimeNotificationRequest arg) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final Intent intent = new Intent(context, AlarmService.class);
            intent.setAction(ACTION_START);
            intent.putExtra(NotificationReceiver.KEY_SHOW_NOTIFICATION, arg.toMap());
            context.startForegroundService(intent);
        }
    }

    private void _stopForegroundService() {
        if(AlarmService.isRunning()) {
            final Intent intent = new Intent(context, AlarmService.class);
            context.stopService(intent);
        }
    }

    @Override
    public NotificationResponse showRunningNotification(TimeNotificationRequest arg) {
        final NotificationResponse response = new NotificationResponse();
        final String timerId = arg.getTimerId();
        if(timerId == null) {
            response.setSuccess(false);
            return response;
        }

        Log.wtf(TAG, "Request to show running notification for timer with id " + timerId);

        _startForegroundService(arg);
        // Notification will be triggered by broadcast when foreground service is ready

        response.setTimerId(timerId);
        response.setSuccess(true);
        return response;
    }

    @Override
    public NotificationResponse showPausingNotification(TimeNotificationRequest arg) {
        final NotificationResponse response = new NotificationResponse();
        final String timerId = arg.getTimerId();
        if(timerId == null) {
            response.setSuccess(false);
            return response;
        }

        Log.wtf(TAG, "Request to show pause notification for timer with id " + timerId);

        _stopForegroundService();

        final Intent intent = new Intent(context, NotificationReceiver.class);
        intent.setAction(NotificationReceiver.ACTION_PAUSE_NOTIFICATION);
        intent.putExtra(NotificationReceiver.KEY_PAUSE_NOTIFICATION, arg.toMap());
        context.sendBroadcast(intent);

        response.setTimerId(timerId);
        response.setSuccess(true);
        return response;
    }

    @Override
    public NotificationResponse showElapsedNotification(NotificationRequest arg) {
        final NotificationResponse response = new NotificationResponse();
        final String timerId = arg.getTimerId();
        if(timerId == null) {
            response.setSuccess(false);
            return response;
        }

        Log.wtf(TAG, "Request to show elapsed notification for timer with id " + timerId);

        _stopForegroundService();

        final Intent intent = new Intent(context, NotificationReceiver.class);
        intent.setAction(NotificationReceiver.ACTION_ELAPSED_NOTIFICATION);
        intent.putExtra(NotificationReceiver.KEY_ELAPSED_NOTIFICATION, arg.toMap());
        context.sendBroadcast(intent);

        response.setTimerId(timerId);
        response.setSuccess(true);
        return response;
    }

    @Override
    public CancelResponse cancelTimer(CancelRequest arg) {
        final CancelResponse response = new CancelResponse();
        final String timerId = arg.getTimerId();
        if(timerId == null) {
            response.setSuccess(false);
            return response;
        }

        Log.wtf(TAG, "Request to cancel notification for timer with id " + timerId);

        _stopForegroundService();
        NotificationManagerCompat.from(context).cancel(NotificationReceiver.NOTIFICATION_ID);

        response.setTimerId(timerId);
        response.setSuccess(true);
        return response;
    }

}