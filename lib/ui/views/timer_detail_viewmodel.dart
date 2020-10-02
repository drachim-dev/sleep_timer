import 'dart:async';
import 'dart:math';

import 'package:android_alarm_manager/android_alarm_manager.dart';
import 'package:device_functions/device_functions.dart';
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:sleep_timer/app/locator.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/main.dart';
import 'package:sleep_timer/model/action_model.dart';
import 'package:sleep_timer/model/timer_model.dart';
import 'package:sleep_timer/services/device_service.dart';
import 'package:sleep_timer/services/timer_service.dart';
import 'package:stacked/stacked.dart';

class TimerDetailViewModel extends ReactiveViewModel {
  final TimerService _timerService;
  final TimerModel _timerModel;
  final _deviceService = locator<DeviceService>();
  final _prefsService = locator<SharedPreferences>();

  Timer _timer;
  bool _isStarting = true;

  TimerDetailViewModel(this._timerModel)
      : _timerService = locator<TimerService>(param1: _timerModel) {
    startTimer(delay: const Duration(milliseconds: 1500));
  }

  TimerModel get timerModel => _timerModel;
  int get initialTime => _timerModel.initialTimeInSeconds;
  int get remainingTime => _timerService.remainingTime;
  int get maxTime =>
      max(_timerModel.initialTimeInSeconds, _timerService.remainingTime);
  bool get isStarting => _isStarting;
  bool get isActive => _timer?.isActive ?? false;

  Future<void> startTimer(
      {final Duration delay = const Duration(seconds: 0)}) async {
    const interval = Duration(seconds: 1);

    _isStarting = true;
    await Future.delayed(delay, () {
      _timer = Timer.periodic(interval, (timer) {
        _timerService.remainingTime > 0 ? _timerService.count() : cancelTimer();
      });
    });
    _isStarting = false;
    _deviceService.showRunningNotification(
        initialTime: initialTime, remainingTime: remainingTime);

    AndroidAlarmManager.oneShot(
        Duration(seconds: remainingTime), kAlarmId, alarmCallback,
        allowWhileIdle: true, wakeup: true);
  }

  void pauseTimer() {
    _deviceService.showPauseNotification(remainingTime);
    _disposeTimer();
  }

  void cancelTimer() {
    _deviceService.cancelNotification();
    _disposeTimer();
  }

  void _disposeTimer() {
    _deviceService.showElapsedNotification();
    AndroidAlarmManager.cancel(kAlarmId);
    _timer?.cancel();
    _timer = null;
  }

  void onExtendTime(int minutes) {
    final int seconds = minutes * 60;
    _timerService.extendTime(seconds);
  }

  void onChangeMedia(bool value) {
    _timerModel.mediaAction.value = value;
    _prefsService.setBool(ActionType.MEDIA.toString(), value);
    notifyListeners();
  }

  void onChangeWifi(bool value) {
    _timerModel.wifiAction.value = value;
    _prefsService.setBool(ActionType.WIFI.toString(), value);
    notifyListeners();
  }

  void onChangeBluetooth(bool value) {
    _timerModel.bluetoothAction.value = value;
    _prefsService.setBool(ActionType.BLUETOOTH.toString(), value);
    notifyListeners();
  }

  void onChangeScreen(bool value) {
    _timerModel.screenAction.value = value;
    _prefsService.setBool(ActionType.SCREEN.toString(), value);
    notifyListeners();
  }

  void onChangeVolume(bool value) {
    _timerModel.volumeAction.value = value;
    _prefsService.setBool(ActionType.VOLUME.toString(), value);
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

Future<void> alarmCallback(int id) async {
  WidgetsFlutterBinding.ensureInitialized();
  await Application.init();

  final _prefsService = await SharedPreferences.getInstance();

  print("alarm callback for alarm $id");

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

  if (mediaAction) DeviceFunctions.disableMedia();
  if (wifiAction) DeviceFunctions.disableWifi();
  if (bluetoothAction) DeviceFunctions.disableBluetooth();
  if (screenAction) DeviceFunctions.disableScreen();
  if (volumeAction) DeviceFunctions.setVolume(1);

  // _deviceService.showElapsedNotification();

  DeviceFunctions.showNotification(kNotificationId);
}
