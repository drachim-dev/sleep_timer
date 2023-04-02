import 'package:flutter/material.dart';

import '../../common/constants.dart';

class Separator extends StatelessWidget {
  final double inset;
  const Separator({
    super.key,
    this.inset = kHorizontalPadding,
  });

  @override
  Widget build(BuildContext context) {
    return Divider(
      indent: inset,
      endIndent: inset,
    );
  }
}
