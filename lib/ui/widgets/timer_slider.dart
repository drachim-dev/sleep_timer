import 'package:flutter/material.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/ui/widgets/sleek_circular_slider/appearance.dart';
import 'package:sleep_timer/ui/widgets/sleek_circular_slider/circular_slider.dart';

class TimerSlider extends StatefulWidget {
  final double size;
  final int initialValue, minValue, maxValue;
  final bool hasHandle;
  final bool showGlow;
  final bool animationEnabled;
  final TextStyle labelStyle;
  final Function(int) onChange;
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
  _TimerSliderState createState() => _TimerSliderState();
}

class _TimerSliderState extends State<TimerSlider> {
  ValueNotifier<CustomSliderColors> colors =
      ValueNotifier(CustomSliderColors());

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

    final double shadowWidth = 25;

    colors.value = CustomSliderColors(
      trackColor: trackColor,
      progressBarColor: progressBarColor,
      shadowColor: shadowColor,
      dotColor: handleColor,
      hideShadow: !widget.showGlow,
    );

    return SleekCircularSlider(
        minValue: widget.minValue ?? 0,
        maxValue: widget.maxValue ?? 60,
        initialValue: widget.initialValue,
        appearance: CircularSliderAppearance(
            animationEnabled: widget.animationEnabled,
            size: widget.size ?? 256,
            customWidths: CustomSliderWidths(
              trackWidth: 15,
              progressBarWidth: 4,
              handlerSize: widget.hasHandle ? 12 : 0,
              shadowWidth: shadowWidth,
            ),
            startAngle: 270,
            angleRange: 360,
            infoProperties: InfoProperties(
                mainLabelStyle: widget.labelStyle ?? theme.textTheme.headline2,
                modifier: (value) => widget.onUpdateLabel(value.round())),
            customColors: colors),
        onChange: widget.hasHandle ? (_) {} : null,
        onChangeEnd: widget.hasHandle ? widget.onChange : null);
  }
}
