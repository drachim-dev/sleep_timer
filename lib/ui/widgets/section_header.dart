import 'package:flutter/material.dart';

class SectionHeader extends StatelessWidget {
  final String title;
  final double leftPadding;

  SectionHeader(this.title, {this.leftPadding = 0.0});

  @override
  Widget build(BuildContext context) {
    final ThemeData theme = Theme.of(context);
    final TextStyle textStyle =
        TextStyle(color: theme.accentColor, fontWeight: FontWeight.bold);

    return Padding(
      padding: EdgeInsets.only(left: leftPadding, bottom: 6.0, top: 20.0),
      child: Text(title, style: textStyle),
    );
  }
}
