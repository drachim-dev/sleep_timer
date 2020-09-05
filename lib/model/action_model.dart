import 'package:flutter/foundation.dart';

enum ActionType { MEDIA, WIFI, BLUETOOTH, SCREEN, VOLUME, LIGHT, APP }

class ActionModel {
  final ActionType id;
  final String title, description;
  final bool common;
  bool value;

  ActionModel(
      {@required this.id,
      @required this.title,
      @required this.description,
      this.common = true,
      @required this.value});
}
