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

    return FlatButton(
        padding: const EdgeInsets.all(25.0),
        shape:
            RoundedRectangleBorder(borderRadius: BorderRadius.circular(10.0)),
        child: Text(title, style: theme.textTheme.headline5),
        onPressed: onPressed);
  }
}
