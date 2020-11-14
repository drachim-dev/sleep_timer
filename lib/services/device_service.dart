import 'package:device_functions/messages_generated.dart';
import 'package:device_functions/platform_interface.dart';
import 'package:flutter/foundation.dart';
import 'package:injectable/injectable.dart';
import 'package:observable_ish/observable_ish.dart';
import 'package:sleep_timer/common/utils.dart';
import 'package:sleep_timer/generated/l10n.dart';
import 'package:sleep_timer/model/action_model.dart';
import 'package:sleep_timer/model/app.dart';
import 'package:sleep_timer/model/timer_model.dart';
import 'package:sleep_timer/platform_interface.dart';
import 'package:stacked/stacked.dart';

@lazySingleton
class DeviceService with ReactiveServiceMixin {
  final DeviceFunctionsPlatform _deviceFunctionsPlatform =
      DeviceFunctionsPlatform.getInstance();

  DeviceService() {
    listenToReactiveValues([_deviceAdmin, _notificationSettingsAccess]);
  }

  int _platformVersion = 0;
  int get platformVersion => _platformVersion;

  RxValue<bool> _deviceAdmin = RxValue<bool>(initial: false);
  bool get deviceAdmin => _deviceAdmin.value;

  Future<VolumeResponse> get volume => _deviceFunctionsPlatform.getVolume();

  RxValue<bool> _notificationSettingsAccess = RxValue<bool>(initial: false);
  bool get notificationSettingsAccess => _notificationSettingsAccess.value;

  Future<List<App>> get playerApps async =>
      SleepTimerPlatform.getInstance().getInstalledPlayerApps();

  Future<List<App>> get alarmApps async =>
      SleepTimerPlatform.getInstance().getInstalledAlarmApps();

  Future<void> init() async {
    _platformVersion = await _deviceFunctionsPlatform.getPlatformVersion();
    _deviceAdmin.value = await _deviceFunctionsPlatform.isDeviceAdminActive();
    _notificationSettingsAccess.value =
        await _deviceFunctionsPlatform.isNotificationAccessGranted();
  }

  Future<bool> toggleMedia(final bool enable) {
    return _deviceFunctionsPlatform.toggleMedia(enable);
  }

  Future<bool> toggleWifi(final bool enable) {
    return _deviceFunctionsPlatform.toggleWifi(enable);
  }

  Future<bool> toggleBluetooth(final bool enable) {
    return _deviceFunctionsPlatform.toggleBluetooth(enable);
  }

  Future<bool> toggleScreen(final bool enable) {
    return _deviceFunctionsPlatform.toggleScreen(enable);
  }

  Future<VolumeResponse> setVolume(final int level) {
    return _deviceFunctionsPlatform.setVolume(level);
  }

  Future<bool> toggleDoNotDisturb(final bool enable) {
    return _deviceFunctionsPlatform.toggleDoNotDisturb(enable);
  }

  Future<void> toggleDeviceAdmin(bool enable) async {
    await _deviceFunctionsPlatform.toggleDeviceAdmin(enable);
  }

  Future<void> toggleNotificationSettingsAccess(bool enable) async {
    await _deviceFunctionsPlatform.toggleNotificationAccess(enable);
  }

  Future<bool> showRunningNotification(
      {@required final String timerId,
      @required final int duration,
      @required final int remainingTime}) async {
    final String durationString =
        Utils.secondsToString(duration, trimTrailingZeros: true);

    return SleepTimerPlatform.getInstance().showRunningNotification(
        timerId: timerId,
        title: S.current.notificationStatusRunning,
        description: S.current.notificationTimerSet(durationString),
        pauseAction: S.current.notificationActionPause,
        extendActions: [5, 20],
        duration: duration,
        remainingTime: remainingTime);
  }

  Future<bool> showPauseNotification(
      {@required final String timerId,
      @required final int remainingTime}) async {
final String timeLeft = Utils.secondsToString(remainingTime, trimTrailingZeros: true);

    return SleepTimerPlatform.getInstance().showPausingNotification(
        timerId: timerId,
        title: S.current.notificationStatusPausing,
        description:
            S.current.notificationTimeLeft(timeLeft),
        cancelAction: S.current.notificationActionCancel,
        continueAction: S.current.notificationActionContinue,
        remainingTime: remainingTime);
  }

  Future<bool> showElapsedNotification(
      {@required final TimerModel timerModel}) async {
    final String durationString = Utils.secondsToString(
        timerModel.initialTimeInSeconds,
        trimTrailingZeros: true);
    final String time = S.current.unitMinute(durationString);

    String description = S.current.notificationTimeExpired(time);
    final Iterable<ActionModel> activeActions =
        timerModel.actions.where((element) => element.enabled);
    if (activeActions.isEmpty) {
      description += S.current.notificationNoActionsExecuted;
    } else {
      for (var i = 0; i < activeActions.length; i++) {
        final element = activeActions.elementAt(i);
        description += "${element.description}. ";
      }
    }

    return SleepTimerPlatform.getInstance().showElapsedNotification(
        timerId: timerModel.id,
        title: S.current.notificationStatusElapsed,
        description: description,
        restartAction: S.current.notificationActionRestart);
  }

  Future<bool> cancelNotification({@required final String timerId}) async {
    return SleepTimerPlatform.getInstance().cancelTimer(timerId);
  }

  // Async response not yet possible with pigeon.
  // Workaround by using @FlutterApi() for callback.
  void setDeviceAdmin(final bool granted) {
    _deviceAdmin.value = granted;
  }

  // Async response not yet possible with pigeon.
  // Workaround by using @FlutterApi() for callback.
  void setNotificationAccess(final bool granted) {
    _notificationSettingsAccess.value = granted;
  }

  Future<void> openPackage(final String packageName) async {
    return SleepTimerPlatform.getInstance().launchApp(packageName);
  }
}
