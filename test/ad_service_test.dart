import 'package:flutter/widgets.dart';
import 'package:injectable/injectable.dart' show Environment;
import 'package:shared_preferences/shared_preferences.dart';
import 'package:sleep_timer/app/app.locator.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/services/ad_service.dart';
import 'package:test/test.dart';

void main() {
  group('AdService Test | ', () {
    setUpAll(() {
      WidgetsFlutterBinding.ensureInitialized();
      SharedPreferences.setMockInitialValues({});
      setupLocator(Environment.test);
    });

    test('Dont show ad at first launch', () async {
      final adService = locator<AdService>();

      // test data

      // mocking

      // action
      final shouldAsk = await adService.mayShow();

      // assertion
      expect(shouldAsk, false);
    });

    test('Dont show ad if user bought adFree product', () async {
      final adService = locator<AdService>();

      // test data

      // mocking

      // action
      final shouldAsk = await adService.mayShow();

      // assertion
      expect(shouldAsk, false);
    });

    test(
      'Dont show if user bought adFree product although force is set',
      () {},
    );

    test('Dont show ad when asked for review in the same session', () {});

    test('Show ad if forced', () async {
      final adService = locator<AdService>();

      // test data

      // mocking

      // action
      final shouldAsk = await adService.mayShow(force: true);

      // assertion
      expect(shouldAsk, true);
    });

    test(
      'Begin to count once maxAskedForReview has been reached and not before',
      () async {
        final adService = locator<AdService>();
        final prefsService = locator<SharedPreferences>();

        // test data
        await prefsService.setInt(kPrefKeyAdIntervalCounter, 0);

        // action
        await prefsService.setInt(kPrefKeyReviewCount, 0);
        await adService.mayShow();
        final adCounterWithReviewCount0 = prefsService.getInt(
          kPrefKeyAdIntervalCounter,
        );

        // assertion
        expect(adCounterWithReviewCount0, 0);

        // action
        await prefsService.setInt(kPrefKeyReviewCount, kMaxAskForReview - 1);
        await adService.mayShow();
        final adCounterWithReviewCountMaxMinus1 = prefsService.getInt(
          kPrefKeyAdIntervalCounter,
        );

        // assertion
        expect(adCounterWithReviewCountMaxMinus1, 0);

        // action
        await prefsService.setInt(kPrefKeyReviewCount, kMaxAskForReview);
        await adService.mayShow();
        final adCounterWithReviewCountMax = prefsService.getInt(
          kPrefKeyAdIntervalCounter,
        );

        // assertion
        expect(adCounterWithReviewCountMax, 1);

        // action
        await prefsService.setInt(kPrefKeyReviewCount, kMaxAskForReview + 1);
        await adService.mayShow();
        await adService.mayShow();
        final adCounterWithReviewCountMaxPlus1 = prefsService.getInt(
          kPrefKeyAdIntervalCounter,
        );

        // assertion
        expect(adCounterWithReviewCountMaxPlus1, 3);
      },
    );

    test('Show ad when counter equals interval and stop afterwards ', () async {
      final adService = locator<AdService>();
      final prefsService = locator<SharedPreferences>();

      // test data
      await prefsService.setInt(kPrefKeyReviewCount, kMaxAskForReview);

      // mocking

      // action

      // assertion
      expect(await adService.mayShow(), false);
      expect(await adService.mayShow(), false);
      expect(await adService.mayShow(), false);
      expect(await adService.mayShow(), false);
      expect(await adService.mayShow(), false);
      expect(await adService.mayShow(), false);
      expect(await adService.mayShow(), true);
      expect(await adService.mayShow(), false);
    });
  });
}
