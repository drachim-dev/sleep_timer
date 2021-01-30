import 'package:http/http.dart';
import 'package:hue_dart/hue_dart.dart';
import 'package:injectable/injectable.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:sleep_timer/app/locator.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/model/bridge_model.dart';
import 'package:sleep_timer/model/light_group.dart';
import 'package:sleep_timer/services/device_service.dart';

@lazySingleton
class LightService {
  final _prefsService = locator<SharedPreferences>();
  final _deviceService = locator<DeviceService>();

  final Client _client = Client();

  Future<List<BridgeModel>> findBridges() async {
    final discovery = BridgeDiscovery(_client);

    try {
      final results = await discovery.automatic();
      return results.map((e) {
        return BridgeModel(id: e.id, ip: e.ipAddress, mac: e.mac, name: e.name);
      }).toList();
    } on BridgeException {
      rethrow;
    }
  }

  Future<bool> linkBridge(BridgeModel bridgeModel) async {
    final bridge = Bridge(_client, bridgeModel.ip);

    final auth = await _createUser(bridge);
    if (auth != null) {
      bridgeModel
        ..auth = auth
        ..state = Connection.connected;
      final bridges = <BridgeModel>[bridgeModel];
      final json = BridgeModel.encode(bridges);
      await _prefsService.setString(kPrefKeyHueBridges, json);
    }

    return false;
  }

  Future<Connection> getConnectionState(BridgeModel bridgeModel) async {
    if (bridgeModel.auth == null) {
      return Connection.unsaved;
    }

    final bridge = Bridge(_client, bridgeModel.ip, bridgeModel.auth);
    var config = await bridge.configuration();

    if (config == null || config.whitelist == null) {
      return Connection.failed;
    }

    return Connection.connected;
  }

  Future<String> _createUser(Bridge bridge) async {
    final username =
        '$kHueBridgeUsername#${_deviceService.deviceManufacturer} ${_deviceService.deviceModel}';
    try {
      final whiteListItem = await bridge.createUser(username);
      return whiteListItem.username;
    } on BridgeException {
      rethrow;
    }
  }

  Future<List<LightGroup>> getRooms(BridgeModel bridgeModel) async {
    final bridge = Bridge(_client, bridgeModel.ip, bridgeModel.auth);
    final groups = await bridge.groups();
    return groups
        .where((e) => e.type?.toLowerCase() != 'entertainment')
        .map((e) => LightGroup(
            id: e.id.toString(),
            className: e.className,
            name: e.name,
            numberOfLights: e.lightIds.length))
        .toList();
  }

  Future<void> toggleLights(final bool enabled) async {
    final savedBridgesJson = _prefsService.getString(kPrefKeyHueBridges);

    if (savedBridgesJson != null) {
      final savedBridges = BridgeModel.decode(savedBridgesJson);

      savedBridges.forEach((bridgeModel) async {
        final connectionState = await getConnectionState(bridgeModel);

        if (connectionState == Connection.connected) {
          final bridge = Bridge(_client, bridgeModel.ip, bridgeModel.auth);
          final groups = await bridge.groups();

          bridgeModel.groups.forEach((group) {
            if (group.actionEnabled) {
              final foundGroup = groups
                  .singleWhere((element) => element.id.toString() == group.id);
              if (foundGroup != null && foundGroup.state.anyOn) {
                foundGroup.groupLights
                    .where((light) => light.state.on)
                    .forEach((shiningLight) {
                  bridge.updateLightState(shiningLight.rebuild((e) => e
                    ..state = lightStateForColorOnly(shiningLight)
                        .rebuild((s) => s..on = enabled)
                        .toBuilder()));
                });
              }
            }
          });
        }
      });
    }
  }
}
