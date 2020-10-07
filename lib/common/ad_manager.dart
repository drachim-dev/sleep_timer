import 'dart:io';

import 'package:flutter_config/flutter_config.dart';

class AdManager {
  static String get appId {
    if (Platform.isAndroid) {
      return FlutterConfig.get("ADMOB_APP_ID");
    } else {
      throw new UnsupportedError("Unsupported platform");
    }
  }

  static String get bannerAdUnitId {
    if (Platform.isAndroid) {
      return FlutterConfig.get("ADMOB_BANNER_AD_ID");
    } else {
      throw new UnsupportedError("Unsupported platform");
    }
  }

  static String get nativeAdUnitId {
    if (Platform.isAndroid) {
      return FlutterConfig.get("ADMOB_NATIVE_AD_ID");
    } else {
      throw new UnsupportedError("Unsupported platform");
    }
  }

  static List<String> get testDeviceId {
    if (Platform.isAndroid) {
      final List<String> testIds = [];
      testIds.add(FlutterConfig.get("ADMOB_TEST_DEVICE_ID_1"));
      testIds.add(FlutterConfig.get("ADMOB_TEST_DEVICE_ID_2"));
      return testIds;
    } else {
      throw new UnsupportedError("Unsupported platform");
    }
  }
}
