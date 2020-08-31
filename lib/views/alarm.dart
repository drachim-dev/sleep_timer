import 'package:auto_route/auto_route.dart';
import 'package:sleep_timer/model/navigation_item.dart';
import 'package:sleep_timer/auto_router.gr.dart';
import 'package:flutter/material.dart';

class AlarmPage extends StatefulWidget {
  final NavigationItemController controller;

  const AlarmPage({Key key, @required this.controller}) : super(key: key);

  @override
  _AlarmPageState createState() => _AlarmPageState(controller);
}

class _AlarmPageState extends State<AlarmPage> {
  _AlarmPageState(NavigationItemController _controller) {
    _controller.onClickFAB = onClickFAB;
  }

  @override
  Widget build(BuildContext context) {
    final ThemeData theme = Theme.of(context);

    return ListView(
      physics: ScrollPhysics(),
      children: [
        Icon(
          Icons.alarm_outlined,
          size: 80,
        )
      ],
    );
  }

  void onClickFAB() => ExtendedNavigator.root.push(Routes.alarmDetailPage);
}
