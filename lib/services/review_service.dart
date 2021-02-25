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
  final int minHoursSinceRun = 6;

  bool askForReview = true;
  int _installDate, _numElapsed;

  ReviewService() {
    _installDate = _prefsService.getInt(kPrefKeyInstallDate);
    if (_installDate == null) {
      _installDate = DateTime.now().millisecondsSinceEpoch;
      _prefsService.setInt(kPrefKeyInstallDate, _installDate);
    }
  }

  bool shouldAskForReview() {
    askForReview = _prefsService.getBool(kPrefKeyAskForReview) ?? askForReview;
    if (!askForReview) {
      return false;
    }

    final installDate = DateTime.fromMillisecondsSinceEpoch(_installDate);
    final daysSinceInstall = DateTime.now().difference(installDate).inDays;

    log.d('daysUsed: ${daysSinceInstall}');
    if (minDays != null && daysSinceInstall < minDays) {
      return false;
    }

    _numElapsed = _prefsService.getInt(kPrefKeyNumTimerElapsed) ?? 0;
    log.d('numElapsed: ${_numElapsed}');
    if (minElapsed != null && _numElapsed < minElapsed) {
      return false;
    }

    final _lastRunStartedInMillis =
        _prefsService.getInt(kPrefKeyLastRunStartedDate) ??
            DateTime.now().millisecondsSinceEpoch;
    final _lastRunStartedDate =
        DateTime.fromMillisecondsSinceEpoch(_lastRunStartedInMillis);
    final hoursSinceRunStarted =
        DateTime.now().difference(_lastRunStartedDate).inHours;

    log.d('hoursSinceRunStarted: ${hoursSinceRunStarted}');
    if (minHoursSinceRun != null && hoursSinceRunStarted < minHoursSinceRun) {
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
