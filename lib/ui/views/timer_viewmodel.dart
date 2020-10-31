import 'package:shared_preferences/shared_preferences.dart';
import 'package:sleep_timer/app/auto_router.gr.dart';
import 'package:sleep_timer/app/locator.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/common/timer_service_manager.dart';
import 'package:sleep_timer/model/action_model.dart';
import 'package:sleep_timer/model/timer_model.dart';
import 'package:stacked/stacked.dart';
import 'package:stacked_services/stacked_services.dart';

class TimerViewModel extends BaseViewModel {
  final _navigationService = locator<NavigationService>();
  final _prefService = locator<SharedPreferences>();

  int _initialTime = kDefaultInitialTime;
  int get initialTime => _initialTime;

  String _activeTimerId;
  bool get hasActiveTimer => _activeTimerId != null;

  TimerViewModel() {
    _initialTime = _prefService.getInt(kPrefKeyInitialTime) ?? _initialTime;
    actionList.forEach((element) {
      element.enabled =
          _prefService.get(element.id.toString()) ?? element.enabled;
    });
  }

  void startNewTimer() async {
    final TimerModel timerModel = TimerModel(_initialTime * 60, actionList);

    _activeTimerId = await _navigationService.navigateTo(Routes.timerDetailView,
        arguments: TimerDetailViewArguments(timerModel: timerModel));
    notifyListeners();
  }

  void openActiveTimer() async {
    if (hasActiveTimer) {
      final TimerModel timerModel = TimerServiceManager.getInstance()
          .getTimerService(_activeTimerId)
          .timerModel;

      _activeTimerId = await _navigationService.navigateTo(
          Routes.timerDetailView,
          arguments: TimerDetailViewArguments(timerModel: timerModel));
      notifyListeners();
    }
  }

  void updateTime(int value) {
    setTime(value);
    notifyListeners();
  }

  void setTime(int value) {
    _initialTime = value;
    _prefService.setInt(kPrefKeyInitialTime, value);
  }
}
