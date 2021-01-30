import 'dart:async';

import 'package:device_functions/messages_generated.dart';
import 'package:get_it/get_it.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:sleep_timer/app/auto_router.gr.dart';
import 'package:sleep_timer/app/locator.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/common/timer_service_manager.dart';
import 'package:sleep_timer/model/action_model.dart';
import 'package:sleep_timer/model/app.dart';
import 'package:sleep_timer/model/bridge_model.dart';
import 'package:sleep_timer/model/timer_model.dart';
import 'package:sleep_timer/services/device_service.dart';
import 'package:sleep_timer/services/purchase_service.dart';
import 'package:sleep_timer/services/theme_service.dart';
import 'package:sleep_timer/services/timer_service.dart';
import 'package:stacked/stacked.dart';
import 'package:stacked_services/stacked_services.dart';

class TimerViewModel extends ReactiveViewModel implements Initialisable {
  final TimerService _timerService;
  final TimerModel _timerModel;
  final _navigationService = locator<NavigationService>();

  final _prefsService = locator<SharedPreferences>();
  final _themeService = locator<ThemeService>();
  final _purchaseService = locator<PurchaseService>();
  final _deviceService = locator<DeviceService>();

  bool _newInstance;

  bool get isDeviceAdmin => _deviceService.deviceAdmin ?? false;
  bool get hasNotificationSettingsAccess =>
      _deviceService.notificationSettingsAccess ?? false;

  /// check if there is at least one bridge with a light action enabled
  bool get hasEnabledLights {
    final savedBridgesJson = _prefsService.getString(kPrefKeyHueBridges);
    if (savedBridgesJson != null) {
      final savedBridges = BridgeModel.decode(savedBridgesJson);
      return savedBridges
          .any((bridge) => bridge.groups.any((group) => group.actionEnabled));
    } else {
      return false;
    }
  }

  bool get showHints => showTapHint || showLongPressHint;

  bool get showTapHint =>
      _prefsService.getBool(kPrefKeyShowTapHintForStartActions) ?? true;

  bool get showLongPressHint =>
      _prefsService.getBool(kPrefKeyShowLongPressHintForStartActions) ?? true;

  TimerViewModel(this._timerModel)
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
    _isAdFree = _purchaseService.products.any((element) =>
        element.productDetails.id == kProductRemoveAds && element.purchased);
  }

  TimerModel get timerModel => _timerModel;
  int get initialTime => _timerModel.initialTimeInSeconds;
  int get remainingTime => _timerService.remainingTime;
  int get maxTime => _timerService.maxTime;

  TimerStatus get timerStatus => _timerService.status;

  bool _isAdFree = false;
  bool get isAdFree => _isAdFree;

  Future<VolumeResponse> get volume => _deviceService.volume;
  int get platformVersion => _deviceService.platformVersion;

  bool get showGlow => _themeService.showGlow;

  Future<List<App>> get playerApps => _deviceService.playerApps;
  Future<List<App>> get alarmApps => _deviceService.alarmApps;

  @override
  Future<void> initialise() async {
    await GetIt.I.isReady<DeviceService>();
    if (_newInstance) {
      await initActionPreferences();
      await startTimer();
    }
  }

  Future<void> initActionPreferences() async {
    await _prefsService.setBool(
        ActionType.VOLUME.toString(), _timerModel.volumeAction.enabled);
    await _prefsService.setDouble(
        _timerModel.volumeAction.key, _timerModel.volumeAction.value);
    await _prefsService.setBool(
        ActionType.LIGHT.toString(), _timerModel.lightAction.enabled);
    await _prefsService.setBool(
        ActionType.PLAY_MUSIC.toString(), _timerModel.playMusicAction.enabled);
    await _prefsService.setBool(
        ActionType.DND.toString(), _timerModel.doNotDisturbAction.enabled);

    await _prefsService.setBool(
        ActionType.MEDIA.toString(), _timerModel.mediaAction.enabled);
    await _prefsService.setBool(
        ActionType.WIFI.toString(), _timerModel.wifiAction.enabled);
    await _prefsService.setBool(
        ActionType.BLUETOOTH.toString(), _timerModel.bluetoothAction.enabled);
    await _prefsService.setBool(
        ActionType.SCREEN.toString(), _timerModel.screenAction.enabled);

    await _prefsService.setBool(
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

  Future<void> navigateBack() async {
    _navigationService.back(result: timerModel.id);
  }

  Future<void> navigateToSettings(
      {final bool deviceAdminFocused,
      final bool notificationSettingsAccessFocused}) async {
    await _navigationService.navigateTo(Routes.settingsView,
        arguments: SettingsViewArguments(
            deviceAdminFocused: deviceAdminFocused,
            notificationSettingsAccessFocused:
                notificationSettingsAccessFocused));
  }

  Future<void> navigateToLightsGroups() async {
    await _navigationService.navigateTo(Routes.lightGroupView);
    notifyListeners();
  }

  void dismissTapHint() {
    _prefsService.setBool(kPrefKeyShowTapHintForStartActions, false);
    notifyListeners();
  }

  void dismissLongPressHint() {
    _prefsService.setBool(kPrefKeyShowLongPressHintForStartActions, false);
    notifyListeners();
  }

  void onExtendTime(int minutes) {
    final seconds = minutes * 60;

    _timerService.extendTime(seconds);
  }

  void onChangeVolume(bool enabled) {
    if (_timerModel.volumeAction.value != null) {
      _timerModel.volumeAction.enabled = enabled;
      _prefsService.setBool(ActionType.VOLUME.toString(), enabled);
      notifyListeners();
    }
  }

  Future<void> onChangeVolumeLevel(double value) async {
    _timerModel.volumeAction.value = value;
    await _prefsService.setDouble(_timerModel.volumeAction.key, value);
    notifyListeners();
  }

  void handleVolumeAction(int value) {
    _timerService.handleVolumeAction(value);
  }

  Future<void> onChangeLight(bool enabled) async {
    if (enabled && !hasEnabledLights) {
      await navigateToLightsGroups();
    }

    if ((enabled && hasEnabledLights) || !enabled) {
      _timerModel.lightAction.enabled = enabled;
      await _prefsService.setBool(ActionType.LIGHT.toString(), enabled);
      notifyListeners();
    }
  }

  void onChangePlayMusic(bool enabled) {
    _timerModel.playMusicAction.enabled = enabled;
    _prefsService.setBool(ActionType.PLAY_MUSIC.toString(), enabled);
    notifyListeners();
  }

  void onChangeDoNotDisturb(bool enabled) async {
    if (enabled && !hasNotificationSettingsAccess) {
      await navigateToSettings(notificationSettingsAccessFocused: true);
    }

    if ((enabled && hasNotificationSettingsAccess) || !enabled) {
      _timerModel.doNotDisturbAction.enabled = enabled;
      await _prefsService.setBool(ActionType.DND.toString(), enabled);
      notifyListeners();
    }
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

  void onChangeScreen(bool enabled) async {
    if (enabled && !isDeviceAdmin) {
      await navigateToSettings(deviceAdminFocused: true);
    }

    if ((enabled && isDeviceAdmin) || !enabled) {
      _timerModel.screenAction.enabled = enabled;
      await _prefsService.setBool(ActionType.SCREEN.toString(), enabled);
      notifyListeners();
    }
  }

  void onChangeApp(bool enabled) {
    _timerModel.appAction.enabled = enabled;
    notifyListeners();
  }

  void openPackage(String packageName) {
    _deviceService.openPackage(packageName);
    _navigationService.back();
  }

  @override
  List<ReactiveServiceMixin> get reactiveServices => [_timerService];
}
