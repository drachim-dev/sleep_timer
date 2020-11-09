import 'package:flutter/material.dart';

class SectionHeader extends StatelessWidget {
  final String title;
  final double leftPadding;
  final bool dense;

  SectionHeader(this.title, {this.leftPadding = 0.0, this.dense = false});

  @override
  Widget build(BuildContext context) {
    final ThemeData theme = Theme.of(context);
    final double size = dense
        ? theme.textTheme.bodyText1.fontSize
        : theme.textTheme.subtitle1.fontSize;

    final TextStyle textStyle = TextStyle(
        color: theme.accentColor, fontWeight: FontWeight.bold, fontSize: size);

    return Padding(
      padding: EdgeInsets.only(left: leftPadding, bottom: 6.0, top: 20.0),
      child: Text(title, style: textStyle),
    );
  }
}
