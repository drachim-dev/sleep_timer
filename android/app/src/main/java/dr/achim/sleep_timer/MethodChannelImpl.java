package dr.achim.sleep_timer;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.AlarmClock;
import android.provider.MediaStore;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    @Override
    public InstalledAppsResponse getInstalledPlayerApps() {
        final InstalledAppsResponse response = new InstalledAppsResponse();

        final PackageManager manager = context.getPackageManager();
        Intent intentAudio = new Intent(Intent.ACTION_VIEW);
        Uri uriAudio = Uri.withAppendedPath(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, "1");
        intentAudio.setDataAndType(uriAudio, "audio/*");
        List<ResolveInfo> audioList = manager.queryIntentActivities(intentAudio, 0);

        Intent intentVideo = new Intent(Intent.ACTION_VIEW);
        Uri uriVideo = Uri.withAppendedPath(MediaStore.Video.Media.INTERNAL_CONTENT_URI, "1");
        intentVideo.setDataAndType(uriVideo, "video/*");
        List<ResolveInfo> videoList = manager.queryIntentActivities(intentVideo, 0);

        Intent intentMedia = new Intent(Intent.ACTION_MEDIA_BUTTON);
        List<ResolveInfo> mediaList = manager.queryBroadcastReceivers(intentMedia, 0);

        List<ResolveInfo> playerList = new ArrayList<>(audioList);
        playerList.addAll(videoList);
        playerList.addAll(mediaList);

        final ArrayList apps = getAppsFromResolveInfo(manager, playerList);
        response.setApps(apps);
        return response;
    }

    @Override
    public InstalledAppsResponse getInstalledAlarmApps() {
        final InstalledAppsResponse response = new InstalledAppsResponse();

        final PackageManager manager = context.getPackageManager();

        Intent showAlarmsIntent = new Intent(AlarmClock.ACTION_SHOW_ALARMS);
        List<ResolveInfo> alarmList = manager.queryIntentActivities(showAlarmsIntent, 0);

        final ArrayList apps = getAppsFromResolveInfo(manager, alarmList);
        response.setApps(apps);
        return response;
    }

    @NotNull
    private ArrayList getAppsFromResolveInfo(PackageManager manager, List<ResolveInfo> playerList) {
        Set<String> distinctAppSet = new HashSet<>();
        ArrayList apps = new ArrayList();
        for(ResolveInfo info : playerList) {
            ActivityInfo activity = info.activityInfo;
            ApplicationInfo applicationInfo = activity.applicationInfo;

            if (applicationInfo == null || !applicationInfo.enabled || distinctAppSet.contains(applicationInfo.packageName)) {
                continue;
            }

            // https://stackoverflow.com/questions/51368075/how-can-i-get-android-drawables-in-flutter
            final Bitmap bitmap = getBitmapFromDrawable(applicationInfo.loadIcon(manager));
            final String encoded = getBase64FromBitmap(bitmap);

            Messages.Package app = new Messages.Package();
            app.setPackageName(applicationInfo.packageName);
            app.setTitle(applicationInfo.loadLabel(manager).toString());
            app.setIcon(encoded);

            distinctAppSet.add(applicationInfo.packageName);
            apps.add(app.toMap());
        }
        return apps;
    }

    @Override
    public void launchApp(final LaunchAppRequest request) {
        final PackageManager manager = context.getPackageManager();
        final Intent intent = manager.getLaunchIntentForPackage(request.getPackageName());
        context.startActivity(intent);
    }

    @Override
    public Messages.Package dummyApp() {
        return null;
    }

    // https://stackoverflow.com/questions/44447056/convert-adaptiveicondrawable-to-bitmap-in-android-o-preview
    @NonNull
    private Bitmap getBitmapFromDrawable(@NonNull Drawable drawable) {
        final Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    // https://stackoverflow.com/questions/9224056/android-bitmap-to-base64-string/9224180#9224180
    @NonNull
    private String getBase64FromBitmap(@NonNull Bitmap bitmap) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        final byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }

}