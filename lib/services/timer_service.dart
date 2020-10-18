import 'dart:async';

import 'package:android_alarm_manager/android_alarm_manager.dart';
import 'package:injectable/injectable.dart';
import 'package:observable_ish/observable_ish.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:sleep_timer/app/locator.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/model/action_model.dart';
import 'package:sleep_timer/model/timer_model.dart';
import 'package:sleep_timer/services/device_service.dart';
import 'package:stacked/stacked.dart';

@injectable
class TimerService with ReactiveServiceMixin {
  final _deviceService = locator<DeviceService>();
  final TimerModel timerModel;
  Timer _timer;

  Timer get timer => _timer;


  TimerService(@factoryParam this.timerModel)
      : _remainingTime =
            RxValue<int>(initial: timerModel.initialTimeInSeconds) {
    listenToReactiveValues([_remainingTime]);
  }

  final RxValue<int> _remainingTime;
  int get remainingTime => _remainingTime.value;

  void count() {
    _remainingTime.value--;
  }

  void setRemainingTime(int value) {
    _remainingTime.value = value;
  }

  void extendTime(int seconds) {
    _remainingTime.value += seconds;
  }

  void start() {
    const interval = Duration(seconds: 1);

    _timer = Timer.periodic(interval, (timer) {
      remainingTime > 0 ? count() : cancelTimer();
    });
    AndroidAlarmManager.oneShot(Duration(seconds: remainingTime), kAlarmId, alarmCallback,
        allowWhileIdle: true, wakeup: true);

    _deviceService.showRunningNotification(
        timerId: timerModel.id,
        duration: timerModel.initialTimeInSeconds,
        remainingTime: remainingTime);
  }

  void pauseTimer() {
    _deviceService.showPauseNotification(timerId: timerModel.id, remainingTime: remainingTime);
    _disposeTimer();
  }

  void cancelTimer() {
    _deviceService.cancelNotification(timerId: timerModel.id);
    _disposeTimer();
  }

  void _disposeTimer() {
    AndroidAlarmManager.cancel(kAlarmId);
    _timer?.cancel();
    _timer = null;
  }

}

Future<void> alarmCallback(int id) async {
  print("alarm callback for alarm $id");

  // ignore: await_only_futures
  await setupLocator();

  final _prefsService = locator<SharedPreferences>();
  final _deviceService = locator<DeviceService>();

  _deviceService.showElapsedNotification();

  final bool mediaAction =
      _prefsService.getBool(ActionType.MEDIA.toString()) ?? false;
  final bool wifiAction =
      _prefsService.getBool(ActionType.WIFI.toString()) ?? false;
  final bool bluetoothAction =
      _prefsService.getBool(ActionType.BLUETOOTH.toString()) ?? false;
  final bool screenAction =
      _prefsService.getBool(ActionType.SCREEN.toString()) ?? false;
  final bool volumeAction =
      _prefsService.getBool(ActionType.VOLUME.toString()) ?? false;

  if (mediaAction) _deviceService.toggleMedia(false);
  if (wifiAction) _deviceService.toggleWifi(false);
  if (bluetoothAction) _deviceService.toggleBluetooth(false);
  if (screenAction) _deviceService.toggleScreen(false);
  if (volumeAction) _deviceService.setVolume(1, 10);

  // _deviceService.showElapsedNotification();

  // DeviceFunctions.showNotification(kNotificationId);
}
