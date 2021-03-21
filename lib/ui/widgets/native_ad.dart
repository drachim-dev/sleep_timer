import 'dart:async';

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
  final Completer<NativeAd> nativeAdCompleter = Completer<NativeAd>();
  NativeAd _nativeAd;

  @override
  void initState() {
    super.initState();

    _nativeAd = NativeAd(
      adUnitId: AdManager.nativeAdUnitId,
      request: AdRequest(testDevices: AdManager.testDeviceId),
      factoryId: 'listTileAdFactory',
      listener: AdListener(
        onAdLoaded: (Ad ad) {
          log.d('$NativeAd loaded.');
          nativeAdCompleter.complete(ad as NativeAd);
        },
        onAdFailedToLoad: (Ad ad, LoadAdError error) {
          ad.dispose();
          log.d('$NativeAd failedToLoad: $error');
          nativeAdCompleter.completeError(null);
        },
        onAdOpened: (Ad ad) => log.d('$NativeAd onAdOpened.'),
        onAdClosed: (Ad ad) => log.d('$NativeAd onAdClosed.'),
        onApplicationExit: (Ad ad) =>
            log.d('$NativeAd onApplicationExit.'),
      ),
    );
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();

    // add custom options before ad load
    _nativeAd.isLoaded().then((value) {
      if (!value) {
        final titleTextColor = Theme.of(context).textTheme.subtitle1.color;
        final subtitleTextColor = Theme.of(context).textTheme.caption.color;

        _nativeAd?.customOptions = {
          'titleTextColor': '#${titleTextColor.value.toRadixString(16)}',
          'subtitleTextColor': '#${subtitleTextColor.value.toRadixString(16)}',
        };
        _nativeAd?.load();
      }
    });
  }

  @override
  void dispose() {
    super.dispose();
    _nativeAd?.dispose();
    _nativeAd = null;
  }

  @override
  Widget build(BuildContext context) {
    return FutureBuilder<NativeAd>(
      future: nativeAdCompleter.future,
      builder: (_, snapshot) {
        Widget child;

        switch (snapshot.connectionState) {
          case ConnectionState.none:
          case ConnectionState.waiting:
          case ConnectionState.active:
            return Container();
            break;
          case ConnectionState.done:
            return snapshot.hasData
                ? Container(
                    height: kAdHeight,
                    padding: const EdgeInsets.symmetric(
                      horizontal: kHorizontalPadding,
                      vertical: kVerticalPaddingSmall,
                    ),
                    child: AdWidget(ad: _nativeAd),
                  )
                : Container();
        }

        return Container(child: child);
      },
    );
  }
}
