import 'package:shared_preferences/shared_preferences.dart';
import 'package:sleep_timer/app/app.router.dart';
import 'package:sleep_timer/app/locator.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/common/timer_service_manager.dart';
import 'package:sleep_timer/model/action_model.dart';
import 'package:sleep_timer/model/timer_model.dart';
import 'package:sleep_timer/services/ad_service.dart';
import 'package:sleep_timer/services/purchase_service.dart';
import 'package:sleep_timer/services/review_service.dart';
import 'package:sleep_timer/services/theme_service.dart';
import 'package:stacked/stacked.dart';
import 'package:stacked_services/stacked_services.dart';

class HomeViewModel extends BaseViewModel {
  final NavigationService _navigationService = locator<NavigationService>();
  final SharedPreferences _prefService = locator<SharedPreferences>();
  final ReviewService _reviewService = locator<ReviewService>();
  final ThemeService _themeService = locator<ThemeService>();
  final Future<AdService> _adService = locator.getAsync<AdService>();
  final Future<PurchaseService> _purchaseService =
      locator.getAsync<PurchaseService>();

  bool get showGlow => _themeService.showGlow;

  int _initialTime = kDefaultInitialTime;
  int get initialTime => _initialTime;

  String? activeTimerId;
  bool get hasActiveTimer => activeTimerId != null;

  // lock variable to prevent multiple calls due to rebuild
  bool mayAskForReviewLocked = false;

  HomeViewModel() {
    _initialTime = _prefService.getInt(kPrefKeyInitialTime) ?? _initialTime;

    for (var element in startActionList) {
      element.enabled =
          _prefService.getBool(element.id.toString()) ?? element.enabled;

      if (element is ValueActionModel) {
        element.value = _prefService.getDouble(element.key!) ?? element.value;
      }
    }

    for (var element in endActionList) {
      element.enabled =
          _prefService.getBool(element.id.toString()) ?? element.enabled;
    }
  }

  void startNewTimer() {
    _purchaseService.then((purchaseService) {
      if (!purchaseService.adFree) {
        _adService.then((adService) => adService.mayShow());
      }
    });

    final timerModel =
        TimerModel(_initialTime * 60, startActionList, endActionList);

    final resultFuture = _navigationService.navigateTo(Routes.timerView,
        arguments: TimerViewArguments(timerModel: timerModel));

    resultFuture?.then((value) {
      activeTimerId = value;
      notifyListeners();
    });
  }

  void openActiveTimer() async {
    if (hasActiveTimer && activeTimerId != null) {
      final timerModel = TimerServiceManager.instance
          .getTimerService(activeTimerId!)!
          .timerModel;

      final result = await (_navigationService.navigateTo(Routes.timerView,
          arguments: TimerViewArguments(timerModel: timerModel)));

      activeTimerId = result;
      notifyListeners();
    }
  }

  /// Sets the time without notifying listeners.
  void setTimeSilent(int value) {
    _initialTime = value;
    _prefService.setInt(kPrefKeyInitialTime, value);
  }

  void setTime(int value) {
    setTimeSilent(value);
    notifyListeners();
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

  Future navigateToSettings() async =>
      await _navigationService.navigateTo(Routes.settingsView);
}
