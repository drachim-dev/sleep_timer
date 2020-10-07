import 'package:flutter/material.dart';
import 'package:stacked/stacked.dart';

import 'alarm_detail_viewmodel.dart';

class AlarmDetailView extends StatefulWidget {
  @override
  _AlarmDetailViewState createState() => _AlarmDetailViewState();
}

class _AlarmDetailViewState extends State<AlarmDetailView> {
  AlarmDetailViewModel model;

  @override
  Widget build(BuildContext context) {
    final ThemeData theme = Theme.of(context);

    return ViewModelBuilder<AlarmDetailViewModel>.reactive(
        viewModelBuilder: () => AlarmDetailViewModel(),
        onModelReady: (model) => this.model = model,
        builder: (context, model, child) {
          return Scaffold(
            appBar: _buildAppBar(theme),
            body: _buildBody(theme),
          );
        });
  }

  AppBar _buildAppBar(ThemeData theme) {
    return AppBar();
  }

  _buildBody(ThemeData theme) {
    return Container(
      child: Text("AlarmDetailPage"),
    );
  }
}
