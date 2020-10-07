import 'package:injectable/injectable.dart';
import 'package:observable_ish/observable_ish.dart';
import 'package:sleep_timer/common/theme.dart';
import 'package:stacked/stacked.dart';

@lazySingleton
class ThemeService with ReactiveServiceMixin {
  ThemeService() {
    listenToReactiveValues([_myTheme]);
  }

  final RxValue<MyTheme> _myTheme = RxValue<MyTheme>(initial: themeList[1]);
  MyTheme get myTheme => _myTheme.value;

  void updateTheme(final String value) {
    final MyTheme theme = themeList.firstWhere((theme) => theme.title == value,
        orElse: () => null);

    if (theme != null) _myTheme.value = theme;
  }
}
