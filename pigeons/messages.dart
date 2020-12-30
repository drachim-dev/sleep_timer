import 'package:pigeon/pigeon.dart';

class InitializationRequest {
  int callbackHandle;
}

class NotificationRequest {
  String timerId, title, description;

  String restartAction;
  String continueAction;
  String pauseAction;
  String cancelAction;
  List<int> extendActions;
}

class TimeNotificationRequest implements NotificationRequest {
  @override
  String description;

  @override
  String timerId;

  @override
  String title;

  @override
  String restartAction;

  @override
  String continueAction;

  @override
  String pauseAction;

  @override
  String cancelAction;

  @override
  List<int> extendActions;

  /// The initial number of minutes the timer was set to.
  int duration;

  /// The number of seconds left for the timer.
  int remainingTime;
}

class NotificationResponse {
  String timerId;
  bool success;
}

class CancelRequest {
  String timerId;
}

class CancelResponse {
  String timerId;
  bool success;
}

class ExtendTimeResponse {
  String timerId;

  /// The time in seconds the timer was extended by.
  int additionalTime;
}

class CountDownRequest {
  String timerId;

  /// The new time after countdown in seconds.
  int newTime;
}

class OpenRequest {
  String timerId;
}

class TimerRequest {
  String timerId;
}

class WidgetUpdateResponse {
  String title;
}

class Package {
  String title, icon, packageName;
}

class InstalledAppsResponse {
  List<Package> apps;
}

class LaunchAppRequest {
  String packageName;
}

// Native methods
@HostApi()
abstract class HostTimerApi {
  void init(InitializationRequest request);
  NotificationResponse showRunningNotification(TimeNotificationRequest request);
  NotificationResponse showPausingNotification(TimeNotificationRequest request);
  NotificationResponse showElapsedNotification(NotificationRequest request);
  CancelResponse cancelTimer(CancelRequest request);
  InstalledAppsResponse getInstalledPlayerApps();
  InstalledAppsResponse getInstalledAlarmApps();
  void launchApp(LaunchAppRequest request);
  Package dummyApp();
}

// Dart methods
@FlutterApi()
abstract class FlutterTimerApi {
  void onExtendTime(ExtendTimeResponse response);
  void onCountDown(CountDownRequest request);
  void onContinueRequest(TimerRequest request);
  void onPauseRequest(TimerRequest request);
  void onCancelRequest(TimerRequest request);
  void onRestartRequest(TimerRequest request);
  void onOpen(OpenRequest request);
  void onAlarm(TimerRequest request);
  WidgetUpdateResponse onWidgetUpdate();
  void onWidgetStartTimer();
}

void configurePigeon(PigeonOptions opts) {
  opts.dartOut = 'lib/messages_generated.dart';
  opts.javaOut = 'android/app/src/main/java/dr/achim/sleep_timer/Messages.java';
  opts.javaOptions.package = 'dr.achim.sleep_timer';
}
