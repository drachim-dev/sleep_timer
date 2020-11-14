import 'package:flutter/material.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/generated/l10n.dart';
import 'package:sleep_timer/ui/widgets/section_header.dart';

class FAQView extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    final ThemeData theme = Theme.of(context);

    return Scaffold(
      appBar: AppBar(title: Text(S.of(context).faqTitle)),
      body: ListView(
        padding: const EdgeInsets.symmetric(horizontal: kHorizontalPadding),
        children: [
          SectionHeader(S.of(context).timerNoAlarm),
          Text(S.of(context).timerNoAlarmDescription),
          SectionHeader(S.of(context).timerNoWifi),
          Text(S.of(context).timerNoWifiDescription),
        ],
      ),
    );
  }
}
