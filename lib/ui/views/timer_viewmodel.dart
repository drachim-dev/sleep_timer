import 'package:shared_preferences/shared_preferences.dart';
import 'package:sleep_timer/app/locator.dart';
import 'package:sleep_timer/app/auto_router.gr.dart';
import 'package:sleep_timer/model/action_model.dart';
import 'package:sleep_timer/model/timer_model.dart';
import 'package:stacked/stacked.dart';
import 'package:stacked_services/stacked_services.dart';

class TimerViewModel extends BaseViewModel {
  final _navigationService = locator<NavigationService>();
  final _prefService = locator<SharedPreferences>();

  List<ActionModel> _actions;
  int _initialTime = 15;
  int get initialTime => _initialTime;

  TimerViewModel() {
    _initActions();
  }

  void _initActions() {
    final bool mediaAction = _prefService.get(ActionType.MEDIA.toString());
    final bool wifiAction = _prefService.get(ActionType.WIFI.toString());
    final bool bluetoothAction =
    _prefService.get(ActionType.BLUETOOTH.toString());
    final bool screenAction = _prefService.get(ActionType.SCREEN.toString());
    final bool volumeAction = _prefService.get(ActionType.VOLUME.toString());
    final bool lightAction = _prefService.get(ActionType.LIGHT.toString());
    final bool actionAction = _prefService.get(ActionType.APP.toString());

    _actions = [
      ActionModel(
        id: ActionType.MEDIA,
        title: "Media",
        description: "Stop media playback",
        value: mediaAction ?? true,
      ),
      ActionModel(
        id: ActionType.WIFI,
        title: "Wifi",
        description: "Turn off wifi",
        value: wifiAction ?? false,
      ),
      ActionModel(
        id: ActionType.BLUETOOTH,
        title: "Bluetooth",
        description: "Turn off bluetooth",
        value: bluetoothAction ?? true,
      ),
      ActionModel(
        id: ActionType.SCREEN,
        title: "Screen",
        description: "Turn off screen",
        value: screenAction ?? false,
        common: false,
      ),
      ActionModel(
        id: ActionType.VOLUME,
        title: "Volume",
        description: "Set media volume to 10",
        value: volumeAction ?? false,
        common: false,
      ),
      ActionModel(
        id: ActionType.LIGHT,
        title: "Light",
        description: "Turn off 3 lights",
        value: lightAction ?? false,
        common: false,
      ),
      ActionModel(
        id: ActionType.APP,
        title: "App",
        description: "Force close YouTube",
        value: actionAction ?? false,
        common: false,
      ),
    ];
  }

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
