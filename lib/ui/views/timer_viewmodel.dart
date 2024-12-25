import 'dart:async';
import 'dart:math';

import 'package:device_functions/messages_generated.dart';
import 'package:get_it/get_it.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:sleep_timer/app/app.router.dart';
import 'package:sleep_timer/app/locator.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/common/timer_service_manager.dart';
import 'package:sleep_timer/messages_generated.dart';
import 'package:sleep_timer/model/action_model.dart';
import 'package:sleep_timer/model/bridge_model.dart';
import 'package:sleep_timer/model/timer_model.dart';
import 'package:sleep_timer/services/device_service.dart';
import 'package:sleep_timer/services/theme_service.dart';
import 'package:sleep_timer/services/timer_service.dart';
import 'package:stacked/stacked.dart';
import 'package:stacked_services/stacked_services.dart';

class TimerViewModel extends ReactiveViewModel implements Initialisable {
  final TimerService _timerService;
  final TimerModel _timerModel;
  final NavigationService _navigationService = locator<NavigationService>();

  final SharedPreferences _prefsService = locator<SharedPreferences>();
  final ThemeService _themeService = locator<ThemeService>();
  final DeviceService _deviceService = locator<DeviceService>();

  bool _newInstance = false;

  bool get isDeviceAdmin => _deviceService.deviceAdmin;
  bool get hasNotificationSettingsAccess =>
      _deviceService.notificationSettingsAccess;

  /// check if there is at least one bridge with a light action enabled
  bool get hasEnabledLights {
    final savedBridgesJson = _prefsService.getString(kPrefKeyHueBridges);
    if (savedBridgesJson != null) {
      final savedBridges = BridgeModel.decode(savedBridgesJson);
      return savedBridges
          .any((bridge) => bridge.groups.any((group) => group.actionEnabled!));
    } else {
      return false;
    }
  }

  bool get showStartActionHints => showTapHint || showLongPressHint;

  bool get showTapHint =>
      _prefsService.getBool(kPrefKeyShowTapHintForStartActions) ?? true;

  bool get showLongPressHint =>
      _prefsService.getBool(kPrefKeyShowLongPressHintForStartActions) ?? true;

  bool get showEndActionHints => showBluetoothNotSupportedHint;

  bool get showBluetoothNotSupportedHint =>
      _prefsService.getBool(kPrefKeyShowBluetoothNotSupportedHint) ??
      !bluetoothSupported;

  bool get wifiSupported => _platformVersion < 29;
  bool get bluetoothSupported => _platformVersion < 33;

  TimerViewModel(this._timerModel)
      : _timerService =
            TimerServiceManager.instance.getTimerService(_timerModel.id) ??
                locator<TimerService>(param1: _timerModel) {
    _newInstance =
        TimerServiceManager.instance.getTimerService(timerModel.id) == null;
    if (_newInstance) {
      TimerServiceManager.instance.setTimerService(_timerService);
    }
  }

  TimerModel get timerModel => _timerModel;
  int get initialTime => _timerModel.initialTimeInSeconds;
  int get remainingTime => max(_timerService.remainingTime, 0);
  int get maxTime => _timerService.maxTime;

  TimerStatus get timerStatus => _timerService.status;

  Future<VolumeResponse> get volume => _deviceService.volume;
  int get _platformVersion => _deviceService.platformVersion;

  bool get showGlow => _themeService.showGlow;

  Future<List<Package>> get playerApps => _deviceService.playerApps;
  Future<List<Package>> get alarmApps => _deviceService.alarmApps;

  @override
  Future<void> initialise() async {
    await GetIt.I.isReady<DeviceService>();
    if (_newInstance) {
      await initActionPreferences(_timerModel.startActions);
      await initActionPreferences(_timerModel.endActions);
      await startTimer();
    }
  }

