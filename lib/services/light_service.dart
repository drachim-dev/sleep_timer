import 'package:collection/collection.dart';
import 'package:flutter_hue/flutter_hue.dart';
import 'package:injectable/injectable.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:sleep_timer/app/locator.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/model/bridge_model.dart';
import 'package:sleep_timer/model/light_group.dart';

@lazySingleton
class LightService {
  final SharedPreferences _prefsService = locator<SharedPreferences>();

  Future<List<BridgeModel>> discoverBridges() async {
    await FlutterHueMaintenanceRepo.maintainBridges();

    final result = await BridgeDiscoveryRepo.discoverBridges();
    return result.map((ip) {
      return BridgeModel(id: ip, ip: ip);
    }).toList();
  }

  Future<List<BridgeModel>> getSavedBridges() async {
    final result = await BridgeDiscoveryRepo.fetchSavedBridges();
    return result.map((bridge) {
      final authKey = bridge.applicationKey;
      final state = authKey != null ? Connection.connected : Connection.failed;

      return BridgeModel(
          id: bridge.bridgeId,
          ip: bridge.ipAddress,
          auth: authKey,
          state: state);
    }).toList();
  }

  Future<bool> linkBridge(BridgeModel bridgeModel) async {
    final bridge = await BridgeDiscoveryRepo.firstContact(
      bridgeIpAddr: bridgeModel.ip,
    );

    final authKey = bridge?.applicationKey;
    final state = authKey != null ? Connection.connected : Connection.failed;

    bridgeModel
      ..auth = authKey
      ..state = state;

    return state == Connection.connected;
  }

  Future<List<LightGroup>> getRooms(BridgeModel bridgeModel) async {
    final result = await BridgeDiscoveryRepo.fetchSavedBridges();
    final bridge =
        result.firstWhereOrNull((bridge) => bridge.bridgeId == bridgeModel.id);

    if (bridge == null) {
      return List.empty();
    }

    final hueNetwork = HueNetwork(bridges: [bridge]);
    await hueNetwork.fetchAll();

    return hueNetwork.rooms
        .where((room) => room.type == ResourceType.room)
        .map((room) {
      final lights = room.childrenAsResources
          .where((child) => child.type == ResourceType.light)
          .toList();

      return LightGroup(
          id: room.id,
          className: room.type.name,
          name: room.metadata.name,
          numberOfLights: lights.length);
    }).toList();
  }

  Future<void> toggleLights(final bool enabled) async {
    final savedBridgesJson = _prefsService.getString(kPrefKeyHueBridges);

    /* if (savedBridgesJson != null) {
      final savedBridges = BridgeModel.decode(savedBridgesJson);

      for (var bridgeModel in savedBridges) {
        final connectionState = await getConnectionState(bridgeModel);

        if (connectionState == Connection.connected) {
          final bridge = Bridge(_client, bridgeModel.ip!, bridgeModel.auth!);
          final groups = await bridge.groups();

          for (var group in bridgeModel.groups) {
            if (group.actionEnabled!) {
              final foundGroup = groups
                  .singleWhere((element) => element.id.toString() == group.id);

              if (foundGroup.state?.anyOn ?? true) {
                foundGroup.groupLights
                    ?.where((light) => light.state?.on ?? true)
                    .forEach((shiningLight) {
                  bridge.updateLightState(shiningLight.rebuild((e) => e
                    ..state = lightStateForColorOnly(shiningLight)
                        .rebuild((s) => s..on = enabled)
                        .toBuilder()));
                });
              }
            }
          }
        }
      }
    } */
  }
}

/* LightState lightStateForColorOnly(Light light) {
  LightState state;
  if (light.state?.colorMode == 'xy') {
    state = LightState((b) => b..xy = light.state?.xy?.toBuilder());
  } else if (light.state?.colorMode == 'ct') {
    state = LightState((b) => b..ct = light.state?.ct);
  } else {
    state = LightState((b) => b
      ..hue = light.state?.hue
      ..saturation = light.state?.saturation
      ..brightness = light.state?.brightness);
  }
  return state;
} */
