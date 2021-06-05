import 'package:in_app_review/in_app_review.dart';
import 'package:injectable/injectable.dart';
import 'package:logger/logger.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:sleep_timer/app/locator.dart';
import 'package:sleep_timer/app/logger.util.dart';
import 'package:sleep_timer/common/constants.dart';

@lazySingleton
class ReviewService {
  final Logger log = getLogger();
  final InAppReview _inAppReview = InAppReview.instance;
  final SharedPreferences _prefsService = locator<SharedPreferences>();

  final int dayInterval = 14;
  final int minElapsed = 10;

  int _calledDate = DateTime.now().millisecondsSinceEpoch;
  int _numElapsed = 0;
  int _reviewCount = 0;

  bool shouldAskForReview() {
    _reviewCount = _prefsService.getInt(kPrefKeyReviewCount) ?? _reviewCount;
    log.d('reviewCount: $_reviewCount');
    if (_reviewCount >= kMaxAskForReview) {
      return false;
    }

    final calledDateInMillisSinceEpoch =
        _prefsService.getInt(kPrefKeyReviewCalledDate);

    if (calledDateInMillisSinceEpoch == null) {
      _prefsService.setInt(kPrefKeyReviewCalledDate, _calledDate);
    } else {
      _calledDate = calledDateInMillisSinceEpoch;
    }

    final daysSinceLastCall = DateTime.now()
        .difference(DateTime.fromMillisecondsSinceEpoch(_calledDate))
        .inDays;
    log.d('daysSinceLastCall: $daysSinceLastCall');
    if (daysSinceLastCall < dayInterval) {
      return false;
    }

    _numElapsed = _prefsService.getInt(kPrefKeyNumTimerElapsed) ?? _numElapsed;
    log.d('numElapsed: $_numElapsed');
    if (_numElapsed < minElapsed) {
      return false;
    }

    return true;
  }

  Future<void> requestReview() async {
    if (await _inAppReview.isAvailable()) {
      log.d('requestReview()');

      _calledDate = DateTime.now().millisecondsSinceEpoch;
      await _prefsService.setInt(kPrefKeyReviewCalledDate, _calledDate);
      await _prefsService.setInt(kPrefKeyReviewCount, ++_reviewCount);

      await _inAppReview.requestReview();
    }
  }

  Future<void> openStoreListing() => _inAppReview.openStoreListing();
}
