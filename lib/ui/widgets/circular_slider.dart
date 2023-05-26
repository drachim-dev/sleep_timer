import 'dart:math' as math;
import 'dart:math';

import 'package:flutter/material.dart';
import 'package:sleep_timer/common/arc_utils.dart';

class CircularSlider extends StatefulWidget {
  final double initialValue, minValue, maxValue;
  final Widget child;
  final Function(double) onChange;

  CircularSlider({
    required this.initialValue,
    required this.minValue,
    required this.maxValue,
    required this.child,
    required this.onChange,
  });

  @override
  State<CircularSlider> createState() => _CircularSliderState();
}

class _CircularSliderState extends State<CircularSlider> {
  static const double _degreeOffset = 90;
  static final double _angleOffset = ArcUtils.degreeToRadian(_degreeOffset);
  static final double _startAngle = ArcUtils.degreeToRadian(0) - _angleOffset;

  late double _currentAngle;
  double? _prevNormalizedAngle;
  late int _intervalIndex = 0;

  @override
  void initState() {
    super.initState();
    _currentAngle =
        ArcUtils.valueToRadian(widget.initialValue, widget.maxValue) -
            _angleOffset;
  }

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onPanDown: _onPanDown,
      onPanUpdate: _onPanUpdate,
      child: CustomPaint(
        painter: _CircularSliderPainter(
          startAngle: _startAngle,
          currentAngle: _currentAngle,
        ),
        child: Center(child: widget.child),
      ),
    );
  }

  void _onPanDown(DragDownDetails details) {
    _intervalIndex = (_currentAngle / (math.pi * 2)).floor();
  }

  void _onPanUpdate(DragUpdateDetails details) {
    final renderBox = context.findRenderObject() as RenderBox;
    final position = renderBox.globalToLocal(details.globalPosition);
    final size = renderBox.size;
    final center = size.center(Offset.zero);
    final radius = size.width / 2;

    double distance = (position - center).distance;
    if ((distance - radius).abs() > 75) {
      // Gesture is not in hit area
      return;
    }

    double angle = (position - center).direction;
    double normalizedAngle;
    if (position.dx < center.dx && position.dy < center.dy) {
      normalizedAngle = ArcUtils.normalizeAngle(angle);
    } else {
      normalizedAngle = angle;
    }

    if (normalizedAngle == _startAngle) {
      _prevNormalizedAngle! > 0
          ? _intervalIndex++
          : _intervalIndex = max(_intervalIndex - 1, 0);
    }
    _prevNormalizedAngle = normalizedAngle;

    final additionalIntervalAngle = _intervalIndex * math.pi * 2;
    _currentAngle = normalizedAngle + additionalIntervalAngle;

    final currentDegrees =
        ArcUtils.radianToDegrees(_currentAngle) + _degreeOffset;
    final newValue = ArcUtils.degreeToValue(currentDegrees, widget.maxValue);

    widget.onChange(math.max(widget.minValue, newValue));
  }
}

class _CircularSliderPainter extends CustomPainter {
  final double startAngle;
  final double currentAngle;

  static const _arcStrokeWidth = 8.0;
  static const _dragHandleRadius = 16.0;

  _CircularSliderPainter({
    required this.startAngle,
    required this.currentAngle,
  });

  @override
  void paint(Canvas canvas, Size size) {
    final radius = size.width / 2;

    // final sweepAngle = 1.5 * math.pi;
    final sweepAngle = currentAngle - startAngle;
    final center = size.center(Offset.zero);
    final rect = Rect.fromCircle(center: center, radius: radius);

    // full arc
    canvas.drawArc(
      rect,
      0,
      math.pi * 2,
      false,
      Paint()
        ..style = PaintingStyle.stroke
        ..strokeWidth = _arcStrokeWidth,
    );

    // selection arc
    canvas.drawArc(
      rect,
      startAngle,
      sweepAngle,
      false,
      Paint()
        ..color = Colors.red.shade200
        ..style = PaintingStyle.stroke
        ..strokeWidth = _arcStrokeWidth
        ..strokeCap = StrokeCap.round,
    );

    // drag handle
    final selectionSweepAngle = startAngle + sweepAngle;
    final dragHandleCenter = Offset(
      radius + radius * math.cos(selectionSweepAngle),
      radius + radius * math.sin(selectionSweepAngle),
    );
    canvas.drawCircle(
      dragHandleCenter,
      _dragHandleRadius,
      Paint()
        ..color = Colors.white
        ..style = PaintingStyle.fill,
    );
  }

  @override
  bool shouldRepaint(_CircularSliderPainter oldDelegate) {
    return oldDelegate.currentAngle != currentAngle;
  }
}