  Future<void> initActionPreferences(List<ActionModel> actions) async {
    await Future.forEach(actions, (dynamic element) async {
      await _prefsService.setBool(element.id.toString(), element.enabled);

      if (element is ValueActionModel) {
        if (element.value != null) {
          await _prefsService.setDouble(element.key!, element.value!);
        }
      }
    });
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

  Future<void> navigateToSettings(
      {final bool? deviceAdminFocused,
      final bool? notificationSettingsAccessFocused}) async {
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

  void dismissTapHint() async {
    await _prefsService.setBool(kPrefKeyShowTapHintForStartActions, false);
    notifyListeners();
  }

  void dismissLongPressHint() async {
    await _prefsService.setBool(
        kPrefKeyShowLongPressHintForStartActions, false);
    notifyListeners();
  }

  void dismissBluetoothHint() async {
    await _prefsService.setBool(kPrefKeyShowBluetoothNotSupportedHint, false);
    notifyListeners();
  }

  void onExtendTime(int minutes) {
    final seconds = minutes * 60;

    _timerService.extendTime(seconds);
  }

  void onChangeVolumeStartAction(bool enabled) {
    if (_timerModel.volumeStartAction.value != null) {
      _timerModel.volumeStartAction.enabled = enabled;
      _prefsService.setBool(ActionType.volume.toString(), enabled);
      notifyListeners();
    }
  }

  Future<void> onChangeVolumeLevel(
      ValueActionModel actionModel, double value) async {
    actionModel.value = value;
    await _prefsService.setDouble(actionModel.key!, value);
    notifyListeners();
  }

  void handleSetVolumeAction(int value) {
    _timerService.handleSetVolumeAction(value);
  }

  Future<void> onChangeLight(bool enabled) async {
    if (enabled && !hasEnabledLights) {
      await navigateToLightsGroups();
    }

    if ((enabled && hasEnabledLights) || !enabled) {
      _timerModel.lightStartAction.enabled = enabled;
      await _prefsService.setBool(ActionType.light.toString(), enabled);
      notifyListeners();
    }
  }

  void onChangePlayMusic(bool enabled) {
    _timerModel.playMusicStartAction.enabled = enabled;
    _prefsService.setBool(ActionType.playMusic.toString(), enabled);
    notifyListeners();
  }

  void onChangeDoNotDisturb(bool enabled) async {
    if (enabled && !hasNotificationSettingsAccess) {
      await navigateToSettings(notificationSettingsAccessFocused: true);
    }

    if ((enabled && hasNotificationSettingsAccess) || !enabled) {
      _timerModel.doNotDisturbStartAction.enabled = enabled;
      await _prefsService.setBool(ActionType.dnd.toString(), enabled);
      notifyListeners();
    }
  }

  void onChangeMedia(bool enabled) {
    _timerModel.mediaAction.enabled = enabled;
    _prefsService.setBool(ActionType.media.toString(), enabled);
    notifyListeners();
  }

  void onChangeVolumeEndAction(bool enabled) {
    if (_timerModel.volumeAction.value != null || !enabled) {
      _timerModel.volumeAction.enabled = enabled;
      _prefsService.setBool('${ActionType.volume.toString()}_end', enabled);
      notifyListeners();
    }
  }

  void onChangeWifi(bool enabled) {
    _timerModel.wifiAction.enabled = enabled;
    _prefsService.setBool(ActionType.wifi.toString(), enabled);
    notifyListeners();
  }

  void onChangeBluetooth(bool enabled, {bool silent = false}) {
    _timerModel.bluetoothAction.enabled = enabled;
    _prefsService.setBool(ActionType.bluetooth.toString(), enabled);

    if (!silent) {
      notifyListeners();
    }
  }

  void onChangeScreen(bool enabled) async {
    if (enabled && !isDeviceAdmin) {
      await navigateToSettings(deviceAdminFocused: true);
    }

    if ((enabled && isDeviceAdmin) || !enabled) {
      _timerModel.screenAction.enabled = enabled;
      await _prefsService.setBool(ActionType.screen.toString(), enabled);
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
  List<ListenableServiceMixin> get listenableServices => [_timerService];
}
