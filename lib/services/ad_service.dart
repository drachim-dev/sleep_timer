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

  final log = getLogger();

  AdService(this._prefsService);

  @factoryMethod
  static Future<AdService> create(SharedPreferences prefsService) async {
    var instance = AdService(prefsService);
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

  /// Shows an ad (if user was not asked for review)
  /// every [_interval] times according to [_counter]
  /// or [force] to show an ad.
  Future<bool> mayShow({bool force = false}) async {
    final reviewCount = _prefsService.getInt(kPrefKeyReviewCount) ?? 0;
    log.d('mayShowAd reviewCount: $reviewCount');

    // Only if reviewCount is exceeded to prevent from showing both (review and ad).
    if (force || reviewCount >= kMaxAskForReview) {
      await _prefsService.setInt(kPrefKeyAdIntervalCounter, ++_counter);
      log.d('mayShowAd counter: $_counter, interval: $_interval');

      if (force || _counter % _interval == 0) {
        _counter = 0;
        _loadAd();

        return true;
      }
    }
    return false;
  }

  void _loadAd() {
    InterstitialAd.load(
        adUnitId: AdManager.interstitialAdUnitId,
        request: const AdRequest(),
        adLoadCallback: InterstitialAdLoadCallback(
          // Called when an ad is successfully received.
          onAdLoaded: (InterstitialAd ad) {
            ad.fullScreenContentCallback = FullScreenContentCallback(
                onAdFailedToShowFullScreenContent: (ad, err) {
                  log.d('$ad onAdFailedToShowFullScreenContent: $err');
                  ad.dispose();
                },
                onAdDismissedFullScreenContent: (ad) {
                  log.d('$ad onAdDismissedFullScreenContent.');
                  ad.dispose();
                },
                onAdClicked: (ad) {});

            // Keep a reference to the ad so we can dispose it later.
            _interstitialAd = ad;
            ad.show();
          },
          onAdFailedToLoad: (LoadAdError error) {
            log.d('InterstitialAd failed to load: $error');
          },
        ));
  }
}
