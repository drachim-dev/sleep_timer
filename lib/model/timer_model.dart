import 'dart:math';

import 'package:sleep_timer/common/utils.dart';

import 'action_model.dart';

class TimerModel {
  final String _id;
  final int _initialTimeInSeconds;
  final List<ActionModel> _startActions;
  final List<ActionModel> _actions;

  TimerModel(this._initialTimeInSeconds, this._startActions, this._actions)
      : this._id = Utils.random.nextInt(pow(2, 31)).toString();

  String get id => _id;

  int get initialTimeInSeconds => _initialTimeInSeconds;

  List<ActionModel> get startActions => _startActions;
  ValueActionModel get volumeAction =>
      _startActions.singleWhere((action) => action.id == ActionType.VOLUME);
  ActionModel get playMusicAction =>
      _startActions.singleWhere((action) => action.id == ActionType.PLAY_MUSIC);
  ActionModel get doNotDisturbAction =>
      _startActions.singleWhere((action) => action.id == ActionType.DND);

  List<ActionModel> get actions => _actions;
  ActionModel get mediaAction =>
      _actions.singleWhere((action) => action.id == ActionType.MEDIA);
  ActionModel get wifiAction =>
      _actions.singleWhere((action) => action.id == ActionType.WIFI);
  ActionModel get bluetoothAction =>
      _actions.singleWhere((action) => action.id == ActionType.BLUETOOTH);
  ActionModel get screenAction =>
      _actions.singleWhere((action) => action.id == ActionType.SCREEN);

  ActionModel get lightAction =>
      _actions.singleWhere((action) => action.id == ActionType.LIGHT);
  ActionModel get appAction =>
      _actions.singleWhere((action) => action.id == ActionType.APP);
}
