import 'dart:async';
import 'dart:math';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:injectable/injectable.dart';
import 'package:logger/logger.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:sleep_timer/app/locator.dart';
import 'package:sleep_timer/app/logger.util.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/model/action_model.dart';
import 'package:sleep_timer/model/timer_model.dart';
import 'package:sleep_timer/services/device_service.dart';
import 'package:sleep_timer/services/light_service.dart';
import 'package:stacked/stacked.dart';

enum TimerStatus { INITIAL, PAUSING, RUNNING, ELAPSED }

@injectable
class TimerService with ReactiveServiceMixin {
  final Logger log = getLogger();
  final DeviceService _deviceService = locator<DeviceService>();
  final LightService _lightService = locator<LightService>();
  final SharedPreferences _prefsService = locator<SharedPreferences>();
  final TimerModel? timerModel;

  TimerStatus _status = TimerStatus.INITIAL;
  TimerStatus get status => _status;

  TimerService(@factoryParam this.timerModel)
      : _remainingTime = timerModel!.initialTimeInSeconds,
        _maxTime = timerModel.initialTimeInSeconds {
    listenToReactiveValues([_remainingTime, _status]);
  }

  int _remainingTime;
  int get remainingTime => _remainingTime;

  int _maxTime;
  int get maxTime => _maxTime;

  void start() {
    if (status == TimerStatus.ELAPSED) {
      _resetTime();
    } else if (status == TimerStatus.INITIAL) {
      _handleStartActions();
    }

    setTimerStatus(TimerStatus.RUNNING);

    _deviceService.showRunningNotification(
        timerId: timerModel!.id,
        duration: maxTime,
        remainingTime: remainingTime,
        shakeToExtend: _prefsService.getBool(kPrefKeyExtendByShake) ??
            kDefaultExtendByShake);
  }

  void setTimerStatus(TimerStatus status) {
    _status = status;
    notifyListeners();
  }

  void setRemainingTime(final int? seconds) {
    _remainingTime = seconds ?? (_remainingTime - 1);
    notifyListeners();
  }

  void extendTime(final int? seconds) {
    final defaultExtendTime =
        _prefsService.getInt(kPrefKeyDefaultExtendTimeByShake) ??
            kDefaultExtendTimeByShake;

    final newTime = _remainingTime += seconds ?? defaultExtendTime * 60;
    setRemainingTime(newTime);
    setMaxTime();

    if (status == TimerStatus.RUNNING) {
      _deviceService.showRunningNotification(
          timerId: timerModel!.id,
          duration: maxTime,
          remainingTime: remainingTime,
          shakeToExtend: _prefsService.getBool(kPrefKeyExtendByShake) ??
              kDefaultExtendByShake);
    } else if (status == TimerStatus.PAUSING) {
      pauseTimer();
    }
  }

  void pauseTimer() {
    setTimerStatus(TimerStatus.PAUSING);

    _deviceService.showPauseNotification(
        timerId: timerModel!.id, remainingTime: remainingTime);
  }

  Future<void> cancelTimer() async {
    setTimerStatus(TimerStatus.ELAPSED);

    // ignore: unawaited_futures
    _deviceService.cancelNotification(timerId: timerModel!.id);
    _resetTime();
  }

  void _resetTime() => setRemainingTime(timerModel!.initialTimeInSeconds);

  void _handleStartActions() {
    timerModel!.startActions.forEach((element) {
      if (element.enabled) {
        switch (element.id) {
          case ActionType.VOLUME:
            handleVolumeAction((element as ValueActionModel).value!.round());
            break;
          case ActionType.LIGHT:
            handleLightAction();
            break;
          case ActionType.DND:
            if (_deviceService.notificationSettingsAccess) {
              handleDoNotDisturbAction();
            }
            break;
          default:
        }
      }
    });
  }

  void handleVolumeAction(int value) {
    _deviceService.setVolume(value);
  }

  void handleLightAction() {
    _lightService.toggleLights(false);
  }

  void handleDoNotDisturbAction() {
    _deviceService.toggleDoNotDisturb(true);
  }

  Future<void> handleEndedActions() async {
    log.d('handleEndedActions()');
    setTimerStatus(TimerStatus.ELAPSED);
    setRemainingTime(0);

    var _numElapsed = _prefsService.getInt(kPrefKeyNumTimerElapsed) ?? 0;
    await _prefsService.setInt(kPrefKeyNumTimerElapsed, ++_numElapsed);

    timerModel!.endActions.forEach((element) {
      if (element.enabled) {
        switch (element.id) {
          case ActionType.MEDIA:
            _deviceService.toggleMedia(false);
            break;
          case ActionType.WIFI:
            _deviceService.toggleWifi(false);
            break;
          case ActionType.BLUETOOTH:
            _deviceService.toggleBluetooth(false);
            break;
          case ActionType.SCREEN:
            if (_deviceService.deviceAdmin) {
              _deviceService.toggleScreen(false);
            }
            break;
          default:
        }
      }
    });

    await _deviceService.showElapsedNotification(timerModel: timerModel!);
  }

  void setMaxTime() =>
      _maxTime = max(timerModel!.initialTimeInSeconds, remainingTime);
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
