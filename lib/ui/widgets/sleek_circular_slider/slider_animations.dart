import 'package:flutter/material.dart';
import 'utils.dart';

typedef ValueChangeAnimation = void Function(double animation, bool animationFinished);

class ValueChangedAnimationManager {
  final TickerProvider tickerProvider;
  final double durationMultiplier;
  final double minValue;
  final double maxValue;

  ValueChangedAnimationManager({
    required this.tickerProvider,
    required this.minValue,
    required this.maxValue,
    this.durationMultiplier = 1.0,
  });

  late Animation<double> _animation;
  late final AnimationController _animController = AnimationController(vsync: tickerProvider);
  bool _animationCompleted = false;


  void animate(
      {required double initialValue,
      double? oldValue,
      required double angle,
      double? oldAngle,
      required ValueChangeAnimation valueChangedAnimation}) {
    _animationCompleted = false;

    final duration = (durationMultiplier *
            valueToDuration(
                initialValue, oldValue ?? minValue, minValue, maxValue))
        .toInt();

    _animController.duration = Duration(milliseconds: duration);

    final curvedAnimation = CurvedAnimation(
      parent: _animController,
      curve: Curves.easeOut,
    );

    _animation =
        Tween<double>(begin: oldAngle ?? 0, end: angle).animate(curvedAnimation)
          ..addListener(() {
            valueChangedAnimation(_animation.value, _animationCompleted);
          })
          ..addStatusListener((status) {
            if (status == AnimationStatus.completed) {
              _animationCompleted = true;
              _animController.reset();
            }
          });
    _animController.forward();
  }

  void dispose() {
    _animController.dispose();
  }
}
