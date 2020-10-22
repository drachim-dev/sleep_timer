import 'package:flutter/foundation.dart';
import 'package:injectable/injectable.dart';
import 'package:observable_ish/observable_ish.dart';
import 'package:sleep_timer/common/utils.dart';
import 'package:sleep_timer/model/action_model.dart';
import 'package:sleep_timer/model/timer_model.dart';
import 'package:sleep_timer/platform_interface.dart';
import 'package:stacked/stacked.dart';
import 'package:device_functions/platform_interface.dart';

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
    _deviceAdmin.value = await _deviceFunctionsPlatform.isDeviceAdminActive();
    _notificationSettingsAccess.value = await _deviceFunctionsPlatform.isNotificationAccessGranted();
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
    final String time =
        Utils.secondsToString(duration, trimTrailingZeros: true);
    final String unit = time == "1" ? "minute" : "minutes";

    return SleepTimerPlatform.getInstance().showRunningNotification(
        timerId: timerId,
        title: "Sleep timer running",
        description: "Timer set for $time $unit",
        actions: ["Pause", "+5", "+20"],
        duration: duration,
        remainingTime: remainingTime);
  }

  Future<bool> showPauseNotification(
      {@required final String timerId,
      @required final int remainingTime}) async {
    return SleepTimerPlatform.getInstance().showPausingNotification(
        timerId: timerId,
        title: "Sleep timer pausing",
        description:
            "Time left: ${Utils.secondsToString(remainingTime, trimTrailingZeros: true)}",
        actions: ["Cancel", "Continue"],
        remainingTime: remainingTime);
  }

  Future<bool> showElapsedNotification(
      {@required final TimerModel timerModel}) async {
    final String time = Utils.secondsToString(timerModel.initialTimeInSeconds,
        trimTrailingZeros: true);
    final String unit = time == "1" ? "minute has" : "minutes have";

    String description = "$time $unit expired. ";
    final Iterable<ActionModel> activeActions =
        timerModel.actions.where((element) => element.enabled == true);
    if (activeActions.isEmpty) {
      description += "No actions selected for execution.";
    } else {
      description += "Toggle ";

      for (var i = 0; i < activeActions.length; i++) {
        final element = activeActions.elementAt(i);
        description += "${element.title}";

        // Add separator, if it's not the last element
        if (i != activeActions.length - 1) {
          description += ", ";
        }
      }
    }

    return SleepTimerPlatform.getInstance().showElapsedNotification(
        timerId: timerModel.id,
        title: "Sleep timer elapsed",
        description: description,
        actions: ["Restart"]);
  }

  Future<bool> cancelNotification({@required final String timerId}) async {
    return SleepTimerPlatform.getInstance().cancelNotification(timerId);
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

}
