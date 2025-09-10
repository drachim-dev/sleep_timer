import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/common/utils.dart';

class MyTheme {
  final String id, title;
  final ThemeData theme;

  const MyTheme({required this.id, required this.title, required this.theme});
}

const _lightFillColor = Colors.black;
const _darkFillColor = Colors.white;

final Color _lightFocusColor = Colors.black.withValues(alpha: 0.12);
final Color _darkFocusColor = Colors.white.withValues(alpha: 0.12);

const TextTheme _textTheme = TextTheme(
  displayMedium: TextStyle(fontWeight: FontWeight.w300),
  titleMedium: TextStyle(fontWeight: FontWeight.w500, fontSize: 16.0),
);

List<MyTheme> themeList = [
  MyTheme(
    id: kThemeKeyLightOrange,
    title: 'Light Orange',
    theme: _themeData(_lightOrangeColorScheme, _lightFocusColor),
  ),
  MyTheme(
    id: kThemeKeyLightGreen,
    title: 'Light Green',
    theme: _themeData(_lightGreenColorScheme, _lightFocusColor),
  ),
  MyTheme(
    id: kThemeKeyDarkOrange,
    title: 'Dark Orange',
    theme: _themeData(_darkOrangeColorScheme, _darkFocusColor),
  ),
  MyTheme(
    id: kThemeKeyDarkYellow,
    title: 'Dark Yellow',
    theme: _themeData(_darkYellowColorScheme, _darkFocusColor),
  ),
  MyTheme(
    id: kThemeKeyBlackBlue,
    title: 'Black',
    theme: _themeData(_blackColorScheme, _darkFocusColor),
  ),
];

ThemeData _themeData(ColorScheme colorScheme, Color focusColor) {
  final isLight = colorScheme.brightness == Brightness.light;

  return ThemeData(
    pageTransitionsTheme: PageTransitionsTheme(
      builders: {
        TargetPlatform.android: PredictiveBackPageTransitionsBuilder(),
      },
    ),
    colorScheme: colorScheme,
    textTheme: _textTheme,
    useMaterial3: true,
    appBarTheme: AppBarTheme(
      // textTheme: _textTheme.apply(bodyColor: colorScheme.onBackground),
      backgroundColor: Colors.transparent,
      elevation: 0,
      iconTheme: IconThemeData(color: colorScheme.primary),
      systemOverlayStyle: isLight
          ? SystemUiOverlayStyle.dark.copyWith(
              statusBarColor: Colors.transparent,
            )
          : SystemUiOverlayStyle.light.copyWith(
              statusBarColor: Colors.transparent,
            ),
    ),
    iconTheme: IconThemeData(color: colorScheme.onPrimary),
    bottomNavigationBarTheme: BottomNavigationBarThemeData(
      selectedIconTheme: IconThemeData(color: colorScheme.primary),
      backgroundColor: colorScheme.surfaceContainer,
    ),
    floatingActionButtonTheme: FloatingActionButtonThemeData(
      foregroundColor: Colors.white,
      shape: StadiumBorder(),
      backgroundColor: colorScheme.secondary,
    ),
    outlinedButtonTheme: OutlinedButtonThemeData(
      style: OutlinedButton.styleFrom(
        foregroundColor: colorScheme.onSurface,
        side: BorderSide(color: colorScheme.onSurface.withValues(alpha: 0.6)),
      ),
    ),
    canvasColor: colorScheme.surface,
    scaffoldBackgroundColor: colorScheme.surface,
    highlightColor: Colors.transparent,
    textSelectionTheme: TextSelectionThemeData(
      selectionColor: colorScheme.primary,
      selectionHandleColor: colorScheme.primary,
      cursorColor: colorScheme.primary,
    ),
    dividerTheme: DividerThemeData(
      color: colorScheme.onSurface.withValues(alpha: 0.4),
    ),
    dialogTheme: DialogThemeData(backgroundColor: colorScheme.surfaceContainer),
    cardColor: colorScheme.surfaceContainer,
    cardTheme: CardThemeData(color: colorScheme.surfaceContainer),
    popupMenuTheme: PopupMenuThemeData(color: colorScheme.surfaceContainer),
    bottomSheetTheme: BottomSheetThemeData(
      backgroundColor: colorScheme.surfaceContainer,
      modalBackgroundColor: colorScheme.surfaceContainer,
    ),
    focusColor: focusColor,
    snackBarTheme: SnackBarThemeData(
      behavior: SnackBarBehavior.floating,
      backgroundColor: Color.alphaBlend(
        _lightFillColor.withValues(alpha: 0.8),
        _darkFillColor,
      ),
      contentTextStyle: _textTheme.titleMedium!.apply(color: _darkFillColor),
    ),
    switchTheme: ThemeData(
      colorSchemeSeed: colorScheme.primary,
      brightness: colorScheme.brightness,
      useMaterial3: true,
    ).switchTheme,
    toggleButtonsTheme: ToggleButtonsThemeData(
      constraints: BoxConstraints(minWidth: 80),
      selectedColor: colorScheme.primary,
      disabledColor: colorScheme.onSurface.withValues(alpha: 0.38),
      fillColor: colorScheme.primary.withValues(alpha: 0.18),
    ),
  );
}

