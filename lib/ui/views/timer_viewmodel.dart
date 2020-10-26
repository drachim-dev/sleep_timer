import 'package:shared_preferences/shared_preferences.dart';
import 'package:sleep_timer/app/auto_router.gr.dart';
import 'package:sleep_timer/app/locator.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/common/timer_service_manager.dart';
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

  String _activeTimerId;
  bool get hasActiveTimer => _activeTimerId != null;

  TimerViewModel() {
    _initialTime = _prefService.getInt(kPrefKeyInitialTime) ?? _initialTime;
    _initActions();
  }

  void _initActions() {
    final bool mediaAction = _prefService.get(ActionType.MEDIA.toString());
    final bool wifiAction = _prefService.get(ActionType.WIFI.toString());
    final bool bluetoothAction =
        _prefService.get(ActionType.BLUETOOTH.toString());
    final bool screenAction = _prefService.get(ActionType.SCREEN.toString());
    final bool volumeAction = _prefService.get(ActionType.VOLUME.toString());
    final double volumeLevel = _prefService.get(kKeyVolumeLevel);
    final bool dndAction = _prefService.get(ActionType.DND.toString());
    final bool lightAction = _prefService.get(ActionType.LIGHT.toString());
    final bool actionAction = _prefService.get(ActionType.APP.toString());

    _actions = [
      ActionModel(
        id: ActionType.MEDIA,
        title: "Media",
        description: "Stop media playback",
        enabled: mediaAction ?? true,
      ),
      ActionModel(
        id: ActionType.WIFI,
        title: "Wifi",
        description: "Disable wifi",
        enabled: wifiAction ?? false,
      ),
      ActionModel(
        id: ActionType.BLUETOOTH,
        title: "Bluetooth",
        description: "Disable bluetooth",
        enabled: bluetoothAction ?? true,
      ),
      ActionModel(
        id: ActionType.SCREEN,
        title: "Screen",
        description: "Turn screen off",
        enabled: screenAction ?? false,
        common: false,
      ),
      ValueActionModel(
        id: ActionType.VOLUME,
        title: "Volume",
        description: "Set media volume to ",
        enabled: volumeAction ?? false,
        common: false,
        value: volumeLevel ?? 5.0,
        key: kKeyVolumeLevel,
      ),
      ActionModel(
        id: ActionType.DND,
        title: "Do not disturb",
        description: "Enable do not disturb",
        enabled: dndAction ?? false,
        common: true,
      ),
      ActionModel(
        id: ActionType.LIGHT,
        title: "Light",
        description: "Turn 3 lights off",
        enabled: lightAction ?? false,
        common: false,
      ),
      ActionModel(
        id: ActionType.APP,
        title: "App",
        description: "Force close YouTube",
        enabled: actionAction ?? false,
        common: false,
      ),
    ];
  }

  void startNewTimer() async {
    final TimerModel timerModel = TimerModel(_initialTime * 60, _actions);

    _activeTimerId = await _navigationService.navigateTo(Routes.timerDetailView,
        arguments: TimerDetailViewArguments(timerModel: timerModel));
    notifyListeners();
  }

  void openActiveTimer() async {
    if (hasActiveTimer) {
      final TimerModel timerModel = TimerServiceManager.getInstance()
          .getTimerService(_activeTimerId)
          .timerModel;

      _activeTimerId = await _navigationService.navigateTo(
          Routes.timerDetailView,
          arguments: TimerDetailViewArguments(timerModel: timerModel));
      notifyListeners();
    }
  }

  void updateTime(int value) {
    setTime(value);
    notifyListeners();
  }

  void setTime(int value) {
    _initialTime = value;
    _prefService.setInt(kPrefKeyInitialTime, value);
  }
}
