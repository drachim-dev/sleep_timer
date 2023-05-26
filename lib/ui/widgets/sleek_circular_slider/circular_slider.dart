library circular_slider;

import 'dart:math';

import 'package:flutter/material.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/foundation.dart' show kIsWeb;
import 'package:sleep_timer/ui/widgets/sleek_circular_slider/slider_animations.dart';
import 'utils.dart';
import 'appearance.dart';
import 'dart:math' as math;

part 'curve_painter.dart';
part 'custom_gesture_recognizer.dart';

typedef OnChange = void Function(int lapIndex, int currentLapValue);

enum SlidingState { none, endIsBiggerThanStart, endIsSmallerThanStart }

class SleekCircularSlider extends StatefulWidget {
  final double initial;
  final double min;
  final double max;
  final CircularSliderAppearance appearance;
  final OnChange? onChange;
  final Widget Function(int lapIndex, int currentLapValue)? child;
  static const defaultAppearance = CircularSliderAppearance();

  double get angle {
    return valueToAngle(initial, min, max, appearance.angleRange);
  }

  const SleekCircularSlider({
    Key? key,
    this.initial = 50,
    this.min = 0,
    this.max = 100,
    this.appearance = defaultAppearance,
    this.onChange,
    this.child,
  })  : assert(min <= max),
        assert(initial >= min),
        super(key: key);
  @override
  SleekCircularSliderState createState() => SleekCircularSliderState();
}

