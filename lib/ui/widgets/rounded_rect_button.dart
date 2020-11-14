import 'package:flutter/material.dart';

class RoundedRectButton extends StatelessWidget {
  final String title;
  final VoidCallback onPressed;

  const RoundedRectButton({
    @required this.title,
    @required this.onPressed,
  });

  @override
  Widget build(BuildContext context) {
    final ThemeData theme = Theme.of(context);

    final ButtonStyle buttonStyle = TextButton.styleFrom(
      padding: const EdgeInsets.all(20),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
    );

    return TextButton(
        style: buttonStyle,
        child: Text(title, style: theme.textTheme.headline5),
        onPressed: onPressed);
  }
}
