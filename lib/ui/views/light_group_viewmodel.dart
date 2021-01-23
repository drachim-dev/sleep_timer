import 'package:shared_preferences/shared_preferences.dart';
import 'package:sleep_timer/app/locator.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/app/auto_router.gr.dart';
import 'package:sleep_timer/model/bridge_model.dart';
import 'package:sleep_timer/services/light_service.dart';
import 'package:stacked/stacked.dart';
import 'package:stacked_services/stacked_services.dart';

class LightGroupViewModel extends FutureViewModel {
  final _navigationService = locator<NavigationService>();
  final _prefsService = locator<SharedPreferences>();
  final _lightService = locator<LightService>();

  @override
  Future futureToRun() async {
    final savedBridgesJson = _prefsService.getString(kPrefKeyHueBridges);
    var savedBridges = <BridgeModel>[];
    if (savedBridgesJson != null) {
      savedBridges = BridgeModel.decode(savedBridgesJson);
    }

    await Future.forEach(savedBridges, (BridgeModel bridge) async {
      final savedEntry = savedBridges.firstWhere(
          (element) => element.id == element.id,
          orElse: () => null);

      if (savedEntry != null) {
        bridge = savedEntry
          ..state = await _lightService.getConnectionState(bridge);

        if (bridge.state == Connection.connected) {
          var allGroups = await _lightService.getRooms(bridge);

          // check for possible new groups
          allGroups.forEach((group) {
            var exists =
                bridge.groups.any((savedGroup) => savedGroup.id == group.id);
            if (!exists) {
              bridge.groups.add(group);
            }
          });
        }
      }
    });

    return savedBridges
        .where((element) => element.state == Connection.connected).toList();
  }

  void onChangeRoom(final bool value) async {
    await _prefsService.setString(kPrefKeyHueBridges, BridgeModel.encode(data));
    notifyListeners();
  }

  Future<void> navigateToLinkBridge() async {
    await _navigationService.navigateTo(Routes.bridgeLinkView);
    await initialise();
  }
}
