import 'dart:math';

import 'package:sleep_timer/common/utils.dart';

import 'action_model.dart';

class TimerModel {
  final String _id;
  final int _initialTimeInSeconds;
  final List<ActionModel> _startActions;
  final List<ActionModel> _endActions;

  TimerModel(this._initialTimeInSeconds, this._startActions, this._endActions)
      : _id = Utils.random.nextInt(pow(2, 31) as int).toString();

  String get id => _id;

  int get initialTimeInSeconds => _initialTimeInSeconds;

  List<ActionModel> get startActions => _startActions;
  ValueActionModel get volumeAction =>
      _startActions.singleWhere((action) => action.id == ActionType.VOLUME) as ValueActionModel;
  ActionModel get lightAction =>
      _startActions.singleWhere((action) => action.id == ActionType.LIGHT);
  ActionModel get playMusicAction =>
      _startActions.singleWhere((action) => action.id == ActionType.PLAY_MUSIC);
  ActionModel get doNotDisturbAction =>
      _startActions.singleWhere((action) => action.id == ActionType.DND);

  List<ActionModel> get endActions => _endActions;
  ActionModel get mediaAction =>
      _endActions.singleWhere((action) => action.id == ActionType.MEDIA);
  ActionModel get wifiAction =>
      _endActions.singleWhere((action) => action.id == ActionType.WIFI);
  ActionModel get bluetoothAction =>
      _endActions.singleWhere((action) => action.id == ActionType.BLUETOOTH);
  ActionModel get screenAction =>
      _endActions.singleWhere((action) => action.id == ActionType.SCREEN);

  ActionModel get appAction =>
      _endActions.singleWhere((action) => action.id == ActionType.APP);
}
