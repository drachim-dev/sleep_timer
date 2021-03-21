class AdManager {
  /// Must be 'const' when using String.fromEnvironment
  static const String nativeAdUnitId = String.fromEnvironment(
      'ADMOB_NATIVE_AD_ID',
      defaultValue: 'ca-app-pub-3940256099942544/2247696110');

  static const testDevice1 = String.fromEnvironment('ADMOB_TEST_DEVICE_ID_1');
  static const testDevice2 = String.fromEnvironment('ADMOB_TEST_DEVICE_ID_2');

  static List<String> get testDeviceId => [testDevice1, testDevice2];
}