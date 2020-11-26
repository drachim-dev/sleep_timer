import 'package:flutter/cupertino.dart';
import 'package:logger/logger.dart';
import 'package:sleep_timer/app/auto_router.gr.dart';
import 'package:sleep_timer/app/locator.dart';
import 'package:sleep_timer/app/logger.util.dart';
import 'package:sleep_timer/messages_generated.dart';
import 'package:sleep_timer/model/app.dart';
import 'package:sleep_timer/model/action_model.dart';
import 'package:sleep_timer/model/timer_model.dart';
import 'package:sleep_timer/platform_interface.dart';
import 'package:sleep_timer/services/timer_service.dart';
import 'package:stacked_services/stacked_services.dart';

import 'common/timer_service_manager.dart';

class SleepTimerPlatformImpl implements SleepTimerPlatform {
  final HostTimerApi _hostApi = HostTimerApi();

  @override
  Future<void> init(final int callbackHandle) {
    return _hostApi
        .init(InitializationRequest()..callbackHandle = callbackHandle);
  }

  @override
  Future<bool> showRunningNotification(
      {@required final String timerId,
      @required final String title,
      @required final String description,
      final String restartAction,
      final String pauseAction,
      final String cancelAction,
      final List<int> extendActions,
      @required final int duration,
      @required final int remainingTime}) async {
    final response =
        await _hostApi.showRunningNotification(TimeNotificationRequest()
          ..timerId = timerId
          ..title = title
          ..description = description
          ..restartAction = restartAction
          ..pauseAction = pauseAction
          ..cancelAction = cancelAction
          ..extendActions = extendActions
          ..duration = duration
          ..remainingTime = remainingTime);
    return response.success;
  }

  @override
  Future<bool> showPausingNotification(
      {@required final String timerId,
      @required final String title,
      @required final String description,
      final String restartAction,
      final String continueAction,
      final String cancelAction,
      final List<int> extendActions,
      @required final int remainingTime}) async {
    final response =
        await _hostApi.showPausingNotification(TimeNotificationRequest()
          ..timerId = timerId
          ..title = title
          ..description = description
          ..restartAction = restartAction
          ..continueAction = continueAction
          ..cancelAction = cancelAction
          ..extendActions = extendActions
          ..remainingTime = remainingTime);
    return response.success;
  }

  @override
  Future<bool> showElapsedNotification({
    @required final String timerId,
    @required final String title,
    @required final String description,
    final String restartAction,
  }) async {
    final response =
        await _hostApi.showElapsedNotification(NotificationRequest()
          ..timerId = timerId
          ..title = title
          ..description = description
          ..restartAction = restartAction);
    return response.success;
  }

  @override
  Future<bool> cancelTimer(final String timerId) async {
    final response =
        await _hostApi.cancelTimer(CancelRequest()..timerId = timerId);
    return response.success;
  }

  @override
  Future<List<App>> getInstalledPlayerApps() async {
    final response = await _hostApi.getInstalledPlayerApps();

    final apps = response.apps.map((e) => App.fromMap(e)).toList()
      ..sort((a, b) => a.title.compareTo(b.title));
    return apps;
  }

  @override
  Future<List<App>> getInstalledAlarmApps() async {
    final response = await _hostApi.getInstalledAlarmApps();

    final apps = response.apps.map((e) => App.fromMap(e)).toList()
      ..sort((a, b) => a.title.compareTo(b.title));
    return apps;
  }

  @override
  Future<void> launchApp(final String packageName) async {
    await _hostApi.launchApp(LaunchAppRequest()..packageName = packageName);
  }
}

class FlutterApiHandler extends FlutterTimerApi {
  final Logger log = getLogger();
  final Function callback;
  final Function alarmCallback;

  FlutterApiHandler({this.callback, this.alarmCallback});

  @override
  void onExtendTime(ExtendTimeResponse arg) {
    final timerId = arg.timerId;
    log.i('extend time by: ${arg.additionalTime} for timer with id $timerId');

    final _timerService =
        TimerServiceManager.getInstance().getTimerService(timerId);
    _timerService.extendTime(arg.additionalTime);
  }

  @override
  void onOpen(OpenRequest arg) {
    final timerId = arg.timerId;
    log.i('onOpen called for timer with id $timerId');

    final _timerService =
        TimerServiceManager.getInstance().getTimerService(timerId);
    final _navigationService = locator<NavigationService>();

    // Navigate to timer detail view
    _navigationService.popUntil((route) => route.isFirst);
    _navigationService.navigateTo(Routes.timerView,
        arguments: TimerViewArguments(timerModel: _timerService.timerModel));
  }

  @override
  void onPauseRequest(TimerRequest arg) {
    final timerId = arg.timerId;
    log.i('onPauseRequest requested for timer with id $timerId');

    final _timerService =
        TimerServiceManager.getInstance().getTimerService(timerId);
    _timerService.pauseTimer();
  }

  @override
  void onCancelRequest(TimerRequest arg) {
    final timerId = arg.timerId;
    log.i('onCancelRequest called for timer with id $timerId');

    final _timerService =
        TimerServiceManager.getInstance().getTimerService(timerId);
    _timerService.cancelTimer();
  }

  @override
  void onContinueRequest(TimerRequest arg) {
    final timerId = arg.timerId;
    log.i('onContinueRequest called for timer with id $timerId');

    final _timerService =
        TimerServiceManager.getInstance().getTimerService(timerId);
    _timerService.start();
  }

  @override
  void onRestartRequest(TimerRequest arg) {
    final timerId = arg.timerId;
    log.i('onRestartRequest called for timer with id $timerId');

    final _timerService =
        TimerServiceManager.getInstance().getTimerService(timerId);
    _timerService.restartTimer();
  }

  @override
  void onAlarm(TimerRequest arg) {
    log.i('onAlarm()');
    alarmCallback(arg.timerId);
  }

  @override
  WidgetUpdateResponse onWidgetUpdate() {
    log.i('onWidgetUpdate called on dart side');
    return WidgetUpdateResponse()..title = 'Test ';
  }

  @override
  void onWidgetStartTimer() {
    log.i('onWidgetStartTimer called on dart side');

    final timerModel = TimerModel(120, startActionList, actionList);
    final _timerService = locator<TimerService>(param1: timerModel);
    TimerServiceManager.getInstance().setTimerService(_timerService);
    _timerService.start();
  }
}
