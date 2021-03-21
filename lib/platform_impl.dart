import 'package:flutter/cupertino.dart';
import 'package:flutter/scheduler.dart';
import 'package:logger/logger.dart';
import 'package:sleep_timer/app/auto_router.gr.dart';
import 'package:sleep_timer/app/locator.dart';
import 'package:sleep_timer/app/logger.util.dart';
import 'package:sleep_timer/messages_generated.dart';
import 'package:sleep_timer/model/action_model.dart';
import 'package:sleep_timer/model/app.dart';
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
      @required final int remainingTime,
      @required final bool shakeToExtend}) async {
    final response =
        await _hostApi.showRunningNotification(RunningNotificationRequest()
          ..timerId = timerId
          ..title = title
          ..description = description
          ..restartAction = restartAction
          ..pauseAction = pauseAction
          ..cancelAction = cancelAction
          ..extendActions = extendActions
          ..duration = duration
          ..remainingTime = remainingTime
          ..shakeToExtend = shakeToExtend);
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
    log.d('extend time by: ${arg.additionalTime} for timer with id $timerId');

    TimerServiceManager.getInstance()
        .getTimerService(timerId)
        ?.extendTime(arg.additionalTime);
  }

  @override
  void onCountDown(CountDownRequest arg) {
    final timerId = arg.timerId;
    log.d(
        'new time after countdown: ${arg.newTime} for timer with id $timerId');

    TimerServiceManager.getInstance()
        .getTimerService(timerId)
        ?.setRemainingTime(arg.newTime);
  }

  @override
  void onOpen(OpenRequest arg) {
    final timerId = arg.timerId;
    log.d('onOpen called for timer with id $timerId');

    final _timerService =
        TimerServiceManager.getInstance().getTimerService(timerId);

    // Wait for Flutter engine to be attached and runApp to be active
    SchedulerBinding.instance.addPostFrameCallback((_) {
      // _timerService was terminated by system
      if (_timerService == null) {
        StackedService.navigatorKey.currentState
            .pushNamedAndRemoveUntil(Routes.homeView, (route) => false);
      } else {
        // Navigate to timer detail view
        StackedService.navigatorKey.currentState.pushNamedAndRemoveUntil(
            Routes.homeView, (route) => false,
            arguments: HomeViewArguments(timerId: _timerService.timerModel.id));

        StackedService.navigatorKey.currentState.pushNamed(Routes.timerView,
            arguments:
                TimerViewArguments(timerModel: _timerService.timerModel));
      }
    });
  }

  @override
  void onPauseRequest(TimerRequest arg) {
    final timerId = arg.timerId;
    log.d('onPauseRequest requested for timer with id $timerId');

    TimerServiceManager.getInstance().getTimerService(timerId)?.pauseTimer();
  }

  @override
  void onCancelRequest(TimerRequest arg) {
    final timerId = arg.timerId;
    log.d('onCancelRequest called for timer with id $timerId');

    TimerServiceManager.getInstance().getTimerService(timerId)?.cancelTimer();

    StackedService.navigatorKey.currentState
        .popUntil((route) => route.settings.name != Routes.timerView);
  }

  @override
  void onContinueRequest(TimerRequest arg) {
    final timerId = arg.timerId;
    log.d('onContinueRequest called for timer with id $timerId');

    TimerServiceManager.getInstance().getTimerService(timerId)?.start();
  }

  @override
  void onRestartRequest(TimerRequest arg) {
    final timerId = arg.timerId;
    log.d('onRestartRequest called for timer with id $timerId');

    TimerServiceManager.getInstance().getTimerService(timerId)?.start();
  }

  @override
  void onAlarm(TimerRequest arg) {
    log.d('onAlarm()');
    alarmCallback(arg.timerId);
  }

  @override
  WidgetUpdateResponse onWidgetUpdate() {
    log.d('onWidgetUpdate called on dart side');
    return WidgetUpdateResponse()..title = 'Test ';
  }

  @override
  void onWidgetStartTimer() {
    log.d('onWidgetStartTimer called on dart side');

    final timerModel = TimerModel(120, startActionList, endActionList);
    final _timerService = locator<TimerService>(param1: timerModel)..start();
    TimerServiceManager.getInstance().setTimerService(_timerService);
  }
}
