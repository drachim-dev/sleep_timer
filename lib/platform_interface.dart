import 'package:flutter/foundation.dart';
import 'package:sleep_timer/platform_impl.dart';

abstract class SleepTimerPlatform {
  static SleepTimerPlatform _instance;

  static SleepTimerPlatform getInstance({name, age}) {
    if (_instance == null) {
      _instance = SleepTimerPlatformImpl();
      return _instance;
    }
    return _instance;
  }

  Future<void> init(final int callbackHandle);
  Future<bool> showRunningNotification(
      {@required final String timerId,
      @required final String title,
      @required final String description,
      final String restartAction,
      final String pauseAction,
      final String cancelAction,
      final List<int> extendActions,
      @required final int duration,
      @required final int remainingTime});
  Future<bool> showPausingNotification(
      {@required final String timerId,
      @required final String title,
      @required final String description,
      final String restartAction,
      final String continueAction,
      final String cancelAction,
      final List<int> extendActions,
      @required final int remainingTime});
  Future<bool> showElapsedNotification(
      {@required final String timerId,
      @required final String title,
      @required final String description,
      final String restartAction});
  Future<bool> cancelTimer(final String timerId);
}
