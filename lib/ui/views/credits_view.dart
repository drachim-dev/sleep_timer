import 'dart:io';

import 'package:flutter/material.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/ui/widgets/section_header.dart';
import 'package:url_launcher/link.dart';

class CreditsView extends StatelessWidget {
  static final List _imageCredits = [
    "Images",
    Credit(
        title: "sleep by Nithinan Tatah from the Noun Project",
        url: "https://thenounproject.com/term/sleep/2411493/"),
  ];

  static final List _packageCredits = [
    "Libraries",
    Credit(title: "auto_route", url: "https://pub.dev/packages/auto_route"),
    Credit(
        title: "flutter_native_admob",
        url: "https://pub.dev/packages/flutter_native_admob"),
    Credit(title: "get_it", url: "https://pub.dev/packages/get_it"),
    Credit(
        title: "in_app_purchase",
        url: "https://pub.dev/packages/in_app_purchase"),
    Credit(title: "injectable", url: "https://pub.dev/packages/injectable"),
    Credit(title: "logger", url: "https://pub.dev/packages/logger"),
    Credit(
        title: "observable_ish",
        url: "https://pub.dev/packages/observable_ish"),
    Credit(title: "preferences", url: "https://pub.dev/packages/preferences"),
    Credit(title: "provider", url: "https://pub.dev/packages/provider"),
    Credit(
        title: "shared_preferences",
        url: "https://pub.dev/packages/shared_preferences"),
    Credit(
        title: "sleek_circular_slider",
        url: "https://pub.dev/packages/sleek_circular_slider"),
    Credit(title: "stacked", url: "https://pub.dev/packages/stacked"),
    Credit(
        title: "stacked_services",
        url: "https://pub.dev/packages/stacked_services"),
    Credit(
        title: "url_launcher",
        url: "https://pub.dev/packages/url_launcher/install"),
  ];

  static final List _creditList = _imageCredits + _packageCredits;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(title: Text("Credits")),
        body: ListView.builder(
          itemCount: _creditList.length,
          // ignore: missing_return
          itemBuilder: (_, int index) {
            final item = _creditList[index];

            if (item is String) {
              return SectionHeader(item,
                  leftPadding: kPreferenceTitleLeftPadding);
            }

            if (item is Credit) {
              return Link(
                uri: Uri.parse(item.url),
                builder: (_, FollowLink followLink) => ListTile(
                    title: Text(item.title),
                    subtitle: Text(item.url),
                    onTap: followLink),
              );
            }
          },
        ));
  }
}

class Credit {
  final String title, url;

  Credit({@required this.title, @required this.url});
}