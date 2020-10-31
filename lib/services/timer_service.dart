import 'dart:async';
import 'dart:math';

import 'package:flutter/cupertino.dart';
import 'package:injectable/injectable.dart';
import 'package:logger/logger.dart';
import 'package:observable_ish/observable_ish.dart';
import 'package:sleep_timer/app/locator.dart';
import 'package:sleep_timer/app/logger.util.dart';
import 'package:sleep_timer/model/timer_model.dart';
import 'package:sleep_timer/services/device_service.dart';
import 'package:stacked/stacked.dart';

@injectable
class TimerService with ReactiveServiceMixin {
  final Logger log = getLogger();
  final _deviceService = locator<DeviceService>();
  final TimerModel timerModel;
  Timer _timer;

  final RxValue<bool> _isActive = RxValue(initial: true);
  bool get isActive => _isActive.value;

  TimerService(@factoryParam this.timerModel)
      : _remainingTime = RxValue<int>(initial: timerModel.initialTimeInSeconds),
        _maxTime = timerModel.initialTimeInSeconds {
    listenToReactiveValues([_remainingTime, _isActive]);
  }

  final RxValue<int> _remainingTime;
  int get remainingTime => _remainingTime.value;

  void setRemainingTime(final int value) {
    _remainingTime.value = value;
  }

  int _maxTime;
  int get maxTime => _maxTime;

  void start() {
    const interval = Duration(seconds: 1);

    _timer = Timer.periodic(interval, (timer) {
      remainingTime > 0 ? setRemainingTime(remainingTime - 1) : _disposeTimer();
    });
    _isActive.value = true;

    _deviceService.showRunningNotification(
        timerId: timerModel.id,
        duration: timerModel.initialTimeInSeconds,
        remainingTime: remainingTime);
  }

  void restartTimer() {
    setRemainingTime(timerModel.initialTimeInSeconds);
    start();
  }

  void extendTime(final int seconds) {
    _remainingTime.value += seconds;

    setMaxTime();

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

  Future<void> cancelTimer() async {
    _deviceService.cancelNotification(timerId: timerModel.id);
    _disposeTimer();
  }

  void _disposeTimer() {
    _timer?.cancel();
    _timer = null;
    _isActive.value = false;
  }

  Future<void> handleAlarm() async {
    log.i("handleAlarm()");

    if (timerModel.mediaAction.enabled) _deviceService.toggleMedia(false);
    if (timerModel.wifiAction.enabled) _deviceService.toggleWifi(false);
    if (timerModel.bluetoothAction.enabled)
      _deviceService.toggleBluetooth(false);
    if (timerModel.screenAction.enabled && _deviceService.deviceAdmin)
      _deviceService.toggleScreen(false);
    if (timerModel.volumeAction.enabled)
      _deviceService.setVolume(timerModel.volumeAction.value.truncate());
    if (timerModel.doNotDisturbAction.enabled &&
        _deviceService.notificationSettingsAccess)
      _deviceService.toggleDoNotDisturb(true);

    _deviceService.showElapsedNotification(timerModel: timerModel);
  }

  void setMaxTime() =>
      _maxTime = max(timerModel.initialTimeInSeconds, remainingTime);
}

void onDeviceAdminCallback(final bool granted) async {
  final Logger log = getLogger();
  log.d("onDeviceAdminGrantedCallback");

  WidgetsFlutterBinding.ensureInitialized();
}

void onNotificationAccessCallback(final bool granted) async {
  final Logger log = getLogger();
  log.d("onNotificationAccessGrantedCallback");

  WidgetsFlutterBinding.ensureInitialized();
}
