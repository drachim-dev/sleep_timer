import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:injectable/injectable.dart';
import 'package:observable_ish/observable_ish.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/common/utils.dart';
import 'package:stacked/stacked.dart';

@lazySingleton
class DeviceService with ReactiveServiceMixin {
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
      {@required int initialTime, @required int remainingTime}) async {
    try {
      bool success =
          await kMethodChannel.invokeMethod("showRunningNotification", {
        "id": kNotificationId,
        "title": "Sleep timer running",
        "subtitle":
            "Timer set for ${Utils.secondsToString(initialTime, trimTrailingZeros: true)} minutes",
        "seconds": remainingTime,
        "actionTitle1": "Pause",
        "actionTitle2": "+5",
        "actionTitle3": "+20",
      });
      return success;
    } on PlatformException catch (e) {
      print(e.message);
      return false;
    }
  }

  Future<bool> showPauseNotification(int remainingTime) async {
    try {
      bool success =
          await kMethodChannel.invokeMethod("showPauseNotification", {
        "id": kNotificationId,
        "title": "Sleep timer pausing",
        "subtitle":
            "Time left: ${Utils.secondsToString(remainingTime, trimTrailingZeros: true)}",
        "seconds": remainingTime,
        "actionTitle1": "Cancel",
        "actionTitle2": "Continue",
      });
      return success;
    } on PlatformException catch (e) {
      print(e.message);
      return false;
    }
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

  Future<bool> cancelNotification() async {
    try {
      bool success = await kMethodChannel.invokeMethod("cancelNotification", {
        "id": kNotificationId,
      });
      return success;
    } on PlatformException catch (e) {
      print(e.message);
      return false;
    }
  }

}
