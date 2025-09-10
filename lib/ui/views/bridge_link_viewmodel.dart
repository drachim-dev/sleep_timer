import 'package:collection/collection.dart';
import 'package:logger/logger.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:sleep_timer/app/app.locator.dart';
import 'package:sleep_timer/app/app.router.dart';
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

  @override
  Future<List<BridgeModel>> futureToRun() async {
    try {
      final newDevices = await _lightService.discoverBridges();
      final savedBridges = await _lightService.getSavedBridges();

      final allIps =
          savedBridges.map((e) => e.ip).whereType<String>().toList() +
          newDevices;

      return allIps.map((e) {
        final savedBridge = savedBridges.firstWhereOrNull(
          (element) => element.ip == e,
        );
        return savedBridge ?? BridgeModel(id: e, ip: e);
      }).toList();
    } catch (error) {
      setError(error);
      rethrow;
    }
  }

  Future<Connection> linkBridge(final BridgeModel bridgeModel) async {
    final result = await _lightService.linkBridge(bridgeModel);

    if (result == Connection.connected) {
      navigateBackToLights();
    }

    return result;
  }

  Future<void> remove(final BridgeModel bridgeModel) async {
    await _prefsService.remove(kPrefKeyHueBridges);
    _navigationService.back();
    await initialise();
  }

  void navigateBackToLights() {
    _navigationService.popUntil(
      (route) => route.settings.name == Routes.lightGroupView,
    );
  }

  void cancelDialog() {
    _navigationService.back();
  }
}
