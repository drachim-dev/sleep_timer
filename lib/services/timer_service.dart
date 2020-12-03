import 'dart:async';
import 'dart:math';

import 'package:flutter/cupertino.dart';
import 'package:injectable/injectable.dart';
import 'package:logger/logger.dart';
import 'package:observable_ish/observable_ish.dart';
import 'package:sleep_timer/app/locator.dart';
import 'package:sleep_timer/app/logger.util.dart';
import 'package:sleep_timer/common/constants.dart';
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

    if (status == TimerStatus.ELAPSED) {
      _resetTime();
    }

    _timer = Timer.periodic(interval, (timer) {
      remainingTime > 0 ? setRemainingTime(remainingTime - 1) : _elapseTimer();
    });

    if (status == TimerStatus.INITIAL) {
      handleStartActions();
    }
    _status.value = TimerStatus.RUNNING;

    _deviceService.showRunningNotification(
        timerId: timerModel.id,
        duration: maxTime,
        remainingTime: remainingTime);
  }

  void restartTimer() {
    setRemainingTime(timerModel.initialTimeInSeconds);
    start();
  }

  void extendTime(final int seconds) {
    _remainingTime.value += seconds ?? kDefaultExtendTimeByShake * 60;

    setMaxTime();

    if (status == TimerStatus.RUNNING) {
      _deviceService.showRunningNotification(
          timerId: timerModel.id,
          duration: maxTime,
          remainingTime: remainingTime);
    } else if (status == TimerStatus.PAUSING) {
      pauseTimer();
    }
  }

  void pauseTimer() {
    _status.value = TimerStatus.PAUSING;
    _deviceService.showPauseNotification(
        timerId: timerModel.id, remainingTime: remainingTime);
    _disposeTimer();
  }

  Future<void> cancelTimer() async {
    _status.value = TimerStatus.INITIAL;
    // ignore: unawaited_futures
    _deviceService.cancelNotification(timerId: timerModel.id);
    _resetTime();
    _disposeTimer();
  }

  void _resetTime() {
    setRemainingTime(timerModel.initialTimeInSeconds);
  }

  void _elapseTimer() {
    _status.value = TimerStatus.ELAPSED;
    _disposeTimer();
  }

  void _disposeTimer() {
    _timer?.cancel();
    _timer = null;
  }

  Future<void> handleStartActions() async {
    if (timerModel.volumeAction.enabled) {
      await _deviceService.setVolume(timerModel.volumeAction.value.truncate());
    }

    if (timerModel.playMusicAction.enabled) {
      print('Play music');
    }
    //_deviceService.setVolume(timerModel.volumeAction.value.truncate());

    if (timerModel.doNotDisturbAction.enabled &&
        _deviceService.notificationSettingsAccess) {
      await _deviceService.toggleDoNotDisturb(true);
    }
  }

  Future<void> handleEndedActions() async {
    log.d('handleEndedActions()');

    if (timerModel.mediaAction.enabled) await _deviceService.toggleMedia(false);
    if (timerModel.wifiAction.enabled && _deviceService.platformVersion < 29) {
      await _deviceService.toggleWifi(false);
    }
    if (timerModel.bluetoothAction.enabled) {
      await _deviceService.toggleBluetooth(false);
    }
    if (timerModel.screenAction.enabled && _deviceService.deviceAdmin) {
      await _deviceService.toggleScreen(false);
    }

    await _deviceService.showElapsedNotification(timerModel: timerModel);
  }

  void setMaxTime() =>
      _maxTime = max(timerModel.initialTimeInSeconds, remainingTime);
}

void onDeviceAdminCallback(final bool granted) async {
  final log = getLogger();
  log.d('onDeviceAdminGrantedCallback');

  WidgetsFlutterBinding.ensureInitialized();
}

void onNotificationAccessCallback(final bool granted) async {
  final log = getLogger();
  log.d('onNotificationAccessGrantedCallback');

  WidgetsFlutterBinding.ensureInitialized();
}
