import 'package:flutter/cupertino.dart';
import 'package:sleep_timer/app/auto_router.gr.dart';
import 'package:sleep_timer/app/locator.dart';
import 'package:sleep_timer/platform_interface.dart';
import 'package:sleep_timer/messages_generated.dart';
import 'package:stacked_services/stacked_services.dart';

import 'common/timer_service_manager.dart';

class SleepTimerPlatformImpl implements SleepTimerPlatform {
  HostTimerApi _hostApi = HostTimerApi();

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
    final NotificationResponse response =
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
    final NotificationResponse response =
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
    final NotificationResponse response =
        await _hostApi.showElapsedNotification(NotificationRequest()
          ..timerId = timerId
          ..title = title
          ..description = description
          ..restartAction = restartAction);
    return response.success;
  }

  @override
  Future<bool> cancelTimer(final String timerId) async {
    final CancelResponse response =
        await _hostApi.cancelTimer(CancelRequest()..timerId = timerId);
    return response.success;
  }
}

class FlutterApiHandler extends FlutterTimerApi {
  final Function callback;
  final Function alarmCallback;

  FlutterApiHandler({this.callback, this.alarmCallback});

  @override
  void onExtendTime(ExtendTimeResponse arg) {
    final String timerId = arg.timerId;
    print("extend time by: ${arg.additionalTime} for timer with id $timerId");

    final _timerService =
        TimerServiceManager.getInstance().getTimerService(timerId);
    _timerService.extendTime(arg.additionalTime);
  }

  @override
  void onOpen(OpenRequest arg) {
    final String timerId = arg.timerId;
    print("onOpen called for timer with id $timerId");

    final _timerService =
        TimerServiceManager.getInstance().getTimerService(timerId);
    final _navigationService = locator<NavigationService>();

    // Navigate to timer detail view
    _navigationService.popUntil((route) => route.isFirst);
    _navigationService.navigateTo(Routes.timerDetailView,
        arguments: TimerDetailViewArguments(timerModel: _timerService.timerModel));
  }

  @override
  void onPauseRequest(TimerRequest arg) {
    final String timerId = arg.timerId;
    print("onPauseRequest requested for timer with id $timerId");

    final _timerService =
        TimerServiceManager.getInstance().getTimerService(timerId);
    _timerService.pauseTimer();
  }

  @override
  void onCancelRequest(TimerRequest arg) {
    final String timerId = arg.timerId;
    print("onCancelRequest called for timer with id $timerId");

    final _timerService =
        TimerServiceManager.getInstance().getTimerService(timerId);
    _timerService.cancelTimer();
  }

  @override
  void onContinueRequest(TimerRequest arg) {
    final String timerId = arg.timerId;
    print("onContinueRequest called for timer with id $timerId");

    final _timerService =
        TimerServiceManager.getInstance().getTimerService(timerId);
    _timerService.start();
  }

  @override
  void onRestartRequest(TimerRequest arg) {
    final String timerId = arg.timerId;
    print("onRestartRequest called for timer with id $timerId");

    final _timerService =
        TimerServiceManager.getInstance().getTimerService(timerId);
    _timerService.restartTimer();
  }

  @override
  void onAlarm(TimerRequest arg) {
    print("onAlarm()");
    alarmCallback(arg.timerId);
  }
}
