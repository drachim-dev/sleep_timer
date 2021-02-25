import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:sleep_timer/common/constants.dart';

class MyTheme {
  final String id, title;
  final ThemeData theme;

  const MyTheme(
      {@required this.id, @required this.title, @required this.theme});
}

const _lightFillColor = Colors.black;
const _darkFillColor = Colors.white;

final Color _lightFocusColor = Colors.black.withOpacity(0.12);
final Color _darkFocusColor = Colors.white.withOpacity(0.12);

const TextTheme _textTheme = TextTheme(
  headline2: TextStyle(fontWeight: FontWeight.w300),
  subtitle1: TextStyle(fontWeight: FontWeight.w500, fontSize: 16.0),
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
  )
];

ThemeData _themeData(ColorScheme colorScheme, Color focusColor) {
  final isLight = colorScheme.brightness == Brightness.light;

  return ThemeData(
    colorScheme: colorScheme,
    textTheme: _textTheme,
    primaryColor: const Color(0xFF030303),
    primaryColorBrightness: colorScheme.brightness,
    appBarTheme: AppBarTheme(
      textTheme: _textTheme.apply(bodyColor: colorScheme.onBackground),
      backgroundColor: Colors.transparent,
      elevation: 0,
      iconTheme: IconThemeData(color: colorScheme.primary),
      systemOverlayStyle: isLight
          ? SystemUiOverlayStyle.dark
              .copyWith(statusBarColor: Colors.transparent)
          : SystemUiOverlayStyle.light
              .copyWith(statusBarColor: Colors.transparent),
    ),
    iconTheme: IconThemeData(color: colorScheme.onPrimary),
    bottomNavigationBarTheme: BottomNavigationBarThemeData(
      selectedIconTheme: IconThemeData(color: colorScheme.primary),
      backgroundColor: colorScheme.surface,
    ),
    floatingActionButtonTheme: FloatingActionButtonThemeData(
      foregroundColor: Colors.white,
      backgroundColor: colorScheme.secondary,
    ),
    canvasColor: colorScheme.background,
    scaffoldBackgroundColor: colorScheme.background,
    highlightColor: Colors.transparent,
    accentColor: colorScheme.primary,
    toggleableActiveColor: colorScheme.primaryVariant,
    textSelectionTheme: TextSelectionThemeData(
      selectionColor: colorScheme.primary,
      selectionHandleColor: colorScheme.primary,
      cursorColor: colorScheme.primary,
    ),
    dialogBackgroundColor: colorScheme.surface,
    cardColor: colorScheme.surface,
    popupMenuTheme: PopupMenuThemeData(
      color: colorScheme.surface,
    ),
    bottomSheetTheme: BottomSheetThemeData(
      backgroundColor: colorScheme.surface,
      modalBackgroundColor: colorScheme.surface,
    ),
    focusColor: focusColor,
    snackBarTheme: SnackBarThemeData(
      behavior: SnackBarBehavior.floating,
      backgroundColor: Color.alphaBlend(
        _lightFillColor.withOpacity(0.80),
        _darkFillColor,
      ),
      contentTextStyle: _textTheme.subtitle1.apply(color: _darkFillColor),
    ),
    toggleButtonsTheme: ToggleButtonsThemeData(
      constraints: BoxConstraints(minWidth: 80),
      selectedColor: colorScheme.primary,
      disabledColor: colorScheme.onSurface.withOpacity(0.38),
      fillColor: colorScheme.primary.withOpacity(0.18),
    ),
    outlinedButtonTheme: OutlinedButtonThemeData(
        style: OutlinedButton.styleFrom(
      shape: StadiumBorder(),
    )),
  );
}

ColorScheme _lightOrangeColorScheme = ColorScheme(
  primary: Colors.blueGrey[600],
  primaryVariant: Colors.deepOrangeAccent[100],
  secondary: Colors.deepOrangeAccent[100],
  secondaryVariant: Colors.deepOrange[300],
  background: Color(0xFFFCFCFC),
  surface: Color(0xFFF4F4F4),
  onBackground: Colors.grey[900],
  error: _lightFillColor,
  onError: _lightFillColor,
  onPrimary: _lightFillColor,
  onSecondary: Color(0xFF322942),
  onSurface: Color(0xFF241E30),
  brightness: Brightness.light,
);

ColorScheme _lightGreenColorScheme = ColorScheme(
  primary: Colors.blueGrey[600],
  primaryVariant: Color(0xFF68D391),
  secondary: Color(0xFF68D391),
  secondaryVariant: Color(0xFF48BB78),
  background: Color(0xFFFCFCFC),
  surface: Color(0xFFF4F4F4),
  onBackground: Colors.grey[900],
  error: _lightFillColor,
  onError: _lightFillColor,
  onPrimary: _lightFillColor,
  onSecondary: Color(0xFF322942),
  onSurface: Color(0xFF241E30),
  brightness: Brightness.light,
);

ColorScheme _darkOrangeColorScheme = ColorScheme(
  primary: Colors.deepOrange[300],
  primaryVariant: Colors.tealAccent,
  secondary: Colors.deepPurpleAccent,
  secondaryVariant: Colors.deepPurple,
  background: Color(0xFF1F1929),
  surface: Color(0xFF393343), // 241E30
  onBackground: Color(0x0DFFFFFF),
  error: _darkFillColor,
  onError: _darkFillColor,
  onPrimary: _darkFillColor,
  onSecondary: _darkFillColor,
  onSurface: _darkFillColor,
  brightness: Brightness.dark,
);

ColorScheme _darkYellowColorScheme = ColorScheme(
  primary: Colors.yellow[800],
  primaryVariant: Colors.tealAccent[400],
  secondary: Colors.teal[700],
  secondaryVariant: Colors.teal[800],
  background: Color(0xFF1b1b1b),
  surface: Color(0xFF373737),
  onBackground: Color(0x0DFFFFFF),
  error: _darkFillColor,
  onError: _darkFillColor,
  onPrimary: _darkFillColor,
  onSecondary: _darkFillColor,
  onSurface: _darkFillColor,
  brightness: Brightness.dark,
);

ColorScheme _blackColorScheme = ColorScheme(
  primary: Colors.indigo[200],
  primaryVariant: Colors.indigoAccent[100],
  secondary: Colors.indigo[500],
  secondaryVariant: Colors.indigo[400],
  background: Colors.black,
  surface: Color(0xFF1A1A1A),
  onBackground: Color(0x0DFFFFFF),
  error: _darkFillColor,
  onError: _darkFillColor,
  onPrimary: _darkFillColor,
  onSecondary: _darkFillColor,
  onSurface: _darkFillColor,
  brightness: Brightness.dark,
);
