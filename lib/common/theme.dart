import 'package:flutter/material.dart';

class MyTheme {
  final String id, title;
  final ThemeData theme;

  const MyTheme({this.id, this.title, this.theme});
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
    id: "light_theme",
    title: "Light",
    theme: _themeData(_lightColorScheme, _lightFocusColor),
  ),
  MyTheme(
    id: "dark_theme",
    title: "Dark",
    theme: _themeData(_darkColorScheme, _darkFocusColor),
  ),
  MyTheme(
    id: "black_theme",
    title: "Black",
    theme: _themeData(_blackColorScheme, _darkFocusColor),
  )
];

ThemeData _themeData(ColorScheme colorScheme, Color focusColor) {
  return ThemeData(
    colorScheme: colorScheme,
    textTheme: _textTheme,
    primaryColor: const Color(0xFF030303),
    primaryColorBrightness: colorScheme.brightness,
    appBarTheme: AppBarTheme(
      textTheme: _textTheme.apply(bodyColor: colorScheme.onBackground),
      color: colorScheme.background,
      elevation: 0,
      iconTheme: IconThemeData(color: colorScheme.primary),
      brightness: colorScheme.brightness,
    ),
    iconTheme: IconThemeData(color: colorScheme.onPrimary),
    bottomNavigationBarTheme: BottomNavigationBarThemeData(
        selectedIconTheme: IconThemeData(color: colorScheme.primary),
        backgroundColor: colorScheme.surface),
    floatingActionButtonTheme: FloatingActionButtonThemeData(
        foregroundColor: Colors.white, backgroundColor: colorScheme.secondary),
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
    popupMenuTheme: PopupMenuThemeData(
      color: colorScheme.surface,
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
  );
}

ColorScheme _lightColorScheme = ColorScheme(
  primary: Colors.blueGrey[600],
  primaryVariant: Colors.deepOrangeAccent[100],
  secondary: Colors.deepOrangeAccent[100],
  secondaryVariant: Colors.deepOrange[300],
  background: Color(0xFFFCFCFC),
  surface: Color(0xFFFCFCFC),
  onBackground: Colors.grey[900],
  error: _lightFillColor,
  onError: _lightFillColor,
  onPrimary: _lightFillColor,
  onSecondary: Color(0xFF322942),
  onSurface: Color(0xFF241E30),
  brightness: Brightness.light,
);

ColorScheme _darkColorScheme = ColorScheme(
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
