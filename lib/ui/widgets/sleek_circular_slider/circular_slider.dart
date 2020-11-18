library circular_slider;

import 'dart:math' as math;

import 'package:flutter/foundation.dart' show kIsWeb;
import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';

import 'appearance.dart';
import 'slider_animations.dart';
import 'slider_label.dart';
import 'utils.dart';

part 'curve_painter.dart';
part 'custom_gesture_recognizer.dart';

typedef void OnChange(int value);
typedef Widget InnerWidget(double percentage);

class SleekCircularSlider extends StatefulWidget {
  final int initialValue;
  final int minValue;
  final int maxValue;
  final CircularSliderAppearance appearance;
  final OnChange onChange;
  final OnChange onChangeEnd;
  final InnerWidget innerWidget;
  static const defaultAppearance = CircularSliderAppearance();

  double get angle {
    return valueToAngle(
        initialValue, minValue, maxValue, appearance.angleRange);
  }

  const SleekCircularSlider(
      {Key key,
      this.initialValue = 50,
      this.minValue = 0,
      this.maxValue = 100,
      this.appearance = defaultAppearance,
      this.onChange,
      this.onChangeEnd,
      this.innerWidget})
      : assert(initialValue != null),
        assert(minValue != null),
        assert(maxValue != null),
        assert(minValue <= maxValue),
        assert(initialValue >= minValue && initialValue <= maxValue),
        assert(appearance != null),
        super(key: key);
  @override
  _SleekCircularSliderState createState() => _SleekCircularSliderState();
}

class _SleekCircularSliderState extends State<SleekCircularSlider>
    with SingleTickerProviderStateMixin {
  bool _isHandlerSelected = false;
  bool _animationInProgress = false;
  _CurvePainter _painter;
  double _oldWidgetAngle;
  double _oldWidgetValue;
  double _currentAngle;
  double _startAngle;
  double _angleRange;
  double _selectedAngle;
  ValueChangedAnimationManager _animationManager;

  bool get _interactionEnabled =>
      (widget.onChangeEnd != null || widget.onChange != null);

  @override
  void initState() {
    super.initState();
    _startAngle = widget.appearance.startAngle;
    _angleRange = widget.appearance.angleRange;

    _setupPainter();
    if (widget.appearance.animationEnabled) _animate();
  }

  @override
  void didUpdateWidget(SleekCircularSlider oldWidget) {
    super.didUpdateWidget(oldWidget);
    if (oldWidget.angle != widget.angle && !_isHandlerSelected) _animate();
    _isHandlerSelected = false;
  }

  void _animate() {
    if (!widget.appearance.animationEnabled) {
      _setupPainter();
      _updateOnChange();
      return;
    }
    if (_animationManager == null) {
      _animationManager = ValueChangedAnimationManager(
        tickerProvider: this,
        minValue: widget.minValue.toDouble(),
        maxValue: widget.maxValue.toDouble(),
        durationMultiplier: widget.appearance.animDurationMultiplier,
      );
    }

    _animationManager.animate(
        initialValue: widget.initialValue.toDouble(),
        angle: widget.angle,
        oldAngle: _oldWidgetAngle,
        oldValue: _oldWidgetValue,
        valueChangedAnimation: ((double anim, bool animationCompleted) {
          _animationInProgress = !animationCompleted;
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

  @override
  Widget build(BuildContext context) {
    return RawGestureDetector(
        gestures: <Type, GestureRecognizerFactory>{
          CustomPanGestureRecognizer:
              GestureRecognizerFactoryWithHandlers<CustomPanGestureRecognizer>(
            () => CustomPanGestureRecognizer(
              onPanDown: _onPanDown,
              onPanUpdate: _onPanUpdate,
              onPanEnd: _onPanEnd,
            ),
            (CustomPanGestureRecognizer instance) {},
          ),
        },
        child: CustomPaint(
            painter: _painter,
            child: Container(
                width: widget.appearance.size,
                height: widget.appearance.size,
                child: _buildChildWidget())));
  }

  @override
  void dispose() {
    if (_animationManager != null) _animationManager.dispose();
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
        angle: _currentAngle < 0.5 ? 0.5 : _currentAngle,
        appearance: widget.appearance);
    _oldWidgetAngle = widget.angle;
    _oldWidgetValue = widget.initialValue.toDouble();
  }

  void _updateOnChange() {
    if (widget.onChange != null && !_animationInProgress) {
      final value = angleToValue(
          _currentAngle, widget.minValue, widget.maxValue, _angleRange);
      widget.onChange(value.truncate());
    }
  }

  Widget _buildChildWidget() {
    final value = angleToValue(
        _currentAngle, widget.minValue, widget.maxValue, _angleRange);
    final childWidget = widget.innerWidget != null
        ? widget.innerWidget(value)
        : SliderLabel(
            value: value,
            appearance: widget.appearance,
          );
    return childWidget;
  }

  void _onPanUpdate(Offset details) {
    if (!_isHandlerSelected) {
      return;
    }
    if (_painter.center == null) {
      return;
    }
    _handlePan(details, false);
  }

  void _onPanEnd(Offset details) {
    _handlePan(details, true);
    if (widget.onChangeEnd != null) {
      final value = angleToValue(
          _currentAngle, widget.minValue, widget.maxValue, _angleRange);
      widget.onChangeEnd(value.truncate());
    }
  }

  void _handlePan(Offset details, bool isPanEnd) {
    if (_painter.center == null) {
      return;
    }
    RenderBox renderBox = context.findRenderObject();
    var position = renderBox.globalToLocal(details);
    final double touchWidth = widget.appearance.progressBarWidth + 100;

    if (isPointAlongCircle(
        position, _painter.center, _painter.radius, touchWidth)) {
      _selectedAngle = coordinatesToRadians(_painter.center, position);
      // setup painter with new angle values and update onChange
      _setupPainter(counterClockwise: widget.appearance.counterClockwise);
      _updateOnChange();
      setState(() {});
    }
  }

  bool _onPanDown(Offset details) {
    if (_painter == null || _interactionEnabled == false) {
      return false;
    }
    RenderBox renderBox = context.findRenderObject();
    var position = renderBox.globalToLocal(details);

    if (position == null) {
      return false;
    }

    final angleWithinRange = isAngleWithinRange(
        startAngle: _startAngle,
        angleRange: _angleRange,
        touchAngle: coordinatesToRadians(_painter.center, position),
        previousAngle: _currentAngle,
        counterClockwise: widget.appearance.counterClockwise);
    if (!angleWithinRange) {
      return false;
    }

    final double touchWidth = widget.appearance.handlerSize + 10;

    if (isPointAlongCircle(
        position, _painter.center, _painter.radius, touchWidth)) {
      _isHandlerSelected = true;
      _onPanUpdate(details);
    } else {
      _isHandlerSelected = false;
    }

    return _isHandlerSelected;
  }
}
