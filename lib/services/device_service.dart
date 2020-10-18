import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:injectable/injectable.dart';
import 'package:observable_ish/observable_ish.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/common/utils.dart';
import 'package:sleep_timer/platform_interface.dart';
import 'package:stacked/stacked.dart';
import 'package:device_functions/device_functions_platform_interface.dart';

@lazySingleton
class DeviceService with ReactiveServiceMixin {
  final DeviceFunctionsPlatform _deviceFunctionsPlatform =
      DeviceFunctionsPlatform.getInstance();

  DeviceService() {
    listenToReactiveValues([_deviceAdmin, _notificationSettingsAccess]);
  }

  RxValue<bool> _deviceAdmin = RxValue<bool>(initial: false);
  bool get deviceAdmin => _deviceAdmin.value;

  RxValue<bool> _notificationSettingsAccess = RxValue<bool>(initial: false);
  bool get notificationSettingsAccess => _notificationSettingsAccess.value;

  Future<void> init() async {
    _deviceAdmin.value =
        await kMethodChannel.invokeMethod("isDeviceAdminActive");
    _notificationSettingsAccess.value =
        await kMethodChannel.invokeMethod("isNotificationSettingsAccessActive");
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

  Future<int> setVolume(final int level, final int maxIndex) {
    return _deviceFunctionsPlatform.setVolume(level, maxIndex);
  }

  Future<void> toggleDeviceAdmin(bool value) async {
    try {
      bool success = await kMethodChannel
          .invokeMethod("toggleDeviceAdmin", {"enabled": value});
      if (success) _deviceAdmin.value = value;
    } on PlatformException catch (e) {
      print(e.message);
    }
  }

  Future<void> toggleNotificationSettingsAccess(bool value) async {
    try {
      bool success = await kMethodChannel
          .invokeMethod("toggleNotificationSettingsAccess", {"enabled": value});
      if (success) _notificationSettingsAccess.value = value;
    } on PlatformException catch (e) {
      print(e.message);
    }
  }

  Future<bool> showRunningNotification(
      {@required final String timerId,
      @required final int duration,
      @required final int remainingTime}) async {

    return SleepTimerPlatform.getInstance().showRunningNotification(
        timerId: timerId,
        title: "Sleep timer running",
        description: "Timer set for ${Utils.secondsToString(duration, trimTrailingZeros: true)} minutes",
        actions: ["Pause", "+5", "+20"],
        duration: duration,
        remainingTime: remainingTime);
  }

  Future<bool> showPauseNotification({@required final String timerId, @required final int remainingTime}) async {

    return SleepTimerPlatform.getInstance().showPausingNotification(
        timerId: timerId,
        title: "Sleep timer pausing",
        description: "Time left: ${Utils.secondsToString(remainingTime, trimTrailingZeros: true)}",
        actions: ["Cancel", "Continue"],
        remainingTime: remainingTime);
  }

  Future<bool> showElapsedNotification() async {
    try {
      bool success =
          await kMethodChannel.invokeMethod("showElapsedNotification", {
        "id": 2,
        "title": "Sleep timer elapsed",
        "subtitle": "Disabled media, bluetooth, wifi",
        "actionTitle1": "Dismiss",
        "actionTitle2": "Restart",
      });
      return success;
    } on PlatformException catch (e) {
      print(e.message);
      return false;
    }
  }

  Future<bool> cancelNotification({@required final String timerId}) async {
    return SleepTimerPlatform.getInstance().cancelNotification(timerId);
  }
}
