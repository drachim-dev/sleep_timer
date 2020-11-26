import 'package:sleep_timer/services/timer_service.dart';

class TimerServiceManager {
  static TimerServiceManager _instance;

  static TimerServiceManager getInstance({name, age}) {
    if (_instance == null) {
      _instance = TimerServiceManager();
      return _instance;
    }
    return _instance;
  }

  final Map<String, TimerService> _timerServices = {};

  TimerService getTimerService(final String id) {
    return _timerServices[id];
  }

  void setTimerService(final TimerService timerService) {
    _timerServices[timerService.timerModel.id] = timerService;
  }
}
