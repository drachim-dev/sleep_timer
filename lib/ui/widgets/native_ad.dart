import 'package:flutter/material.dart';
import 'package:google_mobile_ads/google_mobile_ads.dart';
import 'package:sleep_timer/app/logger.util.dart';
import 'package:sleep_timer/common/ad_manager.dart';
import 'package:sleep_timer/common/constants.dart';

class NativeAdWidget extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => NativeAdState();
}

class NativeAdState extends State<NativeAdWidget> {
  final log = getLogger();

  NativeAd? _nativeAd;
  bool _nativeAdIsLoaded = false;

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();

    // add custom options before ad load
    final titleTextColor = Theme.of(context).textTheme.subtitle1!.color!;
    final subtitleTextColor = Theme.of(context).textTheme.caption!.color!;

    _nativeAd = NativeAd(
      adUnitId: AdManager.nativeAdUnitId,
      request: AdRequest(),
      factoryId: 'listTileAdFactory',
      listener: NativeAdListener(
        onAdLoaded: (Ad ad) {
          log.d('$NativeAd loaded.');
          setState(() {
            _nativeAdIsLoaded = true;
          });
        },
        onAdFailedToLoad: (Ad ad, LoadAdError error) {
          log.d('$NativeAd failedToLoad: $error');
          ad.dispose();
        },
        onAdOpened: (Ad ad) => log.d('$NativeAd onAdOpened.'),
        onAdClosed: (Ad ad) => log.d('$NativeAd onAdClosed.'),
      ),
    )
      ..customOptions = {
        'titleTextColor': '#${titleTextColor.value.toRadixString(16)}',
        'subtitleTextColor': '#${subtitleTextColor.value.toRadixString(16)}',
      }
      ..load();
  }

  @override
  Widget build(BuildContext context) {
    final nativeAd = _nativeAd;
    return _nativeAdIsLoaded && nativeAd != null
        ? Container(
            height: kAdHeight,
            padding: const EdgeInsets.symmetric(
              horizontal: kHorizontalPadding,
              vertical: kVerticalPaddingSmall,
            ),
            child: AdWidget(ad: nativeAd),
          )
        : Container();
  }

  @override
  void dispose() {
    super.dispose();
    _nativeAd?.dispose();
  }
}
