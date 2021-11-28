import 'package:sleep_timer/services/timer_service.dart';

class TimerServiceManager {
  static final TimerServiceManager _instance = TimerServiceManager();
  static TimerServiceManager get instance => _instance;

  final Map<String, TimerService> _timerServices = {};

  TimerService? getTimerService(final String id) {
    return _timerServices[id];
  }

  void setTimerService(final TimerService timerService) {
    _timerServices[timerService.timerModel!.id] = timerService;
  }
}
