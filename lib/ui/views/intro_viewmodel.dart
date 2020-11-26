import 'package:shared_preferences/shared_preferences.dart';
import 'package:sleep_timer/app/auto_router.gr.dart';
import 'package:sleep_timer/app/locator.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:stacked/stacked.dart';
import 'package:stacked_services/stacked_services.dart';

class IntroViewModel extends BaseViewModel {
  final _navigationService = locator<NavigationService>();
  final _prefsService = locator<SharedPreferences>();

  Future navigateToHome() async {
    await _prefsService.setBool(kPrefKeyFirstLaunch, false);
    await _navigationService.replaceWith(Routes.homeView);
  }
}
