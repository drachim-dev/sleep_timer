import 'package:flutter/scheduler.dart';
import 'package:logger/logger.dart';
import 'package:sleep_timer/app/locator.dart';
import 'package:sleep_timer/app/logger.util.dart';
import 'package:sleep_timer/messages_generated.dart';
import 'package:sleep_timer/model/action_model.dart';
import 'package:sleep_timer/model/timer_model.dart';
import 'package:sleep_timer/platform_interface.dart';
import 'package:sleep_timer/services/timer_service.dart';
import 'package:stacked_services/stacked_services.dart';

import 'app/app.router.dart';
import 'common/timer_service_manager.dart';

class SleepTimerPlatformImpl implements SleepTimerPlatform {
  final HostTimerApi _hostApi = HostTimerApi();

  @override
  Future<bool> showRunningNotification(
      {required final String timerId,
      required final String title,
      required final String description,
      required final int accentColor,
      final String? restartAction,
      final String? pauseAction,
      final String? cancelAction,
      final List<int>? extendActions,
      required final int duration,
      required final int remainingTime,
      required final bool shakeToExtend}) async {
    final response = await _hostApi.showRunningNotification(
      RunningNotificationRequest(
        timerId: timerId,
        title: title,
        description: description,
        accentColor: accentColor,
        restartAction: restartAction,
        pauseAction: pauseAction,
        cancelAction: cancelAction,
        extendActions: extendActions,
        duration: duration,
        remainingTime: remainingTime,
        shakeToExtend: shakeToExtend,
      ),
    );
    return response.success ?? false;
  }

  @override
  Future<bool> showPausingNotification(
      {required final String timerId,
      required final String title,
      required final String description,
      required final int accentColor,
      final String? restartAction,
      final String? continueAction,
      final String? cancelAction,
      final List<int>? extendActions,
      required final int remainingTime}) async {
    final response = await _hostApi.showPausingNotification(
      TimeNotificationRequest(
        timerId: timerId,
        title: title,
        description: description,
        accentColor: accentColor,
        restartAction: restartAction,
        continueAction: continueAction,
        cancelAction: cancelAction,
        extendActions: extendActions,
        remainingTime: remainingTime,
      ),
    );
    return response.success ?? false;
  }

  @override
  Future<bool> showElapsedNotification({
    required final String timerId,
    required final String title,
    required final String description,
    required final int accentColor,
    final String? restartAction,
  }) async {
    final response = await _hostApi.showElapsedNotification(
      NotificationRequest(
        timerId: timerId,
        title: title,
        description: description,
        accentColor: accentColor,
        restartAction: restartAction,
      ),
    );
    return response.success ?? false;
  }

  @override
  Future<bool> cancelTimer(final String timerId) async {
    final response =
        await _hostApi.cancelTimer(CancelRequest(timerId: timerId));
    return response.success ?? false;
  }

  @override
  Future<void> toggleExtendByShake(final bool enable) async {
    await _hostApi.toggleExtendByShake(ToggleRequest(enable: enable));
  }

  @override
  Future<List<Package>> getInstalledPlayerApps() async {
    final response = await _hostApi.getInstalledPlayerApps();

    if (response.apps == null) {
      return [];
    }

    final apps = response.apps!.nonNulls.toList()
      ..sort((a, b) => a.title!.compareTo(b.title!));
    return apps;
  }

  @override
  Future<List<Package>> getInstalledAlarmApps() async {
    final response = await _hostApi.getInstalledAlarmApps();

    if (response.apps == null) {
      return [];
    }

    final apps = response.apps!.nonNulls.toList()
      ..sort((a, b) => a.title!.compareTo(b.title!));
    return apps;
  }

  @override
  Future<void> launchApp(final String? packageName) async {
    await _hostApi.launchApp(LaunchAppRequest(packageName: packageName));
  }
}

class FlutterApiHandler extends FlutterTimerApi {
  final Logger log = getLogger();
  final Function? alarmCallback;

  FlutterApiHandler({this.alarmCallback});

  @override
  void onExtendTime(ExtendTimeRequest request) {
    final timerId = request.timerId;
    log.d(
        'extend time by: ${request.additionalTime} for timer with id $timerId');

    if (timerId != null) {
      TimerServiceManager.instance
          .getTimerService(timerId)
          ?.extendTime(request.additionalTime);
    }
  }

  @override
  void onCountDown(CountDownRequest request) {
    final timerId = request.timerId;
    log.d(
        'new time after countdown: ${request.newTime} for timer with id $timerId');

    if (timerId != null) {
      TimerServiceManager.instance
          .getTimerService(timerId)
          ?.setRemainingTime(request.newTime);
    }
  }

  @override
  void onOpen(OpenRequest request) {
    final timerId = request.timerId;
    log.d('onOpen called for timer with id $timerId');

    TimerService? timerService;
    if (timerId != null) {
      timerService = TimerServiceManager.instance.getTimerService(timerId);
    }

    // Wait for Flutter engine to be attached and runApp to be active
    SchedulerBinding.instance.addPostFrameCallback((_) {
      // _timerService was terminated by system
      if (timerService == null) {
        StackedService.navigatorKey!.currentState!
            .pushNamedAndRemoveUntil(Routes.homeView, (route) => false);
      } else {
        // Navigate to timer detail view
        StackedService.navigatorKey!.currentState!.pushNamedAndRemoveUntil(
            Routes.homeView, (route) => false,
            arguments: HomeViewArguments(timerId: timerService.timerModel!.id));

        StackedService.navigatorKey!.currentState!.pushNamed(Routes.timerView,
            arguments: TimerViewArguments(timerModel: timerService.timerModel));
      }
    });
  }

  @override
  void onPauseRequest(TimerRequest request) {
    final timerId = request.timerId;
    log.d('onPauseRequest requested for timer with id $timerId');

    if (timerId != null) {
      TimerServiceManager.instance.getTimerService(timerId)?.pauseTimer();
    }
  }

  @override
  void onCancelRequest(TimerRequest request) {
    final timerId = request.timerId;
    log.d('onCancelRequest called for timer with id $timerId');

    if (timerId != null) {
      TimerServiceManager.instance.getTimerService(timerId)?.cancelTimer();
    }

    StackedService.navigatorKey!.currentState!
        .popUntil((route) => route.settings.name != Routes.timerView);
  }

  @override
  void onContinueRequest(TimerRequest request) {
    final timerId = request.timerId;
    log.d('onContinueRequest called for timer with id $timerId');

    if (timerId != null) {
      TimerServiceManager.instance.getTimerService(timerId)?.start();
    }
  }

  @override
  void onRestartRequest(TimerRequest request) {
    final timerId = request.timerId;
    log.d('onRestartRequest called for timer with id $timerId');

    if (timerId != null) {
      TimerServiceManager.instance.getTimerService(timerId)?.start();
    }
  }

  @override
  void onAlarm(TimerRequest request) {
    log.d('onAlarm()');
    alarmCallback!(request.timerId);
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
    final timerService = locator<TimerService>(param1: timerModel)..start();
    TimerServiceManager.instance.setTimerService(timerService);
  }
}
