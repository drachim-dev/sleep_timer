import 'dart:async';

import 'package:device_functions/messages_generated.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:sleep_timer/app/locator.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/common/timer_service_manager.dart';
import 'package:sleep_timer/model/action_model.dart';
import 'package:sleep_timer/model/timer_model.dart';
import 'package:sleep_timer/services/device_service.dart';
import 'package:sleep_timer/services/purchase_service.dart';
import 'package:sleep_timer/services/timer_service.dart';
import 'package:stacked/stacked.dart';
import 'package:stacked_services/stacked_services.dart';

class TimerDetailViewModel extends ReactiveViewModel implements Initialisable {
  final TimerService _timerService;
  final TimerModel _timerModel;
  final _navigationService = locator<NavigationService>();
  final _prefsService = locator<SharedPreferences>();
  final _purchaseService = locator<PurchaseService>();
  final _deviceService = locator<DeviceService>();

  bool _newInstance;

  bool get deviceAdmin => _deviceService.deviceAdmin ?? false;
  bool get notificationSettingsAccess =>
      _deviceService.notificationSettingsAccess ?? false;

  TimerDetailViewModel(this._timerModel)
      : _timerService =
            TimerServiceManager.getInstance().getTimerService(_timerModel.id) ??
                locator<TimerService>(param1: _timerModel) {
    _newInstance =
        TimerServiceManager.getInstance().getTimerService(timerModel.id) ==
            null;
    if (_newInstance) {
      TimerServiceManager.getInstance().setTimerService(_timerService);
    }

    // Check for adFree in-app purchase
    _isAdFree = _purchaseService.products.firstWhere(
            (element) =>
                element.productDetails.id == kProductRemoveAds &&
                element.purchased,
            orElse: () => null) !=
        null;
  }

  TimerModel get timerModel => _timerModel;
  int get initialTime => _timerModel.initialTimeInSeconds;
  int get remainingTime => _timerService.remainingTime;

  bool get isRunning => _timerService.isActive;

  bool _isAdFree = false;
  bool get isAdFree => _isAdFree;

  int get maxTime => _timerService.maxTime;

  Future<VolumeResponse> get volume => _deviceService.volume;

  @override
  Future<void> initialise() async {
    if (_newInstance) {
      await initActionPreferences();
      startTimer(delay: const Duration(milliseconds: 1500));
    }
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
    await runBusyFuture(Future.delayed(delay, () {
      _timerService.start();
    }));
  }

  void pauseTimer() => _timerService.pauseTimer();
  void cancelTimer() async {
    _navigationService.back();
    await runBusyFuture(_timerService.cancelTimer());
  }

  void navigateBack() {
    _navigationService.back(result: timerModel.id);
  }

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

  void onChangeDoNotDisturb(bool enabled) {
    _timerModel.doNotDisturbAction.enabled = enabled;
    _prefsService.setBool(ActionType.DND.toString(), enabled);
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
