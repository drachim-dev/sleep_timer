package dr.achim.sleep_timer;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import dr.achim.sleep_timer.Messages.*;
import io.flutter.Log;


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

    @Override
    public ShowNotificationResponse showRunningNotification(ShowRunningNotificationRequest arg) {
        final ShowNotificationResponse response = new ShowNotificationResponse();
        final String timerId = arg.getTimerId();
        if(timerId == null) {
            response.setSuccess(false);
            return response;
        }

        Log.wtf(TAG, "Request to show notification for timer with id " + timerId);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent intent = new Intent(context, AlarmService.class);
            intent.setAction(AlarmService.ACTION_START);
            intent.putExtra(AlarmService.KEY_SHOW_NOTIFICATION, arg.toMap());
            context.startForegroundService(intent);
        }

        response.setTimerId(timerId);
        response.setSuccess(true);
        return response;
    }

    @Override
    public ShowNotificationResponse showPausingNotification(ShowPausingNotificationRequest arg) {
        final ShowNotificationResponse response = new ShowNotificationResponse();
        final String timerId = arg.getTimerId();
        if(timerId == null) {
            response.setSuccess(false);
            return response;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent intent = new Intent(context, AlarmService.class);
            intent.setAction(AlarmService.ACTION_PAUSE_NOTIFICATION);
            intent.putExtra(AlarmService.KEY_PAUSE_NOTIFICATION, arg.toMap());
            context.startForegroundService(intent);
        }

        response.setTimerId(timerId);
        response.setSuccess(true);
        return response;
    }

    @Override
    public ShowNotificationResponse showElapsedNotification(ShowElapsedNotificationRequest arg) {
        final ShowNotificationResponse response = new ShowNotificationResponse();
        final String timerId = arg.getTimerId();
        if(timerId == null) {
            response.setSuccess(false);
            return response;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent intent = new Intent(context, AlarmService.class);
            intent.setAction(AlarmService.ACTION_ELAPSE_NOTIFICATION);
            intent.putExtra(AlarmService.KEY_ELAPSE_NOTIFICATION, arg.toMap());
            context.startForegroundService(intent);
        }

        Log.wtf(TAG, "Request to show elapsed notification for timer with id " + timerId);
        response.setTimerId(timerId);
        response.setSuccess(true);
        return response;
    }

    @Override
    public CancelNotificationResponse cancelNotification(CancelNotificationRequest arg) {
        final CancelNotificationResponse response = new CancelNotificationResponse();
        final String timerId = arg.getTimerId();
        if(timerId == null) {
            response.setSuccess(false);
            return response;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent intent = new Intent(context, AlarmService.class);
            intent.setAction(AlarmService.ACTION_CANCEL_NOTIFICATION);
            intent.putExtra(AlarmService.KEY_CANCEL_NOTIFICATION, arg.toMap());
            context.startForegroundService(intent);
        }

        Log.wtf(TAG, "Request to cancel notification for timer with id " + timerId);
        response.setTimerId(timerId);
        response.setSuccess(true);
        return response;
    }

}
