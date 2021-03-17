import 'package:shared_preferences/shared_preferences.dart';
import 'package:sleep_timer/app/auto_router.gr.dart';
import 'package:sleep_timer/app/locator.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/common/timer_service_manager.dart';
import 'package:sleep_timer/model/action_model.dart';
import 'package:sleep_timer/model/timer_model.dart';
import 'package:sleep_timer/services/review_service.dart';
import 'package:sleep_timer/services/theme_service.dart';
import 'package:stacked/stacked.dart';
import 'package:stacked_services/stacked_services.dart';

class HomeViewModel extends BaseViewModel {
  final _navigationService = locator<NavigationService>();
  final _prefService = locator<SharedPreferences>();
  final _reviewService = locator<ReviewService>();
  final _themeService = locator<ThemeService>();

  bool get showGlow => _themeService.showGlow;

  int _initialTime = kDefaultInitialTime;
  int get initialTime => _initialTime;

  String activeTimerId;
  bool get hasActiveTimer => activeTimerId != null;

  // lock variable to prevent multiple calls due to rebuild
  bool mayAskForReviewLocked = false;

  HomeViewModel() {
    _initialTime = _prefService.getInt(kPrefKeyInitialTime) ?? _initialTime;

    startActionList.forEach((element) {
      element.enabled =
          _prefService.getBool(element.id.toString()) ?? element.enabled;

      if (element is ValueActionModel) {
        element.value = _prefService.getDouble(element.key) ?? element.value;
      }
    });

    endActionList.forEach((element) {
      element.enabled =
          _prefService.get(element.id.toString()) ?? element.enabled;
    });
  }

  void startNewTimer() async {
    final timerModel =
        TimerModel(_initialTime * 60, startActionList, endActionList);

    activeTimerId = await _navigationService.navigateTo(Routes.timerView,
        arguments: TimerViewArguments(timerModel: timerModel));
    notifyListeners();
  }

  void openActiveTimer() async {
    if (hasActiveTimer) {
      final timerModel = TimerServiceManager.getInstance()
          .getTimerService(activeTimerId)
          .timerModel;

      activeTimerId = await _navigationService.navigateTo(Routes.timerView,
          arguments: TimerViewArguments(timerModel: timerModel));
      notifyListeners();
    }
  }

  void updateTime(int value) {
    _initialTime = value;
    _prefService.setInt(kPrefKeyInitialTime, value);
    notifyListeners();
  }

  void setTime(int value) {
    _initialTime = value;
    _prefService.setInt(kPrefKeyInitialTime, value);
  }

  Future<void> mayAskForReview() async {
    if (!mayAskForReviewLocked && !hasActiveTimer) {
      // to prevent multiple calls
      mayAskForReviewLocked = true;
      final shouldAsk = _reviewService.shouldAskForReview();
      if (shouldAsk) {
        await _reviewService.requestReview();
      }
    }
  }

  Future navigateToSettings() async {
    await _navigationService.navigateTo(Routes.settingsView);
  }
}
