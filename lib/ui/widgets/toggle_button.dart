import 'package:flutter/material.dart';
import 'package:sleep_timer/common/constants.dart';

class ToggleButton extends StatefulWidget {
  final String label;
  final IconData activeIcon;
  final IconData disabledIcon;
  final ValueChanged<bool> onChanged;
  final VoidCallback onLongPress;

  final bool value;
  final double size;

  ToggleButton({
    this.label,
    this.activeIcon,
    this.disabledIcon,
    this.onChanged,
    this.onLongPress,
    this.value,
    this.size,
  });
  @override
  _ToggleButtonState createState() => _ToggleButtonState();
}

class _ToggleButtonState extends State<ToggleButton> {
  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final toggleButtonsTheme = ToggleButtonsTheme.of(context);

    final currentColor = widget.value
        ? toggleButtonsTheme.selectedColor
        : toggleButtonsTheme.disabledColor;

    final fillColor =
        widget.value ? toggleButtonsTheme.fillColor : Colors.transparent;

    final currentTextStyle =
        toggleButtonsTheme.textStyle ?? theme.textTheme.bodyText2;

    const padding = 8.0;
    final size = widget.size - padding;

    return RawMaterialButton(
        elevation: 0,
        focusElevation: 0,
        highlightElevation: 0,
        hoverElevation: 0,
        padding: const EdgeInsets.symmetric(vertical: padding),
        textStyle: currentTextStyle.copyWith(color: currentColor),
        child: Column(
          children: [
            Icon(widget.value ? widget.activeIcon : widget.disabledIcon,
                size: size),
            SizedBox(height: kVerticalPaddingSmall),
            Text(widget.label, maxLines: 1, overflow: TextOverflow.ellipsis,),
          ],
        ),
        fillColor: fillColor,
        focusColor: toggleButtonsTheme.focusColor,
        highlightColor: toggleButtonsTheme.highlightColor,
        hoverColor: toggleButtonsTheme.hoverColor,
        splashColor: toggleButtonsTheme.splashColor,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
        onPressed: () {
          widget.onChanged(!widget.value);
        },
        onLongPress: widget.onLongPress);
  }
}