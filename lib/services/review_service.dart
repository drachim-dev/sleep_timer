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
  final _prefsService = locator<SharedPreferences>();

  final int dayInterval = 14;
  final int minElapsed = 4;
  final int maxAskForReview = 5;

  int _calledDate, _numElapsed, _reviewCount;

  ReviewService() {
    _calledDate = _prefsService.getInt(kPrefKeyReviewCalledDate);
    if (_calledDate == null) {
      _calledDate = DateTime.now().millisecondsSinceEpoch;
      _prefsService.setInt(kPrefKeyReviewCalledDate, _calledDate);
    }

    _reviewCount = _prefsService.getInt(kPrefKeyReviewCount) ?? 0;
  }

  bool shouldAskForReview() {
    log.d('reviewCount: $_reviewCount');
    if (maxAskForReview != null && _reviewCount >= maxAskForReview) {
      return false;
    }

    final calledDate = DateTime.fromMillisecondsSinceEpoch(_calledDate);
    final daysSinceLastCall = DateTime.now().difference(calledDate).inDays;

    log.d('daysSinceLastCall: $daysSinceLastCall');
    if (dayInterval != null && daysSinceLastCall < dayInterval) {
      return false;
    }

    _numElapsed = _prefsService.getInt(kPrefKeyNumTimerElapsed) ?? 0;
    log.d('numElapsed: $_numElapsed');
    if (minElapsed != null && _numElapsed < minElapsed) {
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
