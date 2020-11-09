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

enum TimerStatus { INITIAL, PAUSING, RUNNING, ELAPSED }

@injectable
class TimerService with ReactiveServiceMixin {
  final Logger log = getLogger();
  final _deviceService = locator<DeviceService>();
  final TimerModel timerModel;
  Timer _timer;

  final RxValue<TimerStatus> _status = RxValue(initial: TimerStatus.INITIAL);
  TimerStatus get status => _status.value;

  TimerService(@factoryParam this.timerModel)
      : _remainingTime = RxValue<int>(initial: timerModel.initialTimeInSeconds),
        _maxTime = timerModel.initialTimeInSeconds {
    listenToReactiveValues([_remainingTime, _status]);
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

    if (status == TimerStatus.INITIAL) {
      handleStartActions();
    }
    _status.value = TimerStatus.RUNNING;

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
    _status.value = TimerStatus.PAUSING;
    _deviceService.showPauseNotification(
        timerId: timerModel.id, remainingTime: remainingTime);
    _disposeTimer();
  }

  Future<void> cancelTimer() async {
    _status.value = TimerStatus.PAUSING;
    _deviceService.cancelNotification(timerId: timerModel.id);
    _disposeTimer();
  }

  void _disposeTimer() {
    _timer?.cancel();
    _timer = null;
  }

  Future<void> handleStartActions() async {
    if (timerModel.volumeAction.enabled)
      _deviceService.setVolume(timerModel.volumeAction.value.truncate());

    if (timerModel.playMusicAction.enabled) print("Play music");
    //_deviceService.setVolume(timerModel.volumeAction.value.truncate());

    if (timerModel.doNotDisturbAction.enabled &&
        _deviceService.notificationSettingsAccess)
      _deviceService.toggleDoNotDisturb(true);
  }

  Future<void> handleEndedActions() async {
    log.d("handleEndedActions()");

    if (timerModel.mediaAction.enabled) _deviceService.toggleMedia(false);
    if (timerModel.wifiAction.enabled && _deviceService.platformVersion < 29)
      _deviceService.toggleWifi(false);
    if (timerModel.bluetoothAction.enabled)
      _deviceService.toggleBluetooth(false);
    if (timerModel.screenAction.enabled && _deviceService.deviceAdmin)
      _deviceService.toggleScreen(false);

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
