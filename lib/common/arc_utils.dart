import 'dart:math' as math;

class ArcUtils {
  static double normalizeAngle(double angle) => normalize(angle, math.pi * 2);
  static double normalize(double value, double max) =>
      (value % max + max) % max;

  static double degreeToRadian(double value) => (value * math.pi) / 180;
  static double radianToDegrees(double radian) => radian * 180 / math.pi;

  static double valueToRadian(double value, double intervalValue) {
    final degree = value / intervalValue * 360;
    return degreeToRadian(degree);
  }

  static double degreeToValue(double value, double intervalValue) =>
      value / 360 * intervalValue;
}
