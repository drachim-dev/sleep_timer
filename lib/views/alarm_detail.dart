import 'package:flutter/material.dart';

class AlarmDetailPage extends StatefulWidget {
  @override
  _AlarmDetailPageState createState() => _AlarmDetailPageState();
}

class _AlarmDetailPageState extends State<AlarmDetailPage> {
  @override
  Widget build(BuildContext context) {
    final ThemeData theme = Theme.of(context);

    return Scaffold(
      appBar: _buildAppBar(theme),
      body: _buildyBody(theme),
    );
  }

  AppBar _buildAppBar(ThemeData theme) {
    return AppBar();
  }

  _buildyBody(ThemeData theme) {
    return Container(
      child: Text("AlarmDetailPage"),
    );
  }
}
