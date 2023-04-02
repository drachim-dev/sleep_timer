import 'package:flutter/material.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/common/utils.dart';
import 'package:sleep_timer/generated/l10n.dart';
import 'package:sleep_timer/ui/widgets/section_header.dart';
import 'package:url_launcher/link.dart';

class CreditsView extends StatelessWidget {
  static final List<Credit> _imagesFromNounProject = [
    Credit(
        title: 'Philips Hue Bridge by Derrick Snider',
        url: 'https://thenounproject.com/term/phillips-hue-bridge/2731255'),
    Credit(
        title: 'Sleep by Nithinan Tatah',
        url: 'https://thenounproject.com/term/sleep/2411493/'),
  ];

  static final List<Credit> _imagesFromFreepik = [
    Credit(
        title: 'Creativity Illustrations | Cuate Style',
        url: 'https://stories.freepik.com/idea'),
    Credit(
        title: 'Flashlight Illustrations | Cuate Style',
        url: 'https://stories.freepik.com/people'),
    Credit(
        title: 'Floating Illustrations | Bro Style',
        url: 'https://stories.freepik.com/people'),
    Credit(
        title: 'Headphone Illustrations | Amico Style',
        url: 'https://stories.freepik.com/music'),
    Credit(
        title: 'Mobile marketing Illustrations | Pana Style',
        url: 'https://stories.freepik.com/business'),
    Credit(
        title: 'Web Illustrations | Cuate Style',
        url: 'https://storyset.com/web'),
  ];

  static final List<Credit> _packageCredits = [
    Credit(title: 'collection', url: 'https://pub.dev/packages/collection'),
    Credit(
        title: 'firebase_core', url: 'https://pub.dev/packages/firebase_core'),
    Credit(
        title: 'firebase_crashlytics',
        url: 'https://pub.dev/packages/firebase_crashlytics'),
    Credit(title: 'get_it', url: 'https://pub.dev/packages/get_it'),
    Credit(
        title: 'google_mobile_ads',
        url: 'https://pub.dev/packages/google_mobile_ads'),
    Credit(title: 'http', url: 'https://pub.dev/packages/http'),
    Credit(title: 'hue_dart', url: 'https://pub.dev/packages/hue_dart'),
    Credit(title: 'intl', url: 'https://pub.dev/packages/intl'),
    Credit(
        title: 'in_app_purchase',
        url: 'https://pub.dev/packages/in_app_purchase'),
    Credit(
        title: 'in_app_purchase_android',
        url: 'https://pub.dev/packages/in_app_purchase_android'),
    Credit(
        title: 'in_app_review', url: 'https://pub.dev/packages/in_app_review'),
    Credit(title: 'injectable', url: 'https://pub.dev/packages/injectable'),
    Credit(
        title: 'introduction_screen',
        url: 'https://pub.dev/packages/introduction_screen'),
    Credit(
        title: 'json_annotation',
        url: 'https://pub.dev/packages/json_annotation'),
    Credit(title: 'logger', url: 'https://pub.dev/packages/logger'),
    Credit(
        title: 'material_design_icons_flutter',
        url: 'https://pub.dev/packages/material_design_icons_flutter'),
    Credit(
        title: 'permission_handler',
        url: 'https://pub.dev/packages/permission_handler'),
    Credit(title: 'provider', url: 'https://pub.dev/packages/provider'),
    Credit(
        title: 'shared_preferences',
        url: 'https://pub.dev/packages/shared_preferences'),
    Credit(
        title: 'sleek_circular_slider',
        url: 'https://pub.dev/packages/sleek_circular_slider'),
    Credit(title: 'stacked', url: 'https://pub.dev/packages/stacked'),
    Credit(
        title: 'stacked_services',
        url: 'https://pub.dev/packages/stacked_services'),
    Credit(title: 'url_launcher', url: 'https://pub.dev/packages/url_launcher'),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          title: Text(S.of(context).creditsAppTitle),
        ),
        body: ListView(
          padding: const EdgeInsets.all(kHorizontalPaddingSmall),
          children: [
            ..._buildListItems(
                'Icons from the Noun Project', _imagesFromNounProject),
            ..._buildListItems(
                'Illustrations by Freepik Stories', _imagesFromFreepik),
            ..._buildListItems(S.current.creditsLibraries, _packageCredits),
          ],
        ));
  }

  List<Widget> _buildListItems(String title, List<Credit> items) => [
        SectionHeader(title, leftPadding: kHorizontalPaddingSmall),
        Card(
          clipBehavior: Clip.antiAlias,
          child: Column(
              children: items.map<Widget>((item) {
            return Link(
              uri: Uri.parse(item.url),
              builder: (_, FollowLink? followLink) => ListTile(
                  title: Text(item.title),
                  subtitle: Text(item.url),
                  onTap: followLink),
            );
          }).toList()
                ..withSeparator()),
        ),
      ];
}

class Credit {
  final String title, url;

  Credit({required this.title, required this.url});
}
