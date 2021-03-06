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
  final AdService _adService = locator<AdService>();
  final PurchaseService _purchaseService = locator<PurchaseService>();

  bool get showGlow => _themeService.showGlow;

  int _initialTime = kDefaultInitialTime;
  int get initialTime => _initialTime;

  String? activeTimerId;
  bool get hasActiveTimer => activeTimerId != null;

  // lock variable to prevent multiple calls due to rebuild
  bool mayAskForReviewLocked = false;

  bool get isAdFree => _purchaseService.adFree;

  HomeViewModel() {
    _initialTime = _prefService.getInt(kPrefKeyInitialTime) ?? _initialTime;

    startActionList.forEach((element) {
      element.enabled =
          _prefService.getBool(element.id.toString()) ?? element.enabled;

      if (element is ValueActionModel) {
        element.value = _prefService.getDouble(element.key!) ?? element.value;
      }
    });

    endActionList.forEach((element) {
      element.enabled =
          _prefService.get(element.id.toString()) as bool? ?? element.enabled;
    });

    _adService.createInterstitialAd();
  }

  void startNewTimer() async {
    if (!isAdFree) {
      await _adService.mayShow();
    }

    final timerModel =
        TimerModel(_initialTime * 60, startActionList, endActionList);

    activeTimerId = await (_navigationService.navigateTo(Routes.timerView,
        arguments: TimerViewArguments(timerModel: timerModel)));
    notifyListeners();
  }

  void openActiveTimer() async {
    if (hasActiveTimer) {
      final timerModel = TimerServiceManager.instance
          .getTimerService(activeTimerId)!
          .timerModel;

      activeTimerId = await (_navigationService.navigateTo(Routes.timerView,
          arguments: TimerViewArguments(timerModel: timerModel)));
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
