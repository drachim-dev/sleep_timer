import 'package:flutter/foundation.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/generated/l10n.dart';

enum ActionType {
  PLAY_MUSIC,
  MEDIA,
  WIFI,
  BLUETOOTH,
  SCREEN,
  VOLUME,
  DND,
  LIGHT,
  APP
}

class ActionModel {
  final ActionType id;
  final String title, description;
  bool enabled;

  ActionModel(
      {@required this.id,
      @required this.title,
      @required this.description,
      @required this.enabled});
}

class ValueActionModel extends ActionModel {
  double value;
  final String unit;
  final String key;

  @override
  String get description => '${super.description} ${value.toInt()}$unit';

  ValueActionModel(
      {id,
      title,
      description,
      enabled,
      @required this.value,
      this.unit,
      this.key})
      : super(id: id, title: title, description: description, enabled: enabled);
}

List<ActionModel> startActionList = [
  ValueActionModel(
    id: ActionType.VOLUME,
    title: S.current.actionVolumeTitle,
    description: S.current.actionVolumeDescription,
    unit: ' %',
    enabled: false,
    value: 10.0,
    key: kKeyVolumeLevel,
  ),
  ActionModel(
    id: ActionType.PLAY_MUSIC,
    title: S.current.actionPlayMusicTitle,
    description: "Peacock's Frenchcore Choice",
    enabled: false,
  ),
  ActionModel(
    id: ActionType.DND,
    title: S.current.actionDoNotDisturbTitle,
    description: S.current.actionDoNotDisturbDescription,
    enabled: false,
  ),
];

List<ActionModel> actionList = [
  ActionModel(
    id: ActionType.MEDIA,
    title: S.current.actionToggleMediaTitle,
    description: S.current.actionToggleMediaDescription,
    enabled: true,
  ),
  ActionModel(
    id: ActionType.WIFI,
    title: S.current.actionToggleWifiTitle,
    description: S.current.actionToggleWifiDescription,
    enabled: false,
  ),
  ActionModel(
    id: ActionType.BLUETOOTH,
    title: S.current.actionToggleBluetoothTitle,
    description: S.current.actionToggleBluetoothDescription,
    enabled: false,
  ),
  ActionModel(
    id: ActionType.SCREEN,
    title: S.current.actionToggleScreenTitle,
    description: S.current.actionToggleScreenDescription,
    enabled: false,
  ),
  ActionModel(
    id: ActionType.LIGHT,
    title: 'Light',
    description: 'Turn 3 lights off',
    enabled: false,
  ),
  ActionModel(
    id: ActionType.APP,
    title: 'App',
    description: 'Force close YouTube',
    enabled: false,
  ),
];
