import 'package:pigeon/pigeon.dart';

class InitializationRequest {
  int callbackHandle;
}


class ShowRunningNotificationRequest {
  String timerId, title, description;

  List<String> actions;

  /// The initial number of seconds the timer was set to.
  int duration;

  /// The number of seconds left for the timer.
  int remainingTime;

}

class ShowPausingNotificationRequest {
  String timerId, title, description;

  List<String> actions;

  /// The number of seconds left for the timer.
  int remainingTime;
}

class ShowElapsedNotificationRequest {
  String timerId, title, description;
}

class ShowNotificationResponse {
  String timerId;
  bool success;
}

class CancelNotificationRequest {
  String timerId;
}

class CancelNotificationResponse {
  String timerId;
  bool success;
}

class ExtendTimeResponse {
  String timerId;

  /// The time in seconds the timer was extended by,
  int additionalTime;
}

class OpenRequest {
  String timerId;
}

class PauseRequest {
  String timerId;
}

class ContinueRequest {
  String timerId;
}

class CancelRequest {
  String timerId;
}

// Native methods
@HostApi()
abstract class HostTimerApi {
  void init(InitializationRequest request);
  ShowNotificationResponse showRunningNotification(
      ShowRunningNotificationRequest request);
  ShowNotificationResponse showPausingNotification(
      ShowPausingNotificationRequest request);
  ShowNotificationResponse showElapsedNotification(
      ShowElapsedNotificationRequest request);
  CancelNotificationResponse cancelNotification(
      CancelNotificationRequest request);
}

// Dart methods
@FlutterApi()
abstract class FlutterTimerApi {
  void onShowRunningNotification(ShowNotificationResponse response);
  void onExtendTime(ExtendTimeResponse response);
  void onContinueRequest(ContinueRequest request);
  void onPauseRequest(PauseRequest request);
  void onCancelRequest(CancelRequest request);
  void onOpen(OpenRequest request);
}

void configurePigeon(PigeonOptions opts) {
  opts.dartOut = 'lib/messages_generated.dart';
  opts.javaOut =
      'android/app/src/main/java/dr/achim/sleep_timer/Messages.java';
  opts.javaOptions.package = 'dr.achim.sleep_timer';
}
