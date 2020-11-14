import 'package:flutter/material.dart';
import 'package:sleek_circular_slider/sleek_circular_slider.dart';
import 'package:sleep_timer/common/constants.dart';

class TimerSlider extends StatelessWidget {
  final double size;
  final int initialValue, minValue, maxValue;
  final bool hasHandle;
  final bool showGlow;
  final bool animationEnabled;
  final TextStyle labelStyle;
  final Function(double) onChange;
  final String Function(int) onUpdateLabel;

  const TimerSlider({
    this.size,
    @required this.initialValue,
    this.minValue,
    this.maxValue,
    this.hasHandle = true,
    this.showGlow = kDefaultGlow,
    this.animationEnabled = true,
    this.labelStyle,
    this.onChange,
    this.onUpdateLabel,
  });

  @override
  Widget build(BuildContext context) {
    final ThemeData theme = Theme.of(context);

    final Color progressBarColor = theme.brightness == Brightness.light
        ? theme.colorScheme.secondary
        : theme.colorScheme.primary;

    final Color trackColor = theme.brightness == Brightness.light
        ? Colors.grey[100]
        : theme.colorScheme.surface;

    final Color shadowColor = theme.brightness == Brightness.light
        ? theme.colorScheme.secondary
        : theme.colorScheme.primary;

    final Color handleColor = theme.brightness == Brightness.light
        ? theme.colorScheme.secondary
        : Colors.white;

    final double shadowWidth = showGlow ? 25 : 0;

    return SleekCircularSlider(
        min: minValue?.toDouble() ?? 0,
        max: maxValue?.toDouble() ?? 60,
        initialValue: initialValue.toDouble(),
        appearance: CircularSliderAppearance(
            animationEnabled: animationEnabled,
            size: size ?? 256,
            customWidths: CustomSliderWidths(
              trackWidth: 15,
              progressBarWidth: 4,
              handlerSize: hasHandle ? 12 : 0,
              shadowWidth: shadowWidth,
            ),
            startAngle: 270,
            angleRange: 360,
            infoProperties: InfoProperties(
                mainLabelStyle: labelStyle ?? theme.textTheme.headline2,
                modifier: (value) => onUpdateLabel(value.round())),
            customColors: CustomSliderColors(
              trackColor: trackColor,
              progressBarColor: progressBarColor,
              shadowColor: shadowColor,
              dotColor: handleColor,
            )),
        onChange: hasHandle ? (_) {} : null,
        onChangeEnd: hasHandle ? onChange : null);
  }
}
