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

  static String get testDeviceId {
    if (Platform.isAndroid) {
      return FlutterConfig.get("ADMOB_TEST_DEVICE_ID");
    } else {
      throw new UnsupportedError("Unsupported platform");
    }
  }
}
