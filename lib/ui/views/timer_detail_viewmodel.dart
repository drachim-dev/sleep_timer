import 'dart:async';
import 'dart:math';

import 'package:android_alarm_manager/android_alarm_manager.dart';
import 'package:device_functions/device_functions.dart';
import 'package:sleep_timer/app/locator.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/model/timer_model.dart';
import 'package:sleep_timer/services/timer_service.dart';
import 'package:stacked/stacked.dart';

triggerAlarm() async {
  print("triggerAlarm()");
  if (TimerDetailViewModel._timerModel.musicAction.value)
    DeviceFunctions.disableMedia();
  if (TimerDetailViewModel._timerModel.wifiAction.value)
    DeviceFunctions.disableWifi();
}

class TimerDetailViewModel extends ReactiveViewModel {
  final TimerService _timerService;
  static TimerModel _timerModel;
  Timer _timer;
  bool _isStarting = true;

  TimerDetailViewModel(timerModel)
      : _timerService = locator<TimerService>(param1: timerModel.initialTimeInSeconds) {
    _timerModel = timerModel;
    startTimer(delay: const Duration(milliseconds: 1500));
  }

  TimerModel get timerModel => _timerModel;
  int get initialTime => _timerModel.initialTimeInSeconds;
  int get remainingTime => _timerService.remainingTime;
  int get maxTime => max(_timerModel.initialTimeInSeconds, _timerService.remainingTime);
  bool get isStarting => _isStarting;
  bool get isActive => _timer?.isActive ?? false;

  Future<void> startTimer(
      {final Duration delay = const Duration(seconds: 0)}) async {
    const interval = Duration(seconds: 1);

    _isStarting = true;

    Future.delayed(delay, () {
      _timer = Timer.periodic(interval, (timer) {
        if (_timerService.remainingTime > 0) {
          _timerService.count();
        } else {
          timer.cancel();
        }
      });
    }).then((_) {
      _isStarting = false;
    });

    AndroidAlarmManager.oneShot(
        Duration(seconds: remainingTime), kAlarmId, triggerAlarm,
        allowWhileIdle: true, wakeup: true);
  }

  void stopTimer() {
    _timer?.cancel();
    _timer = null;
  }

  void onExtendTime(int minutes) {
    final int seconds = minutes * 60;
    _timerService.extendTime(seconds);
  }

  void onChangeMusic(bool value) {
    _timerModel.musicAction.value = value;
    notifyListeners();
  }

  void onChangeWifi(bool value) {
    _timerModel.wifiAction.value = value;
    notifyListeners();
  }

  void onChangeBluetooth(bool value) {
    _timerModel.bluetoothAction.value = value;
    notifyListeners();
  }

  void onChangeScreen(bool value) {
    _timerModel.screenAction.value = value;
    notifyListeners();
  }

  void onChangeVolume(bool value) {
    _timerModel.volumeAction.value = value;
    notifyListeners();
  }

  void onChangeLight(bool value) {
    _timerModel.lightAction.value = value;
    notifyListeners();
  }

  void onChangeApp(bool value) {
    _timerModel.appAction.value = value;
    notifyListeners();
  }

  @override
  List<ReactiveServiceMixin> get reactiveServices => [_timerService];
}
