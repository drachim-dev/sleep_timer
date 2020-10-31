import 'package:flutter/material.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/ui/widgets/section_header.dart';

class FAQView extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text("Frequently asked questions"),
      ),
      body: ListView(
        padding: const EdgeInsets.symmetric(horizontal: kHorizontalPadding),
        children: [
          SectionHeader("The timer doesn't work sometimes"),
          Text(
              "Make sure that battery optimization is disabled for the app. Some device manufacturers such as Samsung or Huawei require additional settings to allow the app to be running in background."),
          SectionHeader("Wifi won't turn off"),
          Text(
              "Starting with Android 10 it is not possible to disable wifi anymore."),
        ],
      ),
    );
  }
}
