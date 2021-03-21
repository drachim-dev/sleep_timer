import 'package:google_mobile_ads/google_mobile_ads.dart';

class AdManager {
  static String nativeAdUnitId = String.fromEnvironment(
    'ADMOB_NATIVE_AD_ID',
    defaultValue: NativeAd.testAdUnitId,
  );

  static const testDevice1 = String.fromEnvironment('ADMOB_TEST_DEVICE_ID_1');
  static const testDevice2 = String.fromEnvironment('ADMOB_TEST_DEVICE_ID_2');

  static List<String> get testDeviceId => [testDevice1, testDevice2];
}
