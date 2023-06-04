import 'package:permission_handler/permission_handler.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/generated/l10n.dart';

enum ActionType {
  playMusic,
  media,
  wifi,
  bluetooth,
  screen,
  volume,
  dnd,
  light,
  app
}

class ActionModel {
  final ActionType id;
  final String title;
  final String? description;
  final Permission? permission;
  bool enabled;

  ActionModel(
      {required this.id,
      required this.title,
      required this.description,
      this.permission,
      this.enabled = false});
}

class ValueActionModel extends ActionModel {
  double? value;
  final String? unit;
  final String? key;

  String get label {
    return value == null ? title : '${value!.round()} $unit';
  }

  @override
  String get description {
    return value == null ? title : S.current.setToValue(title, '${value!.round()}$unit');
  }

  ValueActionModel(
      {required id,
      required title,
      description,
      enabled = false,
      this.value,
      this.unit,
      this.key})
      : super(id: id, title: title, description: description, enabled: enabled);
}

List<ActionModel> startActionList = [
  ValueActionModel(
    id: ActionType.volume,
    title: S.current.actionVolumeTitle,
    unit: '%',
    key: kKeyVolumeStartLevel,
  ),
  ActionModel(
    id: ActionType.light,
    title: S.current.actionToggleLightTitle,
    description: S.current.actionToggleLightDescription,
  ),
  ActionModel(
    id: ActionType.dnd,
    title: S.current.actionDoNotDisturbTitle,
    description: S.current.actionDoNotDisturbDescription,
  ),
];

List<ActionModel> endActionList = [
  ActionModel(
    id: ActionType.media,
    title: S.current.actionToggleMediaTitle,
    description: S.current.actionToggleMediaDescription,
    enabled: true,
  ),
  ValueActionModel(
    id: ActionType.volume,
    title: S.current.actionVolumeTitle,
    unit: ' %',
    key: kKeyVolumeEndLevel,
  ),
  ActionModel(
    id: ActionType.wifi,
    title: S.current.actionToggleWifiTitle,
    description: S.current.actionToggleWifiDescription,
  ),
  ActionModel(
      id: ActionType.bluetooth,
      title: S.current.actionToggleBluetoothTitle,
      description: S.current.actionToggleBluetoothDescription,
      permission: Permission.bluetoothConnect),
  ActionModel(
    id: ActionType.screen,
    title: S.current.actionToggleScreenTitle,
    description: S.current.actionToggleScreenDescription,
  ),
];
