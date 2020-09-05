import 'package:auto_route/auto_route.dart';
import 'package:sleep_timer/model/navigation_item_model.dart';
import 'package:flutter/material.dart';
import 'package:stacked/stacked.dart';

import 'alarm_viewmodel.dart';

class AlarmView extends StatefulWidget {
  final NavigationItemController controller;

  const AlarmView({Key key, @required this.controller}) : super(key: key);

  @override
  _AlarmViewState createState() => _AlarmViewState(controller);
}

class _AlarmViewState extends State<AlarmView> {
  AlarmViewModel model;

  _AlarmViewState(NavigationItemController _controller) {
    _controller.onClickFAB = onClickFAB;
  }

  @override
  Widget build(BuildContext context) {
    final ThemeData theme = Theme.of(context);

    return ViewModelBuilder<AlarmViewModel>.reactive(
        viewModelBuilder: () => AlarmViewModel(),
        onModelReady: (model) => this.model = model,
        builder: (context, model, child) {
          return ListView(
            physics: ScrollPhysics(),
            children: [
              Icon(
                Icons.alarm_outlined,
                size: 80,
              )
            ],
          );
        });
  }

  void onClickFAB() => model.navigateToDetail();
}
