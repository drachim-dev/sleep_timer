import 'package:shared_preferences/shared_preferences.dart';
import 'package:sleep_timer/app/locator.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/services/theme_service.dart';
import 'package:stacked/stacked.dart';

class SettingsViewModel extends ReactiveViewModel {
  final _prefsService = locator<SharedPreferences>();
  final _themeService = locator<ThemeService>();

  String _currentTheme = 'Dark';
  String get currentTheme =>
      _prefsService.getString(kPrefKeyTheme) ?? _currentTheme;

  @override
  List<ReactiveServiceMixin> get reactiveServices => [_themeService];

  void updateTheme(String theme) {
    _prefsService.setString(kPrefKeyTheme, theme);
    _themeService.updateTheme(theme);

    notifyListeners();
  }
}
