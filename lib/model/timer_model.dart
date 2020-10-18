import 'package:uuid/uuid.dart';

import 'action_model.dart';

class TimerModel {
  final String _id;
  final int _initialTimeInSeconds;
  final List<ActionModel> _actions;

  TimerModel(this._initialTimeInSeconds, this._actions)
      : this._id = Uuid().v4();

  String get id => _id;

  int get initialTimeInSeconds => _initialTimeInSeconds;

  List<ActionModel> get actions => _actions;
  ActionModel get mediaAction =>
      _actions.singleWhere((action) => action.id == ActionType.MEDIA);
  ActionModel get wifiAction =>
      _actions.singleWhere((action) => action.id == ActionType.WIFI);
  ActionModel get bluetoothAction =>
      _actions.singleWhere((action) => action.id == ActionType.BLUETOOTH);
  ActionModel get screenAction =>
      _actions.singleWhere((action) => action.id == ActionType.SCREEN);
  ActionModel get volumeAction =>
      _actions.singleWhere((action) => action.id == ActionType.VOLUME);
  ActionModel get lightAction =>
      _actions.singleWhere((action) => action.id == ActionType.LIGHT);
  ActionModel get appAction =>
      _actions.singleWhere((action) => action.id == ActionType.APP);
}
