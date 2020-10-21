import 'dart:async';
import 'dart:math';

import 'package:flutter/cupertino.dart';
import 'package:injectable/injectable.dart';
import 'package:observable_ish/observable_ish.dart';
import 'package:sleep_timer/app/locator.dart';
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

  int get maxTime => max(timerModel.initialTimeInSeconds, remainingTime);

  void setRemainingTime(int value) {
    _remainingTime.value = value;
  }

  void start() {
    const interval = Duration(seconds: 1);

    _timer = Timer.periodic(interval, (timer) {
      remainingTime > 0 ? setRemainingTime(remainingTime - 1) : _disposeTimer();
    });

    _deviceService.showRunningNotification(
        timerId: timerModel.id,
        duration: timerModel.initialTimeInSeconds,
        remainingTime: remainingTime);
  }

  void restartTimer() {
    setRemainingTime(timerModel.initialTimeInSeconds);
    start();
  }

  void extendTime(int seconds) {
    _remainingTime.value += seconds;

    _deviceService.showRunningNotification(
        timerId: timerModel.id,
        duration: timerModel.initialTimeInSeconds,
        remainingTime: remainingTime);
  }

  void pauseTimer() {
    _deviceService.showPauseNotification(
        timerId: timerModel.id, remainingTime: remainingTime);
    _disposeTimer();
  }

  void cancelTimer() {
    _deviceService.cancelNotification(timerId: timerModel.id);
    _disposeTimer();
  }

  void _disposeTimer() {
    _timer?.cancel();
    _timer = null;
  }

  Future<void> handleAlarm() async {
    print("handleAlarm()");

    if (timerModel.mediaAction.value) _deviceService.toggleMedia(false);
    if (timerModel.wifiAction.value) _deviceService.toggleWifi(false);
    if (timerModel.bluetoothAction.value) _deviceService.toggleBluetooth(false);
    if (timerModel.screenAction.value) _deviceService.toggleScreen(false);
    if (timerModel.volumeAction.value) _deviceService.setVolume(1, 10);

    _deviceService.showElapsedNotification(timerModel: timerModel);
  }
}

void onDeviceAdminCallback(final bool granted) async {
  WidgetsFlutterBinding.ensureInitialized();

  print('onDeviceAdminGrantedCallback');
}

void onNotificationAccessCallback(final bool granted) async {
  WidgetsFlutterBinding.ensureInitialized();

  print('onNotificationAccessGrantedCallback');
}
