import 'dart:async';

import 'package:shared_preferences/shared_preferences.dart';
import 'package:sleep_timer/app/locator.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/common/timer_service_manager.dart';
import 'package:sleep_timer/model/action_model.dart';
import 'package:sleep_timer/model/product.dart';
import 'package:sleep_timer/model/timer_model.dart';
import 'package:sleep_timer/services/purchase_service.dart';
import 'package:sleep_timer/services/timer_service.dart';
import 'package:stacked/stacked.dart';

class TimerDetailViewModel extends ReactiveViewModel implements Initialisable {
  final TimerService _timerService;
  final TimerModel _timerModel;
  final _prefsService = locator<SharedPreferences>();
  final _purchaseService = locator<PurchaseService>();

  bool _isStarting = true;

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
        ActionType.MEDIA.toString(), _timerModel.mediaAction.value);
    _prefsService.setBool(
        ActionType.WIFI.toString(), _timerModel.wifiAction.value);
    _prefsService.setBool(
        ActionType.BLUETOOTH.toString(), _timerModel.bluetoothAction.value);
    _prefsService.setBool(
        ActionType.SCREEN.toString(), _timerModel.screenAction.value);
    _prefsService.setBool(
        ActionType.VOLUME.toString(), _timerModel.volumeAction.value);
    _prefsService.setBool(
        ActionType.LIGHT.toString(), _timerModel.lightAction.value);
    _prefsService.setBool(
        ActionType.APP.toString(), _timerModel.appAction.value);
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