class SleekCircularSliderState extends State<SleekCircularSlider>
    with SingleTickerProviderStateMixin {
  bool _isHandlerSelected = false;
  _CurvePainter? _painter;
  double? _oldWidgetAngle;
  double? _oldWidgetValue;
  double? _currentAngle;
  late double _value;
  late double _startAngle;
  late double _angleRange;
  double? _selectedAngle;
  double? _rotation;
  ValueChangedAnimationManager? _animationManager;
  late int _appearanceHashCode;

  /// will store the index of full laps (2pi radians) as part of the selection
  int _lapIndex = 0;

  bool get _interactionEnabled => widget.onChange != null;

  @override
  void initState() {
    super.initState();
    _value = widget.initial;
    _startAngle = widget.appearance.startAngle;
    _angleRange = widget.appearance.angleRange;
    _appearanceHashCode = widget.appearance.hashCode;

    if (widget.appearance.animationEnabled) {
      _animate();
    }
  }

  @override
  void didUpdateWidget(SleekCircularSlider oldWidget) {
    if (oldWidget.angle != widget.angle &&
        _currentAngle?.toStringAsFixed(4) != widget.angle.toStringAsFixed(4)) {

      _lapIndex = (widget.initial / (60 + 1)).floor();

      if (widget.appearance.animationEnabled) {
        _animate();
      } else {
        // update without animation
        _setupPainter();
        _updateOnChange();
      }
    }
    super.didUpdateWidget(oldWidget);
  }

  void _animate() {
    _animationManager ??= ValueChangedAnimationManager(
      tickerProvider: this,
      minValue: widget.min,
      maxValue: widget.max,
      durationMultiplier: widget.appearance.animDurationMultiplier,
    );

    _animationManager!.animate(
        initialValue: widget.initial,
        angle: widget.angle,
        oldAngle: _oldWidgetAngle,
        oldValue: _oldWidgetValue,
        valueChangedAnimation: ((double anim, bool animationCompleted) {
          setState(() {
            if (!animationCompleted) {
              _currentAngle = anim;
              // update painter and the on change closure
              _setupPainter();
              _updateOnChange();
            }
          });
        }));
  }

  // we just want to see if a value changed drastically its value
  bool radiansWasModuloed(double current, double previous) {
    return (previous - current).abs() / widget.max * 100 > 50;
  }

  @override
  Widget build(BuildContext context) {
    /// _setupPainter excution when _painter is null or appearance has changed.
    if (_painter == null || _appearanceHashCode != widget.appearance.hashCode) {
      _appearanceHashCode = widget.appearance.hashCode;
      _setupPainter();
    }
    return RawGestureDetector(
        gestures: <Type, GestureRecognizerFactory>{
          _CustomPanGestureRecognizer:
              GestureRecognizerFactoryWithHandlers<_CustomPanGestureRecognizer>(
            () => _CustomPanGestureRecognizer(
              onPanDown: _onPanDown,
              onPanUpdate: _onPanUpdate,
              onPanEnd: _onPanEnd,
            ),
            (_CustomPanGestureRecognizer instance) {},
          ),
        },
        child: _buildRotatingPainter(
            rotation: _rotation,
            size: Size(widget.appearance.size, widget.appearance.size)));
  }

  @override
  void dispose() {
    _animationManager?.dispose();
    super.dispose();
  }

  void _setupPainter({bool counterClockwise = false}) {
    var defaultAngle = _currentAngle ?? widget.angle;

    if (_oldWidgetAngle != null) {
      if (_oldWidgetAngle != widget.angle) {
        _selectedAngle = null;
        defaultAngle = widget.angle;
      }
    }

    _currentAngle = calculateAngle(
        startAngle: _startAngle,
        angleRange: _angleRange,
        selectedAngle: _selectedAngle,
        defaultAngle: defaultAngle,
        counterClockwise: counterClockwise);

    _painter = _CurvePainter(
        startAngle: _startAngle,
        angleRange: _angleRange,
        angle: _currentAngle! < 0.5 ? 0.5 : _currentAngle!,
        appearance: widget.appearance);
    _oldWidgetAngle = widget.angle;
    _oldWidgetValue = widget.initial;
  }

  void _updateOnChange() {
      final prevValue = _value;

      _value =
          angleToValue(_currentAngle!, widget.min, widget.max, _angleRange);

      if (radiansWasModuloed(prevValue, _value)) {
        prevValue > _value ? _lapIndex++ : _lapIndex = max(_lapIndex - 1, 0);
      }

      widget.onChange?.call(_lapIndex, _value.round());
  }

  Widget _buildRotatingPainter({double? rotation, required Size size}) {
    if (rotation != null) {
      return Transform(
          transform: Matrix4.identity()..rotateZ((rotation) * 5 * math.pi / 6),
          alignment: FractionalOffset.center,
          child: _buildPainter(size: size));
    } else {
      return _buildPainter(size: size);
    }
  }

  Widget _buildPainter({required Size size}) {
    final childWidget = widget.child?.call(_lapIndex, _value.round());

    return CustomPaint(
      painter: _painter,
      child: Container(
        width: size.width,
        height: size.height,
        child: childWidget,
      ),
    );
  }

  void _onPanUpdate(Offset details) {
    if (!_isHandlerSelected) {
      return;
    }
    if (_painter?.center == null) {
      return;
    }
    _handlePan(details, false);
  }

  void _onPanEnd(Offset details) {
    _handlePan(details, true);

    _isHandlerSelected = false;
  }

  void _handlePan(Offset details, bool isPanEnd) {
    if (_painter?.center == null) {
      return;
    }
    RenderBox renderBox = context.findRenderObject() as RenderBox;
    var position = renderBox.globalToLocal(details);
    final double touchWidth = widget.appearance.progressBarWidth >= 25.0
        ? widget.appearance.progressBarWidth
        : 25.0;
    if (isPointAlongCircle(
        position, _painter!.center!, _painter!.radius, touchWidth)) {
      _selectedAngle = coordinatesToRadians(_painter!.center!, position);
      // setup painter with new angle values and update onChange
      _setupPainter();
      _updateOnChange();
      setState(() {});
    }
  }

  bool _onPanDown(Offset details) {
    if (_painter == null || _interactionEnabled == false) {
      return false;
    }
    RenderBox renderBox = context.findRenderObject() as RenderBox;
    var position = renderBox.globalToLocal(details);

    final angleWithinRange = isAngleWithinRange(
        startAngle: _startAngle,
        angleRange: _angleRange,
        touchAngle: coordinatesToRadians(_painter!.center!, position),
        previousAngle: _currentAngle);
    if (!angleWithinRange) {
      return false;
    }

    final double touchWidth = widget.appearance.progressBarWidth >= 25.0
        ? widget.appearance.progressBarWidth
        : 25.0;

    if (isPointAlongCircle(
        position, _painter!.center!, _painter!.radius, touchWidth)) {
      _isHandlerSelected = true;

      _onPanUpdate(details);
    } else {
      _isHandlerSelected = false;
    }

    return _isHandlerSelected;
  }
}