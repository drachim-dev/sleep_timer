import 'package:flutter/material.dart';

class SectionHeader extends StatelessWidget {
  final String title;
  final double leftPadding;
  final bool dense;

  SectionHeader(this.title, {this.leftPadding = 0.0, this.dense = false});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    final denseTextStyle = theme.textTheme.bodyLarge!
        .copyWith(color: theme.colorScheme.primary, fontWeight: FontWeight.bold);
    final textStyle =
        theme.textTheme.titleMedium!.copyWith(color: theme.colorScheme.primary);

    return Padding(
      padding: EdgeInsets.only(left: leftPadding, bottom: 6.0, top: 20.0),
      child: Text(title, style: dense ? denseTextStyle : textStyle),
    );
  }
}
