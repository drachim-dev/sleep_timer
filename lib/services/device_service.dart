import 'package:device_functions/messages_generated.dart';
import 'package:device_functions/platform_interface.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:injectable/injectable.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/common/utils.dart';
import 'package:sleep_timer/generated/l10n.dart';
import 'package:sleep_timer/model/app.dart';
import 'package:sleep_timer/model/timer_model.dart';
import 'package:sleep_timer/platform_interface.dart';
import 'package:stacked/stacked.dart';

@singleton
class DeviceService with ReactiveServiceMixin {
  final DeviceFunctionsPlatform _deviceFunctionsPlatform =
      DeviceFunctionsPlatform.getInstance();

  DeviceService() {
    listenToReactiveValues([_deviceAdmin, _notificationSettingsAccess]);
  }

  DeviceInfoResponse _deviceInfo;
  int get platformVersion => _deviceInfo.platformVersion ?? 0;
  String get deviceManufacturer => _deviceInfo.deviceManufacturer ?? '';
  String get deviceModel => _deviceInfo.deviceModel ?? '';

  bool _hasAccelerometer = true;
  bool get hasAccelerometer => _hasAccelerometer;

  bool _deviceAdmin = false;
  bool get deviceAdmin => _deviceAdmin;

  Future<VolumeResponse> get volume => _deviceFunctionsPlatform.getVolume();

  bool _notificationSettingsAccess = false;
  bool get notificationSettingsAccess => _notificationSettingsAccess;

  Future<List<App>> get playerApps async =>
      SleepTimerPlatform.getInstance().getInstalledPlayerApps();

  Future<List<App>> get alarmApps async =>
      SleepTimerPlatform.getInstance().getInstalledAlarmApps();

  @factoryMethod
  static Future<DeviceService> create() async {
    var instance = DeviceService();
    await instance.init();
    return instance;
  }

  Future<void> init() async {
    _deviceInfo = await _deviceFunctionsPlatform.getDeviceInfo();
    _hasAccelerometer = await _deviceFunctionsPlatform.hasAccelerometer();
    _deviceAdmin = await _deviceFunctionsPlatform.isDeviceAdminActive();
    _notificationSettingsAccess =
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
      @required final int remainingTime,
      @required final bool shakeToExtend}) async {
    final durationString =
        Utils.secondsToString(duration, trimTrailingZeros: true);

    return SleepTimerPlatform.getInstance().showRunningNotification(
        timerId: timerId,
        title: S.current.notificationStatusRunning,
        description: S.current.notificationTimerSet(durationString),
        accentColor: kNotificationActionColor.value,
        pauseAction: S.current.notificationActionPause,
        extendActions: [5, 20],
        duration: duration,
        remainingTime: remainingTime,
        shakeToExtend: shakeToExtend);
  }

  Future<bool> showPauseNotification(
      {@required final String timerId,
      @required final int remainingTime}) async {
    final timeLeft =
        Utils.secondsToString(remainingTime, trimTrailingZeros: true);

    return SleepTimerPlatform.getInstance().showPausingNotification(
        timerId: timerId,
        title: S.current.notificationStatusPausing,
        description: S.current.notificationTimeLeft(timeLeft),
        accentColor: kNotificationActionColor.value,
        cancelAction: S.current.notificationActionCancel,
        continueAction: S.current.notificationActionContinue,
        remainingTime: remainingTime);
  }

  Future<bool> showElapsedNotification(
      {@required final TimerModel timerModel}) async {
    var description = S.current.notificationTimeExpired;
    final activeActions =
        timerModel.endActions.where((element) => element.enabled);
    if (activeActions.isEmpty) {
      description += S.current.notificationNoActionsExecuted;
    } else {
      for (var i = 0; i < activeActions.length; i++) {
        final element = activeActions.elementAt(i);
        description += '${element.description}. ';
      }
    }

    return SleepTimerPlatform.getInstance().showElapsedNotification(
        timerId: timerModel.id,
        title: S.current.notificationStatusElapsed,
        description: description,
        accentColor: kNotificationActionColor.value,
        restartAction: S.current.notificationActionRestart);
  }

  Future<bool> cancelNotification({@required final String timerId}) async {
    return SleepTimerPlatform.getInstance().cancelTimer(timerId);
  }

  // Async response not yet possible with pigeon.
  // Workaround by using @FlutterApi() for callback.
  void setDeviceAdmin(final bool granted) {
    _deviceAdmin = granted;
    notifyListeners();
  }

  // Async response not yet possible with pigeon.
  // Workaround by using @FlutterApi() for callback.
  void setNotificationAccess(final bool granted) {
    _notificationSettingsAccess = granted;
    notifyListeners();
  }

  Future<void> openPackage(final String packageName) async {
    return SleepTimerPlatform.getInstance().launchApp(packageName);
  }
}
