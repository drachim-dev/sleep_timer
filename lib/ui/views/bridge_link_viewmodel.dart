import 'package:logger/logger.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:sleep_timer/app/app.router.dart';
import 'package:sleep_timer/app/locator.dart';
import 'package:sleep_timer/app/logger.util.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/model/bridge_model.dart';
import 'package:sleep_timer/services/light_service.dart';
import 'package:stacked/stacked.dart';
import 'package:stacked_services/stacked_services.dart';

class BridgeLinkViewModel extends FutureViewModel {
  final Logger log = getLogger();
  final NavigationService _navigationService = locator<NavigationService>();
  final SharedPreferences _prefsService = locator<SharedPreferences>();
  final LightService _lightService = locator<LightService>();

  String connectionError = '';

  @override
  Future futureToRun() async {
    final savedBridges = await _lightService.getSavedBridges();
    return savedBridges;
  }

  void _resetError() {
    connectionError = '';
  }

  Future<void> linkBridge(final BridgeModel bridgeModel) async {
    await _lightService.linkBridge(bridgeModel);
    _resetError();

    navigateBackToLights();

    notifyListeners();
  }

  Future<void> remove(final BridgeModel bridgeModel) async {
    // TODO: Remove bridge
    await _prefsService.remove(kPrefKeyHueBridges);
    _navigationService.back();
    await initialise();
  }

  void navigateBackToLights() {
    _navigationService
        .popUntil((route) => route.settings.name == Routes.lightGroupView);
  }

  void cancelDialog() {
    _navigationService.back();
    _resetError();
  }
}
