import 'package:hue_dart/hue_dart.dart';
import 'package:logger/logger.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:sleep_timer/app/auto_router.gr.dart';
import 'package:sleep_timer/app/locator.dart';
import 'package:sleep_timer/app/logger.util.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/model/bridge_model.dart';
import 'package:sleep_timer/services/light_service.dart';
import 'package:stacked/stacked.dart';
import 'package:stacked_services/stacked_services.dart';

class BridgeLinkViewModel extends FutureViewModel {
  final Logger log = getLogger();
  final _navigationService = locator<NavigationService>();
  final _prefsService = locator<SharedPreferences>();
  final _lightService = locator<LightService>();

  String connectionError = '';

  @override
  Future futureToRun() async {
    try {
      final bridges = await _lightService.findBridges();

      final savedBridgesJson = _prefsService.getString(kPrefKeyHueBridges);

      if (savedBridgesJson == null) return bridges;

      final savedBridges = BridgeModel.decode(savedBridgesJson);
      await Future.forEach(bridges, (BridgeModel element) async {
        final savedEntry = savedBridges.firstWhere(
            (element) => element.id == element.id,
            orElse: () => null);

        if (savedEntry != null) {
          element
            ..auth = savedEntry.auth
            ..state = await _lightService.getConnectionState(element);
        }
      });

      return bridges;
    } catch (error) {
      setError(error);
      rethrow;
    }
  }

  void _resetError() {
    connectionError = '';
  }

  Future<void> linkBridge(final BridgeModel bridgeModel) async {
    try {
      await _lightService.linkBridge(bridgeModel);
      _resetError();

      navigateBackToLights();
    } on BridgeException catch (error) {
      connectionError = error.description.toUpperCase();
      notifyListeners();
      log.e(error);
    }

    notifyListeners();
  }

  Future<bool> connect(final BridgeModel bridgeModel) async {
    final success = await _lightService.linkBridge(bridgeModel);

    if (success) {
      final savedBridgesJson = _prefsService.getString(kPrefKeyHueBridges);
      var savedBridges = <BridgeModel>[];

      if (savedBridgesJson != null) {
        savedBridges = BridgeModel.decode(savedBridgesJson);
        final savedEntry = savedBridges.firstWhere(
            (element) => element.id == bridgeModel.id,
            orElse: () => null);

        // match with saved bridge --> update
        if (savedEntry != null) {
          savedEntry.auth = bridgeModel.auth;
        } else {
          // no match with saved bridges --> add
          savedBridges.add(bridgeModel);
        }
      } else {
        // no saved bridges --> add
        savedBridges.add(bridgeModel);
      }

      // save updates
      await _prefsService.setString(
          kPrefKeyHueBridges, BridgeModel.encode(savedBridges));
    }

    return success;
  }

  Future<void> remove(final BridgeModel bridgeModel) async {
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
