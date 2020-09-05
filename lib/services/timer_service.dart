import 'package:injectable/injectable.dart';
import 'package:observable_ish/observable_ish.dart';
import 'package:stacked/stacked.dart';

@injectable
class TimerService with ReactiveServiceMixin {
  TimerService(@factoryParam int initialTime)
      : _remainingTime = RxValue<int>(initial: initialTime) {
    listenToReactiveValues([_remainingTime]);
  }

  final RxValue<int> _remainingTime;
  int get remainingTime => _remainingTime.value;

  void count() {
    _remainingTime.value--;
  }

  void setRemainingTime(int value) {
    _remainingTime.value = value;
  }

  void extendTime(int seconds) {
    _remainingTime.value += seconds;
  }
}
