import 'dart:math';

import 'package:flutter/material.dart';

import '../ui/widgets/separator.dart';

class Utils {
  static String secondsToString(int seconds,
      {spacing = false, trimTrailingZeros = false}) {
    var space = spacing ? '\u2009' : '';

    var duration = Duration(seconds: seconds);
    var minutes = duration.inMinutes.toString();
    var secondsRemainder = duration.inSeconds % 60;

    return trimTrailingZeros && secondsRemainder == 0
        ? minutes
        : "$minutes$space:$space${secondsRemainder.toString().padLeft(2, '0')}";
  }

  static var random = Random();
}

extension ExtendedWidgetList on List<Widget> {
  /// Insert [widget] between each member of this list
  List<Widget> insertBetween(Widget widget) {
    if (length > 1) {
      for (var i = length - 1; i > 0; i--) {
        insert(i, widget);
      }
    }

    return this;
  }

  List<Widget> withSeparator() {
    return insertBetween(const Separator());
  }
}

extension ExtendedColor on Color {
  Color darken([double amount = .5]) {
    assert(amount >= 0 && amount <= 1);

    final hsl = HSLColor.fromColor(this);
    final hslDark = hsl.withLightness((hsl.lightness - amount).clamp(0.0, 1.0));

    return hslDark.toColor();
  }
}
