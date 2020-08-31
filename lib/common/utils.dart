class Utils {
  static String secondsToString(int seconds, {spacing = false}) {
    var duration = Duration(seconds: seconds);
    var space = spacing ? "\u2009" : "";
    return '${duration.inMinutes}$space:$space${(duration.inSeconds % 60).toString().padLeft(2, '0')}';
  }
}
