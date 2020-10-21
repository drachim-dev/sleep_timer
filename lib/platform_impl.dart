import 'package:flutter/cupertino.dart';
import 'package:sleep_timer/app/locator.dart';
import 'package:sleep_timer/platform_interface.dart';
import 'package:sleep_timer/messages_generated.dart';

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
      @required final List<String> actions,
      @required final int duration,
      @required final int remainingTime}) async {
    final ShowNotificationResponse response =
        await _hostApi.showRunningNotification(ShowRunningNotificationRequest()
          ..timerId = timerId
          ..title = title
          ..description = description
          ..actions = actions
          ..duration = duration
          ..remainingTime = remainingTime);
    return response.success;
  }

  @override
  Future<bool> showPausingNotification(
      {@required final String timerId,
      @required final String title,
      @required final String description,
      @required final List<String> actions,
      @required final int remainingTime}) async {
    final ShowNotificationResponse response =
        await _hostApi.showPausingNotification(ShowPausingNotificationRequest()
          ..timerId = timerId
          ..title = title
          ..description = description
          ..actions = actions
          ..remainingTime = remainingTime);
    return response.success;
  }

  @override
  Future<bool> showElapsedNotification(
      {@required final String timerId,
      @required final String title,
      @required final String description,
      @required final List<String> actions}) async {
    final ShowNotificationResponse response =
        await _hostApi.showElapsedNotification(ShowElapsedNotificationRequest()
          ..timerId = timerId
          ..title = title
          ..description = description
          ..actions = actions);
    return response.success;
  }

  @override
  Future<bool> cancelNotification(final String timerId) async {
    final CancelNotificationResponse response = await _hostApi
        .cancelNotification(CancelNotificationRequest()..timerId = timerId);
    return response.success;
  }

}

class FlutterApiHandler extends FlutterTimerApi {
  final Function callback;
  final Function alarmCallback;

  FlutterApiHandler({this.callback, this.alarmCallback});

  @override
  void onShowRunningNotification(ShowNotificationResponse arg) {
    final String timerId = arg.timerId;
    print("onShowRunningNotification called for timer with id $timerId");
    callback();
  }

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
  }

  @override
  void onPauseRequest(PauseRequest arg) {
    final String timerId = arg.timerId;
    print("onPauseRequest requested for timer with id $timerId");

    final _timerService =
    TimerServiceManager.getInstance().getTimerService(timerId);
    _timerService.pauseTimer();
  }

  @override
  void onCancelRequest(CancelRequest arg) {
    final String timerId = arg.timerId;
    print("onCancelRequest called for timer with id $timerId");

    final _timerService =
    TimerServiceManager.getInstance().getTimerService(timerId);
    _timerService.cancelTimer();
  }

  @override
  void onContinueRequest(ContinueRequest arg) {
    final String timerId = arg.timerId;
    print("onContinueRequest called for timer with id $timerId");

    final _timerService =
    TimerServiceManager.getInstance().getTimerService(timerId);
    _timerService.start();
  }

  @override
  void onRestartRequest(RestartRequest arg) {
    final String timerId = arg.timerId;
    print("onRestartRequest called for timer with id $timerId");

    final _timerService =
    TimerServiceManager.getInstance().getTimerService(timerId);
    _timerService.restartTimer();
  }

  @override
  void onAlarm(AlarmRequest arg) {
    print("onAlarm()");
    alarmCallback(arg.timerId);
  }

}
