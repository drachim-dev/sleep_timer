import 'package:flutter/material.dart';

class NavigationItemController {
  VoidCallback onClickFAB;
}

class NavigationItem {
  final NavigationItemController controller;
  final Widget page;
  final String title;
  final Icon icon;
  final Icon fabIcon;

  NavigationItem({
    @required this.controller,
    @required this.page,
    @required this.title,
    @required this.icon,
    @required this.fabIcon,
  });
}
