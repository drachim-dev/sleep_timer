import 'package:shared_preferences/shared_preferences.dart';
import 'package:sleep_timer/app/app.router.dart';
import 'package:sleep_timer/app/locator.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/model/bridge_model.dart';
import 'package:sleep_timer/services/light_service.dart';
import 'package:stacked/stacked.dart';
import 'package:stacked_services/stacked_services.dart';

class LightGroupViewModel extends FutureViewModel {
  final NavigationService _navigationService = locator<NavigationService>();
  final SharedPreferences _prefsService = locator<SharedPreferences>();
  final LightService _lightService = locator<LightService>();

  @override
  Future futureToRun() async {
    final savedBridges = await _lightService.getSavedBridges();

    await Future.forEach(savedBridges, (BridgeModel bridge) async {
      if (bridge.state == Connection.connected) {
        final allGroups = await _lightService.getRooms(bridge);

        // check for possible new groups
        for (final group in allGroups) {
          final exists =
              bridge.groups.any((savedGroup) => savedGroup.id == group.id);
          if (!exists) {
            bridge.groups.add(group);
          }
        }
      }
    });

    return savedBridges
        .where((element) => element.state == Connection.connected)
        .toList();
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
