import 'package:injectable/injectable.dart';
import 'package:observable_ish/observable_ish.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/common/theme.dart';
import 'package:stacked/stacked.dart';

@lazySingleton
class ThemeService with ReactiveServiceMixin {
  ThemeService() {
    listenToReactiveValues([_myTheme, _showGlow]);
  }

  final RxValue<MyTheme> _myTheme = RxValue<MyTheme>(
      initial: themeList.firstWhere((e) => e.id == kThemeKeyDarkOrange));
  MyTheme get myTheme => _myTheme.value;

  final RxValue<bool> _showGlow = RxValue<bool>(initial: kDefaultGlow);
  bool get showGlow => _showGlow.value;
  set showGlow(final bool value) => _showGlow.value = value;

  void updateTheme(final String value) {
    final theme =
        themeList.firstWhere((theme) => theme.id == value, orElse: () => null);

    if (theme != null) _myTheme.value = theme;
  }
}
