class AdManager {
  static const String appId = String.fromEnvironment('ADMOB_APP_ID');

  static const String bannerAdUnitId =
      String.fromEnvironment('ADMOB_BANNER_AD_ID');

  static const String nativeAdUnitId =
      String.fromEnvironment('ADMOB_NATIVE_AD_ID');

  static const testdDevice1 = String.fromEnvironment('ADMOB_TEST_DEVICE_ID_1');
  static const testdDevice2 = String.fromEnvironment('ADMOB_TEST_DEVICE_ID_2');

  static List<String> get testDeviceId => [testdDevice1, testdDevice2];
}
