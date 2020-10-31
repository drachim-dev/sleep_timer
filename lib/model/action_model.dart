import 'package:flutter/foundation.dart';
import 'package:sleep_timer/common/constants.dart';

enum ActionType { MEDIA, WIFI, BLUETOOTH, SCREEN, VOLUME, DND, LIGHT, APP }

class ActionModel {
  final ActionType id;
  final String title, description;
  final bool experiment;
  bool enabled;

  ActionModel(
      {@required this.id,
      @required this.title,
      @required this.description,
      this.experiment = false,
      @required this.enabled});
}

class ValueActionModel extends ActionModel {
  double value;
  String key;

  String get description => "${super.description} ${value.toInt()}";

  ValueActionModel(
      {id, title, description, experiment, enabled, @required this.value, this.key})
      : super(
            id: id,
            title: title,
            description: description,
            experiment: experiment,
            enabled: enabled);
}

List<ActionModel> actionList = [
  ActionModel(
    id: ActionType.MEDIA,
    title: "Media",
    description: "Stop media playback",
    enabled: false,
  ),
  ActionModel(
    id: ActionType.WIFI,
    title: "Wifi",
    description: "Disable wifi",
    enabled: false,
  ),
  ActionModel(
    id: ActionType.BLUETOOTH,
    title: "Bluetooth",
    description: "Disable bluetooth",
    enabled: false,
  ),
  ActionModel(
    id: ActionType.SCREEN,
    title: "Screen",
    description: "Turn screen off",
    enabled: false,
    experiment: true,
  ),
  ValueActionModel(
    id: ActionType.VOLUME,
    title: "Volume",
    description: "Set media volume to ",
    enabled: false,
    experiment: false,
    value: 14.0,
    key: kKeyVolumeLevel,
  ),
  ActionModel(
    id: ActionType.DND,
    title: "Do not disturb",
    description: "Enable do not disturb",
    enabled: false,
    experiment: true,
  ),
  ActionModel(
    id: ActionType.LIGHT,
    title: "Light",
    description: "Turn 3 lights off",
    enabled: false,
    experiment: true,
  ),
  ActionModel(
    id: ActionType.APP,
    title: "App",
    description: "Force close YouTube",
    enabled: false,
    experiment: true,
  ),
];