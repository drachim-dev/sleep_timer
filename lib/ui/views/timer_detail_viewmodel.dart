import 'dart:async';

import 'package:shared_preferences/shared_preferences.dart';
import 'package:sleep_timer/app/locator.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/common/timer_service_manager.dart';
import 'package:sleep_timer/model/action_model.dart';
import 'package:sleep_timer/model/product.dart';
import 'package:sleep_timer/model/timer_model.dart';
import 'package:sleep_timer/services/device_service.dart';
import 'package:sleep_timer/services/purchase_service.dart';
import 'package:sleep_timer/services/timer_service.dart';
import 'package:stacked/stacked.dart';

class TimerDetailViewModel extends ReactiveViewModel implements Initialisable {
  final TimerService _timerService;
  final TimerModel _timerModel;
  final _prefsService = locator<SharedPreferences>();
  final _purchaseService = locator<PurchaseService>();
  final _deviceService = locator<DeviceService>();

  bool _isStarting = true;
  bool get deviceAdmin => _deviceService.deviceAdmin ?? false;

  TimerDetailViewModel(this._timerModel)
      : _timerService = locator<TimerService>(param1: _timerModel) {
    TimerServiceManager.getInstance().setTimerService(_timerService);
  }

  TimerModel get timerModel => _timerModel;
  int get initialTime => _timerModel.initialTimeInSeconds;
  int get remainingTime => _timerService.remainingTime;
  int get maxTime => _timerService.maxTime;

  bool get isStarting => _isStarting;
  bool get isActive => _timerService.timer?.isActive ?? false;

  bool _isAdFree = false;
  bool get isAdFree => _isAdFree;

  @override
  Future<void> initialise() async {
    setBusy(true);
    notifyListeners();

    // Check for adFree in-app purchase
    final List<Product> products =
        await runBusyFuture(_purchaseService.products);
    _isAdFree = products.firstWhere(
            (element) =>
                element.productDetails.id == kProductRemoveAds &&
                element.purchased,
            orElse: () => null) !=
        null;

    await initActionPreferences();
    startTimer(delay: const Duration(milliseconds: 1500));
  }

  Future<void> initActionPreferences() async {
    _prefsService.setBool(
        ActionType.MEDIA.toString(), _timerModel.mediaAction.enabled);
    _prefsService.setBool(
        ActionType.WIFI.toString(), _timerModel.wifiAction.enabled);
    _prefsService.setBool(
        ActionType.BLUETOOTH.toString(), _timerModel.bluetoothAction.enabled);
    _prefsService.setBool(
        ActionType.SCREEN.toString(), _timerModel.screenAction.enabled);
    _prefsService.setBool(
        ActionType.VOLUME.toString(), _timerModel.volumeAction.enabled);
    _prefsService.setDouble(
        _timerModel.volumeAction.key, _timerModel.volumeAction.value);
    _prefsService.setBool(
        ActionType.LIGHT.toString(), _timerModel.lightAction.enabled);
    _prefsService.setBool(
        ActionType.APP.toString(), _timerModel.appAction.enabled);
  }

  Future<void> startTimer(
      {final Duration delay = const Duration(seconds: 0)}) async {
    _isStarting = true;
    await Future.delayed(delay, () {
      _timerService.start();
    });
    _isStarting = false;
  }

  void pauseTimer() => _timerService.pauseTimer();

  void onExtendTime(int minutes) {
    final int seconds = minutes * 60;
    _timerService.extendTime(seconds);
  }

  void onChangeMedia(bool enabled) {
    _timerModel.mediaAction.enabled = enabled;
    _prefsService.setBool(ActionType.MEDIA.toString(), enabled);
    notifyListeners();
  }

  void onChangeWifi(bool enabled) {
    _timerModel.wifiAction.enabled = enabled;
    _prefsService.setBool(ActionType.WIFI.toString(), enabled);
    notifyListeners();
  }

  void onChangeBluetooth(bool enabled) {
    _timerModel.bluetoothAction.enabled = enabled;
    _prefsService.setBool(ActionType.BLUETOOTH.toString(), enabled);
    notifyListeners();
  }

  void onChangeScreen(bool enabled) {
    _timerModel.screenAction.enabled = enabled;
    _prefsService.setBool(ActionType.SCREEN.toString(), enabled);
    notifyListeners();
  }

  void onChangeVolume(bool enabled) {
    _timerModel.volumeAction.enabled = enabled;
    _prefsService.setBool(ActionType.VOLUME.toString(), enabled);
    notifyListeners();
  }

  void onChangeVolumeLevel(double value) {
    _timerModel.volumeAction.value = value;
    _prefsService.setDouble(_timerModel.volumeAction.key, value);
    notifyListeners();
  }

  void onChangeLight(bool enabled) {
    _timerModel.lightAction.enabled = enabled;
    notifyListeners();
  }

  void onChangeApp(bool enabled) {
    _timerModel.appAction.enabled = enabled;
    notifyListeners();
  }

  @override
  List<ReactiveServiceMixin> get reactiveServices => [_timerService];
}
