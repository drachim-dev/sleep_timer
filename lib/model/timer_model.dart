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
  ValueActionModel get volumeStartAction =>
      _startActions.singleWhere((action) => action.id == ActionType.volume)
          as ValueActionModel;
  ActionModel get lightStartAction =>
      _startActions.singleWhere((action) => action.id == ActionType.light);
  ActionModel get playMusicStartAction =>
      _startActions.singleWhere((action) => action.id == ActionType.playMusic);
  ActionModel get doNotDisturbStartAction =>
      _startActions.singleWhere((action) => action.id == ActionType.dnd);

  List<ActionModel> get endActions => _endActions;
  ActionModel get mediaAction =>
      _endActions.singleWhere((action) => action.id == ActionType.media);
  ValueActionModel get volumeAction =>
      _endActions.singleWhere((action) => action.id == ActionType.volume)
          as ValueActionModel;
  ActionModel get wifiAction =>
      _endActions.singleWhere((action) => action.id == ActionType.wifi);
  ActionModel get bluetoothAction =>
      _endActions.singleWhere((action) => action.id == ActionType.bluetooth);
  ActionModel get screenAction =>
      _endActions.singleWhere((action) => action.id == ActionType.screen);

  ActionModel get appAction =>
      _endActions.singleWhere((action) => action.id == ActionType.app);
}
