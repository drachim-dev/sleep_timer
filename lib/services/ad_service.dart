import 'package:flutter/cupertino.dart';
import 'package:google_mobile_ads/google_mobile_ads.dart';
import 'package:injectable/injectable.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:sleep_timer/app/logger.util.dart';
import 'package:sleep_timer/common/ad_manager.dart';
import 'package:sleep_timer/common/constants.dart';

@lazySingleton
class AdService {
  final SharedPreferences _prefsService;

  final int _interval = kDefaultAdInterval;
  int _counter = 0;

  InterstitialAd? _interstitialAd;
  int _numInterstitialLoadAttempts = 0;
  static const int maxFailedLoadAttempts = 3;

  final log = getLogger();

  AdService(this._prefsService);

  @factoryMethod
  static Future<AdService> create(SharedPreferences _prefsService) async {
    var instance = AdService(_prefsService);
    await instance._init();

    return instance;
  }

  Future<void> _init() async {
    await MobileAds.instance.initialize();
    await MobileAds.instance.updateRequestConfiguration(
        RequestConfiguration(testDeviceIds: AdManager.testDeviceId));

    _counter = _prefsService.getInt(kPrefKeyAdIntervalCounter) ?? _counter;
  }

  void dispose() {
    _interstitialAd?.dispose();
  }

  void _createInterstitialAd(VoidCallback onAdLoaded) {
    InterstitialAd.load(
        adUnitId: AdManager.interstitialAdUnitId,
        request: AdRequest(),
        adLoadCallback: InterstitialAdLoadCallback(
          onAdLoaded: (InterstitialAd ad) {
            log.d('$ad loaded');
            _interstitialAd = ad;
            _numInterstitialLoadAttempts = 0;

            onAdLoaded();
          },
          onAdFailedToLoad: (LoadAdError error) {
            log.d('InterstitialAd failed to load: $error.');
            _numInterstitialLoadAttempts += 1;
            _interstitialAd = null;
            if (_numInterstitialLoadAttempts <= maxFailedLoadAttempts) {
              _createInterstitialAd(_showInterstitialAd);
            }
          },
        ));
  }

  /// Shows an ad (if user was not asked for review)
  /// every [_interval] times according to [_counter]
  /// or [force] to show an ad.
  Future<bool> mayShow({bool force = false}) async {
    final _reviewCount = _prefsService.getInt(kPrefKeyReviewCount) ?? 0;
    log.d('mayShowAd reviewCount: $_reviewCount');

    // Only if reviewCount is exceeded to prevent from showing both (review and ad).
    if (force || _reviewCount >= kMaxAskForReview) {
      await _prefsService.setInt(kPrefKeyAdIntervalCounter, ++_counter);
      log.d('mayShowAd counter: $_counter, interval: $_interval');

      if (force || _counter % _interval == 0) {
        _counter = 0;
        _createInterstitialAd(_showInterstitialAd);

        return true;
      }
    }
    return false;
  }

  void _showInterstitialAd() {
    if (_interstitialAd == null) {
      log.d('Warning: attempt to show interstitial before loaded.');
      return;
    }
    _interstitialAd!.fullScreenContentCallback = FullScreenContentCallback(
      onAdShowedFullScreenContent: (InterstitialAd ad) =>
          log.d('ad onAdShowedFullScreenContent.'),
      onAdDismissedFullScreenContent: (InterstitialAd ad) {
        log.d('$ad onAdDismissedFullScreenContent.');
        ad.dispose();
      },
      onAdFailedToShowFullScreenContent: (InterstitialAd ad, AdError error) {
        log.d('$ad onAdFailedToShowFullScreenContent: $error');
        ad.dispose();
      },
    );
    _interstitialAd!.show();
    _interstitialAd = null;
  }
}
