import 'package:flutter/widgets.dart';
import 'package:injectable/injectable.dart' show Environment;
import 'package:shared_preferences/shared_preferences.dart';
import 'package:sleep_timer/app/locator.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/services/review_service.dart';
import 'package:test/test.dart';

void main() {
  group('ReviewService Test | ', () {
    setUpAll(() {
      WidgetsFlutterBinding.ensureInitialized();
      SharedPreferences.setMockInitialValues({});
      configureInjection(Environment.test);
    });

    test('Dont ask user for review at first launch', () {
      final reviewService = locator<ReviewService>();

      // test data

      // mocking

      // action
      final shouldAsk = reviewService.shouldAskForReview();

      // assertion
      expect(shouldAsk, false);
    });

    test('Dont ask user for review before min days', () {
      final prefsService = locator<SharedPreferences>();
      final reviewService = locator<ReviewService>();

      // test data
      final date = DateTime.now()
          .subtract(Duration(days: reviewService.dayInterval - 1));
      final minElapsed = reviewService.minElapsed;
      final alreadyAsked = 0;

      // mocking
      prefsService.setInt(
          kPrefKeyReviewCalledDate, date.millisecondsSinceEpoch);
      prefsService.setInt(kPrefKeyNumTimerElapsed, minElapsed);
      prefsService.setInt(kPrefKeyReviewCount, alreadyAsked);

      // action
      final shouldAsk = reviewService.shouldAskForReview();

      // assertion
      expect(shouldAsk, false);
    });

    test('Ask user for review after min days', () {
      final prefsService = locator<SharedPreferences>();
      final reviewService = locator<ReviewService>();

      // test data
      final date =
          DateTime.now().subtract(Duration(days: reviewService.dayInterval));
      final minElapsed = reviewService.minElapsed;
      final alreadyAsked = 0;

      // mocking
      prefsService.setInt(
          kPrefKeyReviewCalledDate, date.millisecondsSinceEpoch);
      prefsService.setInt(kPrefKeyNumTimerElapsed, minElapsed);
      prefsService.setInt(kPrefKeyReviewCount, alreadyAsked);

      // action
      final shouldAsk = reviewService.shouldAskForReview();

      // assertion
      expect(shouldAsk, true);
    });

    test('Dont ask user for review before min elapsed', () {
      final prefsService = locator<SharedPreferences>();
      final reviewService = locator<ReviewService>();

      // test data
      final date =
          DateTime.now().subtract(Duration(days: reviewService.dayInterval));
      final minElapsed = reviewService.minElapsed - 1;
      final alreadyAsked = 0;

      // mocking
      prefsService.setInt(
          kPrefKeyReviewCalledDate, date.millisecondsSinceEpoch);
      prefsService.setInt(kPrefKeyNumTimerElapsed, minElapsed);
      prefsService.setInt(kPrefKeyReviewCount, alreadyAsked);

      // action
      final shouldAsk = reviewService.shouldAskForReview();

      // assertion
      expect(shouldAsk, false);
    });

    test('Ask user for review after min elapsed', () {
      final prefsService = locator<SharedPreferences>();
      final reviewService = locator<ReviewService>();

      // test data
      final date =
          DateTime.now().subtract(Duration(days: reviewService.dayInterval));
      final minElapsed = reviewService.minElapsed;
      final alreadyAsked = 0;

      // mocking
      prefsService.setInt(
          kPrefKeyReviewCalledDate, date.millisecondsSinceEpoch);
      prefsService.setInt(kPrefKeyNumTimerElapsed, minElapsed);
      prefsService.setInt(kPrefKeyReviewCount, alreadyAsked);

      // action
      final shouldAsk = reviewService.shouldAskForReview();

      // assertion
      expect(shouldAsk, true);
    });

    test('Dont ask user more than x times', () {
      final prefsService = locator<SharedPreferences>();
      final reviewService = locator<ReviewService>();

      // test data
      final date =
          DateTime.now().subtract(Duration(days: reviewService.dayInterval));
      final minElapsed = reviewService.minElapsed;
      final alreadyAsked = reviewService.maxAskForReview;

      // mocking
      prefsService.setInt(
          kPrefKeyReviewCalledDate, date.millisecondsSinceEpoch);
      prefsService.setInt(kPrefKeyNumTimerElapsed, minElapsed);
      prefsService.setInt(kPrefKeyReviewCount, alreadyAsked);

      // action
      final shouldAsk = reviewService.shouldAskForReview();

      // assertion
      expect(shouldAsk, false);
    });

    test('Ask user if not asked more than x times', () {
      final prefsService = locator<SharedPreferences>();
      final reviewService = locator<ReviewService>();

      // test data
      final date =
          DateTime.now().subtract(Duration(days: reviewService.dayInterval));
      final minElapsed = reviewService.minElapsed;
      final alreadyAsked = reviewService.maxAskForReview - 2;

      // mocking
      prefsService.setInt(
          kPrefKeyReviewCalledDate, date.millisecondsSinceEpoch);
      prefsService.setInt(kPrefKeyNumTimerElapsed, minElapsed);
      prefsService.setInt(kPrefKeyReviewCount, alreadyAsked);

      // action
      final shouldAsk = reviewService.shouldAskForReview();

      // assertion
      expect(shouldAsk, true);
    });

    test('Ask user second time after dayInterval days if all conditions are met', () {
      final prefsService = locator<SharedPreferences>();
      final reviewService = locator<ReviewService>();

      // test data
      final date =
          DateTime.now().subtract(Duration(days: reviewService.dayInterval));
      final minElapsed = 10;
      final alreadyAsked = 1;

      // mocking
      prefsService.setInt(
          kPrefKeyReviewCalledDate, date.millisecondsSinceEpoch);
      prefsService.setInt(kPrefKeyNumTimerElapsed, minElapsed);
      prefsService.setInt(kPrefKeyReviewCount, alreadyAsked);

      // action
      final shouldAsk = reviewService.shouldAskForReview();

      // assertion
      expect(shouldAsk, true);
    });
  });
}
