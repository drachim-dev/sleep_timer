import 'package:flutter/material.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/generated/l10n.dart';
import 'package:sleep_timer/ui/widgets/section_header.dart';

class FAQView extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    final bottomInsets = MediaQuery.of(context).viewPadding.bottom;
    return Scaffold(
      appBar: AppBar(
        title: Text(S.of(context).faqTitle),
      ),
      body: SafeArea(
        child: ListView(
          padding: EdgeInsets.only(
              left: kHorizontalPadding,
              right: kHorizontalPadding,
              bottom: bottomInsets),
          children: [
            SectionHeader(S.of(context).timerNoAlarm),
            Text(S.of(context).timerNoAlarmDescription),
            SectionHeader(S.of(context).timerNoBluetooth),
            Text(S.of(context).timerNoBluetoothDescription),
            SectionHeader(S.of(context).timerNoWifi),
            Text(S.of(context).timerNoWifiDescription),
            SectionHeader(S.of(context).cannotUninstallTitle),
            Text(S.of(context).cannotUninstallDesc),
          ],
        ),
      ),
    );
  }
}
