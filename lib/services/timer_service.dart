import 'dart:async';
import 'dart:math';

import 'package:flutter/cupertino.dart';
import 'package:injectable/injectable.dart';
import 'package:logger/logger.dart';
import 'package:observable_ish/observable_ish.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:sleep_timer/app/locator.dart';
import 'package:sleep_timer/app/logger.util.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/model/timer_model.dart';
import 'package:sleep_timer/services/device_service.dart';
import 'package:sleep_timer/services/light_service.dart';
import 'package:stacked/stacked.dart';

enum TimerStatus { INITIAL, PAUSING, RUNNING, ELAPSED }

@injectable
class TimerService with ReactiveServiceMixin {
  final Logger log = getLogger();
  final _deviceService = locator<DeviceService>();
  final _lightService = locator<LightService>();
  final _prefsService = locator<SharedPreferences>();
  final TimerModel timerModel;

  final RxValue<TimerStatus> _status = RxValue(initial: TimerStatus.INITIAL);
  TimerStatus get status => _status.value;

  TimerService(@factoryParam this.timerModel)
      : _remainingTime = RxValue<int>(initial: timerModel.initialTimeInSeconds),
        _maxTime = timerModel.initialTimeInSeconds {
    listenToReactiveValues([_remainingTime, _status]);
  }

  final RxValue<int> _remainingTime;
  int get remainingTime => _remainingTime.value;

  int _maxTime;
  int get maxTime => _maxTime;

  void start() {
    if (status == TimerStatus.ELAPSED) {
      _resetTime();
    } else if (status == TimerStatus.INITIAL) {
      handleStartActions();
    }

    _status.value = TimerStatus.RUNNING;

    _deviceService.showRunningNotification(
        timerId: timerModel.id,
        duration: maxTime,
        remainingTime: remainingTime);
  }

  void setRemainingTime(final int seconds) => _remainingTime.value = seconds;

  void extendTime(final int seconds) {
    final defaultExtendTime =
        _prefsService.getInt(kPrefKeyDefaultExtendTimeByShake) ??
            kDefaultExtendTimeByShake;

    _remainingTime.value += seconds ?? defaultExtendTime * 60;
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
  }

  Future<void> cancelTimer() async {
    _status.value = TimerStatus.INITIAL;
    // ignore: unawaited_futures
    _deviceService.cancelNotification(timerId: timerModel.id);
    _resetTime();
  }

  void _resetTime() {
    _remainingTime.value = timerModel.initialTimeInSeconds;
  }

  Future<void> handleStartActions() async {
    if (timerModel.volumeAction.enabled) {
      await _deviceService.setVolume(timerModel.volumeAction.value.truncate());
    }

    if (timerModel.lightAction.enabled) {
      await _lightService.toggleLights(false);
    }

    if (timerModel.doNotDisturbAction.enabled &&
        _deviceService.notificationSettingsAccess) {
      await _deviceService.toggleDoNotDisturb(true);
    }
  }

  Future<void> handleEndedActions() async {
    log.d('handleEndedActions()');
    _status.value = TimerStatus.ELAPSED;
    setRemainingTime(0);

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
