import 'package:flutter/foundation.dart';

enum ActionType { MEDIA, WIFI, BLUETOOTH, SCREEN, VOLUME, DND, LIGHT, APP }

class ActionModel {
  final ActionType id;
  final String title, description;
  final bool common;
  bool enabled;

  ActionModel(
      {@required this.id,
      @required this.title,
      @required this.description,
      this.common = true,
      @required this.enabled});
}

class ValueActionModel extends ActionModel {
  double value;
  String key;

  String get description => "${super.description} ${value.toInt()}";

  ValueActionModel(
      {id, title, description, common, enabled, @required this.value, this.key})
      : super(
            id: id,
            title: title,
            description: description,
            common: common,
            enabled: enabled);
}
