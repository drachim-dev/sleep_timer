import 'package:sleep_timer/app/locator.dart';
import 'package:sleep_timer/app/auto_router.gr.dart';
import 'package:stacked/stacked.dart';
import 'package:stacked_services/stacked_services.dart';

class HomeViewModel extends IndexTrackingViewModel {
  final _navigationService = locator<NavigationService>();

  Future navigateToSettings() async {
    await _navigationService.navigateTo(Routes.settingsView);
  }

}