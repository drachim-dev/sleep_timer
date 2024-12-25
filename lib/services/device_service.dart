import 'package:device_functions/messages_generated.dart';
import 'package:device_functions/platform_interface.dart';
import 'package:injectable/injectable.dart';
import 'package:sleep_timer/common/color_ext.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/generated/l10n.dart';
import 'package:sleep_timer/model/timer_model.dart';
import 'package:sleep_timer/platform_interface.dart';
import 'package:stacked/stacked.dart';
import 'package:sleep_timer/messages_generated.dart';

@prod
@singleton
class DeviceService with ListenableServiceMixin {
  final DeviceFunctionsPlatform _deviceFunctions =
      DeviceFunctionsPlatform.instance;

  DeviceService() {
    listenToReactiveValues([_deviceAdmin, _notificationSettingsAccess]);
  }

  late DeviceInfoResponse _deviceInfo;
  int get platformVersion => _deviceInfo.platformVersion ?? 0;
  String get deviceManufacturer => _deviceInfo.deviceManufacturer ?? '';
  String get deviceModel => _deviceInfo.deviceModel ?? '';

  bool _hasAccelerometer = true;
  bool get hasAccelerometer => _hasAccelerometer;

  bool _deviceAdmin = false;
  bool get deviceAdmin => _deviceAdmin;

  Future<VolumeResponse> get volume => _deviceFunctions.getVolume();

  bool _notificationSettingsAccess = false;
  bool get notificationSettingsAccess => _notificationSettingsAccess;

  Future<List<Package>> get playerApps async =>
      SleepTimerPlatform.instance.getInstalledPlayerApps();

  Future<List<Package>> get alarmApps async =>
      SleepTimerPlatform.instance.getInstalledAlarmApps();

  @factoryMethod
  static Future<DeviceService> create() async {
    var instance = DeviceService();
    await instance._init();
    return instance;
  }

  Future<void> _init() async {
    _deviceInfo = await _deviceFunctions.getDeviceInfo();
    _hasAccelerometer = await _deviceFunctions.hasAccelerometer();
    _deviceAdmin = await _deviceFunctions.isDeviceAdminActive();
    _notificationSettingsAccess =
        await _deviceFunctions.isNotificationAccessGranted();
  }

  Future<bool> toggleMedia(final bool enable, final int? endLevel) {
    return _deviceFunctions.toggleMedia(enable, endLevel);
  }

  Future<bool> toggleWifi(final bool enable) {
    return _deviceFunctions.toggleWifi(enable);
  }

  Future<bool> toggleBluetooth(final bool enable) {
    return _deviceFunctions.toggleBluetooth(enable);
  }

  Future<bool> toggleScreen(final bool enable) {
    return _deviceFunctions.toggleScreen(enable);
  }

  Future<VolumeResponse> setVolume(final int level) {
    return _deviceFunctions.setVolume(level);
  }

  Future<bool> toggleDoNotDisturb(final bool enable) {
    return _deviceFunctions.toggleDoNotDisturb(enable);
  }

  Future<void> toggleDeviceAdmin(final bool enable) async {
    await _deviceFunctions.toggleDeviceAdmin(enable);
  }

  Future<void> toggleNotificationSettingsAccess(final bool enable) async {
    await _deviceFunctions.toggleNotificationAccess(enable);
  }

  Future<bool> showRunningNotification(
      {required final String timerId,
      required final int duration,
      required final int remainingTime,
      required final bool shakeToExtend}) async {
    return SleepTimerPlatform.instance.showRunningNotification(
        timerId: timerId,
        title: S.current.notificationTimeLeft,
        description: S.current.notificationStatusActive,
        accentColor: kNotificationActionColor.toARGB32,
        pauseAction: S.current.notificationActionPause,
        extendActions: [5, 20],
        duration: duration,
        remainingTime: remainingTime,
        shakeToExtend: shakeToExtend);
  }

  Future<bool> showPauseNotification(
      {required final String timerId, required final int remainingTime}) async {
    return SleepTimerPlatform.instance.showPausingNotification(
        timerId: timerId,
        title: S.current.notificationTimeLeft,
        description: S.current.notificationStatusPausing,
        accentColor: kNotificationActionColor.toARGB32,
        cancelAction: S.current.notificationActionCancel,
        continueAction: S.current.notificationActionContinue,
        remainingTime: remainingTime);
  }

  Future<bool> showElapsedNotification(
      {required final TimerModel timerModel}) async {
    var description = '';
    final activeActions =
        timerModel.endActions.where((element) => element.enabled);
    if (activeActions.isEmpty) {
      description += S.current.notificationNoActionsExecuted;
    } else {
      for (var i = 0; i < activeActions.length; i++) {
        final element = activeActions.elementAt(i);
        description += '${element.description}. ';
      }
    }

    return SleepTimerPlatform.instance.showElapsedNotification(
        timerId: timerModel.id,
        title: S.current.notificationStatusElapsed,
        description: description,
        accentColor: kNotificationActionColor.toARGB32,
        restartAction: S.current.notificationActionRestart);
  }

  Future<bool> cancelNotification({required final String timerId}) async {
    return SleepTimerPlatform.instance.cancelTimer(timerId);
  }

  void toggleExtendByShake(final bool enable) {
    SleepTimerPlatform.instance.toggleExtendByShake(enable);
  }

  // Async response not yet possible with pigeon.
  // Workaround by using @FlutterApi() for callback.
  void setDeviceAdmin(final bool granted) {
    _deviceAdmin = granted;
    notifyListeners();
  }

  // Async response not yet possible with pigeon.
  // Workaround by using @FlutterApi() for callback.
  void setNotificationAccess(final bool granted) {
    _notificationSettingsAccess = granted;
    notifyListeners();
  }

  Future<void> openPackage(final String packageName) async {
    return SleepTimerPlatform.instance.launchApp(packageName);
  }
}
