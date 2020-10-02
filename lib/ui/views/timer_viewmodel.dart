import 'package:sleep_timer/app/locator.dart';
import 'package:sleep_timer/app/auto_router.gr.dart';
import 'package:sleep_timer/model/action_model.dart';
import 'package:sleep_timer/model/timer_model.dart';
import 'package:stacked/stacked.dart';
import 'package:stacked_services/stacked_services.dart';

class TimerViewModel extends BaseViewModel {
  final _navigationService = locator<NavigationService>();

  int _initialTime = 15;

  static List<ActionModel> _actions = [
    ActionModel(
      id: ActionType.MEDIA,
      title: "Media",
      description: "Stop media playback",
      value: false,
    ),
    ActionModel(
      id: ActionType.WIFI,
      title: "Wifi",
      description: "Turn off wifi",
      value: false,
    ),
    ActionModel(
      id: ActionType.BLUETOOTH,
      title: "Bluetooth",
      description: "Turn off bluetooth",
      value: false,
    ),
    ActionModel(
      id: ActionType.SCREEN,
      title: "Screen",
      description: "Turn off screen",
      value: false,
      common: false,
    ),
    ActionModel(
      id: ActionType.VOLUME,
      title: "Volume",
      description: "Set media volume to 10",
      value: true,
      common: false,
    ),
    ActionModel(
      id: ActionType.LIGHT,
      title: "Light",
      description: "Turn off 3 lights",
      value: false,
      common: false,
    ),
    ActionModel(
      id: ActionType.APP,
      title: "App",
      description: "Force close YouTube",
      value: false,
      common: false,
    ),
  ];

  int get initialTime => _initialTime;

  void navigateToTimerDetail() {
    final TimerModel timerModel = TimerModel(_initialTime * 60, _actions);
    _navigationService.navigateTo(Routes.timerDetailView,
        arguments: TimerDetailViewArguments(timer: timerModel));
  }

  void updateTime(int value) {
    setTime(value);
    notifyListeners();
  }

  void setTime(int value) => _initialTime = value;
}
