import 'package:shared_preferences/shared_preferences.dart';
import 'package:sleep_timer/app/app.router.dart';
import 'package:sleep_timer/app/locator.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:stacked/stacked.dart';
import 'package:stacked_services/stacked_services.dart';

class IntroViewModel extends BaseViewModel {
  final NavigationService _navigationService = locator<NavigationService>();
  final SharedPreferences _prefsService = locator<SharedPreferences>();

  Future navigateToHome() async {
    await _prefsService.setBool(kPrefKeyFirstLaunch, false);
    await _navigationService.replaceWith(Routes.homeView);
  }
}
