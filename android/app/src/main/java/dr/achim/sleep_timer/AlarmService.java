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
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import dr.achim.sleep_timer.Messages.*;

import java.util.Calendar;
import java.util.HashMap;

import static dr.achim.sleep_timer.NotificationReceiver.*;

public class AlarmService extends Service {
    private static final String TAG = AlarmService.class.toString();
    private static boolean isRunning;

    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_STOP = "ACTION_STOP";
    private static final int REQUEST_CODE_ALARM = 700;
    private PendingIntent pendingAlarmIntent;

    @Override
    public void onCreate() {
        super.onCreate();

        initNotificationChannel();

        final Notification notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID).build();
        startForeground(NOTIFICATION_ID, notification);
        Log.wtf(TAG, "AlarmService onCreate");
    }

    private void initNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final int importance = NotificationManager.IMPORTANCE_DEFAULT;
            final NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Active timer", importance);
            notificationChannel.setDescription("Notify about running or pausing timers");
            NotificationManagerCompat.from(this).createNotificationChannel(notificationChannel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isRunning = true;

        Log.wtf(TAG, intent.getAction());
        switch (intent.getAction()) {
            case ACTION_START:
                final HashMap map = (HashMap) intent.getSerializableExtra(KEY_SHOW_NOTIFICATION);
                final ShowRunningNotificationRequest request = ShowRunningNotificationRequest.fromMap(map);
                _startAlarm(request);
                final Intent showRunningIntent = new Intent(this, NotificationReceiver.class);
                showRunningIntent.setAction(ACTION_SHOW_RUNNING);
                showRunningIntent.putExtra(KEY_SHOW_NOTIFICATION, map);
                sendBroadcast(showRunningIntent);
                break;
            case ACTION_STOP:
                stopForeground(true);
                stopSelf();
            default:
                if(isRunning()) {
                    isRunning = false;
                    stopForeground(true);
                    stopSelf();
                }
                break;
        }

        return START_STICKY;
    }

    private void _startAlarm(Messages.ShowRunningNotificationRequest request) {
        final Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        alarmIntent.putExtra(NotificationReceiver.KEY_TIMER_ID, request.getTimerId());
        pendingAlarmIntent = PendingIntent.getBroadcast(this, REQUEST_CODE_ALARM, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        final AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + request.getRemainingTime() * 1000, pendingAlarmIntent);
    }

    private void _stopAlarm() {
        if(pendingAlarmIntent != null) {
            final AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            manager.cancel(pendingAlarmIntent);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onDestroy() {
        Log.wtf(TAG, "onDestroy Service");
        _stopAlarm();
        isRunning = false;
    }
    public static boolean isRunning() {
        return isRunning;
    }

}