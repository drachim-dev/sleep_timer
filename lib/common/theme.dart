import 'package:flutter/material.dart';

class ThemeNotifier with ChangeNotifier {
  ThemeData _theme;

  ThemeNotifier(this._theme);

  ThemeData get theme => _theme;

  void setTheme(ThemeData themeData) async {
    _theme = themeData;
    notifyListeners();
  }
}

class MyTheme {
  static const _lightFillColor = Colors.black;
  static const _darkFillColor = Colors.white;

  static final Color _lightFocusColor = Colors.black.withOpacity(0.12);
  static final Color _darkFocusColor = Colors.white.withOpacity(0.12);

  static ThemeData lightTheme = themeData(lightColorScheme, _lightFocusColor);
  static ThemeData darkTheme = themeData(darkColorScheme, _darkFocusColor);
  static ThemeData blackTheme = themeData(blackColorScheme, _darkFocusColor);

  static final TextTheme _textTheme = TextTheme(
    headline2: TextStyle(fontWeight: FontWeight.w300),
    subtitle1: TextStyle(fontWeight: FontWeight.w500, fontSize: 16.0),
  );

  static ThemeData getThemeFromName(String themeName) {
    switch (themeName) {
      case 'Light':
        return lightTheme;
        break;
      case 'Dark':
        return darkTheme;
        break;
      case 'Black':
        return blackTheme;
        break;
      default:
        return darkTheme;
        break;
    }
  }

  static ThemeData themeData(ColorScheme colorScheme, Color focusColor) {
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
      canvasColor: colorScheme.background,
      scaffoldBackgroundColor: colorScheme.background,
      highlightColor: Colors.transparent,
      accentColor: colorScheme.primary,
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

  static ColorScheme lightColorScheme = ColorScheme(
    primary: Colors.indigo[700],
    primaryVariant: Colors.indigo[800],
    secondary: Colors.deepOrangeAccent,
    secondaryVariant: Colors.deepOrange,
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

  static ColorScheme darkColorScheme = ColorScheme(
    primary: Colors.deepOrange[300],
    primaryVariant: Colors.deepOrange[400],
    secondary: Colors.deepPurpleAccent,
    secondaryVariant: Colors.deepPurple,
    background: Color(0xFF1F1929),
    surface: Color(0xFF241E30),
    onBackground: Color(0x0DFFFFFF),
    error: _darkFillColor,
    onError: _darkFillColor,
    onPrimary: _darkFillColor,
    onSecondary: _darkFillColor,
    onSurface: _darkFillColor,
    brightness: Brightness.dark,
  );

  static ColorScheme blackColorScheme = ColorScheme(
    primary: Colors.purple[300],
    primaryVariant: Colors.purple[400],
    secondary: Colors.teal,
    secondaryVariant: Colors.teal[700],
    background: Colors.black,
    surface: Color(0xFF111111),
    onBackground: Color(0x0DFFFFFF),
    error: _darkFillColor,
    onError: _darkFillColor,
    onPrimary: _darkFillColor,
    onSecondary: _darkFillColor,
    onSurface: _darkFillColor,
    brightness: Brightness.dark,
  );
}
