import 'package:sleep_timer/platform_impl.dart';
import 'package:sleep_timer/messages_generated.dart';

abstract class SleepTimerPlatform {
  static final SleepTimerPlatform _instance = SleepTimerPlatformImpl();
  static SleepTimerPlatform get instance => _instance;

  Future<bool> showRunningNotification(
      {required final String timerId,
      required final String title,
      required final String description,
      required final int accentColor,
      final String restartAction,
      final String pauseAction,
      final String cancelAction,
      final List<int> extendActions,
      required final int duration,
      required final int remainingTime,
      required final bool shakeToExtend});
  Future<bool> showPausingNotification(
      {required final String timerId,
      required final String title,
      required final String description,
      required final int accentColor,
      final String restartAction,
      final String continueAction,
      final String cancelAction,
      final List<int> extendActions,
      required final int remainingTime});
  Future<bool> showElapsedNotification(
      {required final String timerId,
      required final String title,
      required final String description,
      required final int accentColor,
      final String restartAction});
  Future<bool> cancelTimer(final String timerId);
  Future<void> toggleExtendByShake(final bool enable);
  Future<List<Package>> getInstalledPlayerApps();
  Future<List<Package>> getInstalledAlarmApps();
  Future<void> launchApp(String packageName);
}
