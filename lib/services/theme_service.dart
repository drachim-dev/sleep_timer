import 'package:collection/collection.dart' show IterableExtension;
import 'package:injectable/injectable.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/common/theme.dart';
import 'package:stacked/stacked.dart';

@lazySingleton
class ThemeService with ReactiveServiceMixin {
  ThemeService() {
    listenToReactiveValues([_myTheme, _showGlow]);
  }

  MyTheme _myTheme = themeList.firstWhere((e) => e.id == kThemeKeyDarkOrange);
  MyTheme get myTheme => _myTheme;

  bool _showGlow = kDefaultGlow;
  bool get showGlow => _showGlow;

  void setShowGlow(final bool value) {
    _showGlow = value;
    notifyListeners();
  }

  void setTheme(final String? value) {
    final result =
        themeList.firstWhereOrNull((theme) => theme.id == value);

    if (result != null) {
      _myTheme = result;
      notifyListeners();
    }
  }
}
