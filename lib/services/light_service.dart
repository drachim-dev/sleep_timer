import 'package:collection/collection.dart';
import 'package:flutter_hue/flutter_hue.dart';
import 'package:injectable/injectable.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:sleep_timer/app/locator.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/model/bridge_model.dart';
import 'package:sleep_timer/model/light_group.dart';

extension BridgeModelMapping on BridgeModel {
  Future<Bridge?> toBridge() async {
    final result = await BridgeDiscoveryRepo.fetchSavedBridges();
    return result.firstWhereOrNull((bridge) => bridge.bridgeId == id);
  }
}

@lazySingleton
class LightService {
  final SharedPreferences _prefsService = locator<SharedPreferences>();

  Future<List<String>> discoverBridges() async {
    await FlutterHueMaintenanceRepo.maintainBridges();
    return await BridgeDiscoveryRepo.discoverBridges();
  }

  Future<List<BridgeModel>> getSavedBridges() async {
    final result = await BridgeDiscoveryRepo.fetchSavedBridges();
    return result.map((bridge) {
      final authKey = bridge.applicationKey;
      final state = authKey != null ? Connection.connected : Connection.failed;

      List<LightGroup>? groups;
      final savedBridgesJson = _prefsService.getString(kPrefKeyHueBridges);
      if (savedBridgesJson != null) {
        final savedBridges = BridgeModel.decode(savedBridgesJson);
        final savedBridge =
            savedBridges.firstWhereOrNull((b) => b.id == bridge.bridgeId);

        groups = savedBridge?.groups;
      }

      return BridgeModel(
          id: bridge.bridgeId,
          ip: bridge.ipAddress,
          auth: authKey,
          state: state,
          groups: groups);
    }).toList();
  }

  Future<Connection> linkBridge(BridgeModel bridgeModel) async {
    final bridge = await BridgeDiscoveryRepo.firstContact(
      bridgeIpAddr: bridgeModel.ip,
    );

    final authKey = bridge?.applicationKey;
    final state = authKey != null ? Connection.connected : Connection.failed;

    bridgeModel
      ..auth = authKey
      ..state = state;

    return state;
  }

  Future<Connection> getConnectionState(BridgeModel bridgeModel) async {
    final bridge = await bridgeModel.toBridge();
    if (bridge == null) {
      return Connection.unsaved;
    }

    final authKey = bridge.applicationKey;
    return authKey != null ? Connection.connected : Connection.failed;
  }

  Future<List<LightGroup>> getRooms(BridgeModel bridgeModel) async {
    final bridge = await bridgeModel.toBridge();

    if (bridge == null) {
      return List.empty();
    }

    final hueNetwork = HueNetwork(bridges: [bridge]);
    await hueNetwork.fetchAll();

    return hueNetwork.rooms
        .where((room) => room.type == ResourceType.room)
        .map((room) {
      final lights = room.childrenAsResources
          .where((child) =>
              child is Device &&
              child.services
                  .any((service) => service.type == ResourceType.light))
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

    if (savedBridgesJson != null) {
      final savedBridges = BridgeModel.decode(savedBridgesJson);

      for (var bridgeModel in savedBridges) {
        if (bridgeModel.state == Connection.connected) {
          final bridge = await bridgeModel.toBridge();
          if (bridge == null) return;

          final hueNetwork = HueNetwork(bridges: [bridge]);
          await hueNetwork.fetchAll();

          final groupsToTurnOff = hueNetwork.groupedLights.where((remoteGroup) {
            final owner = remoteGroup.ownerAsResource;
            return owner is Room &&
                remoteGroup.on.isOn &&
                bridgeModel.groups.any((localGroup) =>
                    localGroup.actionEnabled == true &&
                    localGroup.id == owner.id);
          }).toList();

          for (final foundGroup in groupsToTurnOff) {
            final updatedLightGroup =
                foundGroup.copyWith(on: LightOn(isOn: false));
            await bridge.put(updatedLightGroup);
          }
        }
      }
    }
  }
}
