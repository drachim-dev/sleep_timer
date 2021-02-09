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
  final _prefsService = locator<SharedPreferences>();
  final InAppReview _inAppReview = InAppReview.instance;

  final int minDays = 14;
  final int minElapsed = 7;
  bool askForReview;
  int _installDate, _numElapsed;

  ReviewService() {
    askForReview = _prefsService.getBool(kPrefKeyAskForReview) ?? true;

    if (askForReview) {
      _installDate = _prefsService.getInt(kPrefKeyInstallDate);
      if (_installDate == null) {
        _installDate = DateTime.now().millisecondsSinceEpoch;
        _prefsService.setInt(kPrefKeyInstallDate, _installDate);
      }
    }
  }

  bool shouldAskForReview() {
    if (!askForReview) {
      return false;
    }

    log.d(
        'daysUsed: ${DateTime.now().difference(DateTime.fromMillisecondsSinceEpoch(_installDate)).inDays}');
    if (minDays != null &&
        DateTime.now()
                .difference(DateTime.fromMillisecondsSinceEpoch(_installDate))
                .inDays <
            minDays) {
      return false;
    }

    _numElapsed = _prefsService.getInt(kPrefKeyNumTimerElapsed) ?? 0;
    log.d('numElapsed: ${_numElapsed}');
    if (minElapsed != null && _numElapsed < minElapsed) {
      return false;
    }

    return true;
  }

  Future<void> requestReview() async {
    log.d('requestReview()');

    await _prefsService.setBool(kPrefKeyAskForReview, false);
    if (await _inAppReview.isAvailable()) {
      await _inAppReview.requestReview();
    }
  }

  Future<void> openStoreListing() => _inAppReview.openStoreListing();
}
