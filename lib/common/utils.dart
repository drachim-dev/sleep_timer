import 'dart:math';

class Utils {
  static String secondsToString(int seconds,
      {spacing = false, trimTrailingZeros = false}) {
    var space = spacing ? "\u2009" : "";

    var duration = Duration(seconds: seconds);
    var minutes = duration.inMinutes.toString();
    var secondsRemainder = duration.inSeconds % 60;

    return trimTrailingZeros && secondsRemainder == 0
        ? minutes
        : "$minutes$space:$space${secondsRemainder.toString().padLeft(2, '0')}";
  }

  static var random = Random();
}
