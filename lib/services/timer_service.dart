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

enum TimerStatus { initial, pausing, running, elapsed }

@injectable
class TimerService with ReactiveServiceMixin {
  final Logger log = getLogger();
  final DeviceService _deviceService = locator<DeviceService>();
  final LightService _lightService = locator<LightService>();
  final SharedPreferences _prefsService = locator<SharedPreferences>();
  final TimerModel? timerModel;

  TimerStatus _status = TimerStatus.initial;
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
    if (status == TimerStatus.elapsed) {
      _resetTime();
    } else if (status == TimerStatus.initial) {
      _handleStartActions();
    }

    setTimerStatus(TimerStatus.running);

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

    if (status == TimerStatus.running) {
      _deviceService.showRunningNotification(
          timerId: timerModel!.id,
          duration: maxTime,
          remainingTime: remainingTime,
          shakeToExtend: _prefsService.getBool(kPrefKeyExtendByShake) ??
              kDefaultExtendByShake);
    } else if (status == TimerStatus.pausing) {
      pauseTimer();
    }
  }

  void pauseTimer() {
    setTimerStatus(TimerStatus.pausing);

    _deviceService.showPauseNotification(
        timerId: timerModel!.id, remainingTime: remainingTime);
  }

  Future<void> cancelTimer() async {
    setTimerStatus(TimerStatus.elapsed);

    // ignore: unawaited_futures
    _deviceService.cancelNotification(timerId: timerModel!.id);
    _resetTime();
  }

  void _resetTime() => setRemainingTime(timerModel!.initialTimeInSeconds);

  void _handleStartActions() {
    for (var element in timerModel!.startActions) {
      if (element.enabled) {
        switch (element.id) {
          case ActionType.volume:
            handleVolumeAction((element as ValueActionModel).value!.round());
            break;
          case ActionType.light:
            handleLightAction();
            break;
          case ActionType.dnd:
            if (_deviceService.notificationSettingsAccess) {
              handleDoNotDisturbAction();
            }
            break;
          default:
        }
      }
    }
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
    setTimerStatus(TimerStatus.elapsed);
    setRemainingTime(0);

    var numElapsed = _prefsService.getInt(kPrefKeyNumTimerElapsed) ?? 0;
    await _prefsService.setInt(kPrefKeyNumTimerElapsed, ++numElapsed);

    for (var element in timerModel!.endActions) {
      if (element.enabled) {
        switch (element.id) {
          case ActionType.media:
            await _deviceService.toggleMedia(false);
            break;
          case ActionType.wifi:
            await _deviceService.toggleWifi(false);
            break;
          case ActionType.bluetooth:
            await _deviceService.toggleBluetooth(false);
            break;
          case ActionType.screen:
            if (_deviceService.deviceAdmin) {
              await _deviceService.toggleScreen(false);
            }
            break;
          default:
        }
      }
    }

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