ColorScheme _lightOrangeColorScheme = ColorScheme(
  brightness: Brightness.light,
  primary: Colors.amber[700]!,
  error: _lightFillColor,
  onError: _lightFillColor,
  onPrimary: _lightFillColor,
  primaryContainer: Colors.amber[100],
  onPrimaryContainer: Color(0xFF2D1600),
  secondary: Colors.orangeAccent,
  onSecondary: Color(0xFFFFFFFF),
  secondaryContainer: Colors.orangeAccent[100],
  onSecondaryContainer: Color(0xFF2C1600),
  surface: Color(0xFFFCFCFC),
  surfaceContainer: Color(0xFFEFEFEF),
  onSurface: Color(0xFF2D1600),
  surfaceContainerHighest: Color(0xFFF2DFD1),
  onSurfaceVariant: Color(0xFF51443A),
  outline: Color(0xFF837469),
  surfaceTint: Color(0xFF8C5000),
  outlineVariant: Color(0xFFD5C3B6),
  scrim: Color(0xFF000000),
);

ColorScheme _lightGreenColorScheme = ColorScheme(
  brightness: Brightness.light,
  primary: Color(0xFF90AA3C),
  error: _lightFillColor,
  onError: _lightFillColor,
  onPrimary: _lightFillColor,
  primaryContainer: Color(0xFFC9DC8B),
  onPrimaryContainer: Color(0xFF2D1600),
  secondary: Color(0xFF77B25A),
  onSecondary: Color(0xFFFFFFFF),
  secondaryContainer: Color(0xFFB6DDA4),
  onSecondaryContainer: Color(0xFF2C1600),
  surface: Color(0xFFFCFCFC),
  surfaceContainer: Color(0xFFEFEFEF),
  onSurface: Color(0xFF2D1600),
  surfaceContainerHighest: Color(0xFFE3E4D3),
  onSurfaceVariant: Color(0xFF46483C),
  outline: Color(0xFF76786B),
  shadow: Color(0xFF000000),
  surfaceTint: Color(0xFF516600),
  outlineVariant: Color(0xFFC7C8B8),
  scrim: Color(0xFF000000),
);

ColorScheme _darkOrangeColorScheme = ColorScheme(
  primary: Colors.deepOrange[300]!,
  primaryContainer: Colors.deepOrange[300]!.darken(),
  secondary: Colors.deepPurpleAccent,
  secondaryContainer: Colors.deepPurple,
  surface: Color(0xFF1F1929),
  surfaceContainer: Color(0xFF393343), // 241E30
  error: _darkFillColor,
  onError: _darkFillColor,
  onPrimary: _darkFillColor,
  onSecondary: _darkFillColor,
  onSurface: _darkFillColor,
  brightness: Brightness.dark,
  surfaceContainerHighest: Color(0xFF504353),
);

ColorScheme _darkYellowColorScheme = ColorScheme(
  primary: Colors.yellow[800]!,
  primaryContainer: Colors.yellow[800]!.darken(),
  secondary: Colors.teal[700]!,
  secondaryContainer: Colors.teal[800]!,
  surface: Color(0xFF1b1b1b),
  surfaceContainer: Color(0xFF373737),
  error: _darkFillColor,
  onError: _darkFillColor,
  onPrimary: _darkFillColor,
  onSecondary: _darkFillColor,
  onSurface: _darkFillColor,
  brightness: Brightness.dark,
  surfaceContainerHighest: Color(0xFF505047),
);

ColorScheme _blackColorScheme = ColorScheme(
  primary: Colors.indigo[200]!,
  primaryContainer: Colors.indigoAccent[100]!.darken(),
  secondary: Colors.indigo[500]!,
  secondaryContainer: Colors.indigo[400]!,
  surface: Colors.black,
  surfaceContainer: Color(0xFF1A1A1A),
  error: _darkFillColor,
  onError: _darkFillColor,
  onPrimary: _darkFillColor,
  onSecondary: _darkFillColor,
  onSurface: _darkFillColor,
  brightness: Brightness.dark,
);
