import 'package:injectable/injectable.dart';
import 'package:sleep_timer/services/timer_service.dart';

@lazySingleton
class TimerServiceManager {
  Map<String, TimerService> _timerServices = {};

  TimerService getTimerService(final String id) {
    return _timerServices[id];
  }

  void setTimerService(
      final TimerService timerService) {
    _timerServices[timerService.timerModel.id] = timerService;
  }
}
