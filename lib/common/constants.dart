import 'package:flutter/services.dart';

const double kVerticalPadding = 16;
const double kHorizontalPadding = 16;

const String kPrefKeyTheme = 'pref_theme';

const MethodChannel kMethodChannel =
    const MethodChannel('dr.achim/sleep_timer');

const int kNotificationId = 0;
const int kAlarmId = 0;

const String kProductRemoveAds = "remove_ads";
const String kProductDonation = "donate";
const Set<String> kProducts = {kProductRemoveAds, kProductDonation};
