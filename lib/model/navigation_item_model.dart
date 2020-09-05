import 'package:flutter/material.dart';

class NavigationItemController {
  VoidCallback onClickFAB;
}

class NavigationModel {
  final NavigationItemController controller;
  final Widget page;
  final String title;
  final Icon icon;
  final Icon fabIcon;

  NavigationModel({
    @required this.controller,
    @required this.page,
    @required this.title,
    @required this.icon,
    @required this.fabIcon,
  });
}
