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
      this.enabled = false});
}

class ValueActionModel extends ActionModel {
  double value;
  final String unit;
  final String key;

  @override
  String get description {
    return value == null ? title : '${value.toInt()}$unit';
  }

  ValueActionModel(
      {@required id,
      @required title,
      description,
      enabled = false,
      this.value,
      this.unit,
      this.key})
      : super(id: id, title: title, description: description, enabled: enabled);
}

List<ActionModel> startActionList = [
  ValueActionModel(
    id: ActionType.VOLUME,
    title: S.current.actionVolumeTitle,
    unit: ' %',
    key: kKeyVolumeLevel,
  ),
  ActionModel(
    id: ActionType.LIGHT,
    title: S.current.actionToggleLightTitle,
    description: S.current.actionToggleLightDescription,
  ),
  ActionModel(
    id: ActionType.PLAY_MUSIC,
    title: S.current.actionPlayMusicTitle,
    description: "Peacock's Frenchcore Choice",
  ),
  ActionModel(
    id: ActionType.DND,
    title: S.current.actionDoNotDisturbTitle,
    description: S.current.actionDoNotDisturbDescription,
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
  ),
  ActionModel(
    id: ActionType.BLUETOOTH,
    title: S.current.actionToggleBluetoothTitle,
    description: S.current.actionToggleBluetoothDescription,
  ),
  ActionModel(
    id: ActionType.SCREEN,
    title: S.current.actionToggleScreenTitle,
    description: S.current.actionToggleScreenDescription,
  ),
  ActionModel(
    id: ActionType.APP,
    title: 'App',
    description: 'Force close YouTube',
  ),
];
