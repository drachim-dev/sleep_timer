import 'package:flutter/material.dart';

class SectionHeader extends StatelessWidget {
  final String title;
  final double leftPadding;
  final bool dense;

  SectionHeader(this.title, {this.leftPadding = 0.0, this.dense = false});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    final denseTextStyle = theme.textTheme.bodyText1
        .copyWith(color: theme.accentColor, fontWeight: FontWeight.bold);
    final textStyle =
        theme.textTheme.subtitle1.copyWith(color: theme.accentColor);

    return Padding(
      padding: EdgeInsets.only(left: leftPadding, bottom: 6.0, top: 20.0),
      child: Text(title, style: dense ? denseTextStyle : textStyle),
    );
  }
}
