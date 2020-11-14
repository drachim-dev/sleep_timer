import 'package:flutter/material.dart';

const double kVerticalPaddingSmall = kVerticalPadding / 2;
const double kVerticalPadding = 16;
const double kVerticalPaddingBig = kVerticalPadding * 2;
const double kHorizontalPaddingSmall = kHorizontalPadding / 2;
const double kHorizontalPadding = 16;
const double kPreferenceTitleLeftPadding = 16;
const double kBottomSheetPadding = 24;

const String kProductRemoveAds = "remove_ads";
const String kProductDonation = "donate";
const Set<String> kProducts = {kProductRemoveAds, kProductDonation};

const String kPrefKeyTheme = "pref_key_theme";
const String kPrefKeyGlow = "pref_key_glow";
const String kKeyVolumeLevel = "pref_key_volume_level";
const String kPrefKeyInitialTime = "pref_key_initial_time";
const String kPrefKeyFirstLaunch = "pref_key_first_launch";
const String kSpotifyCredentials = "pref_key_spotify_credentials";

const String kThemeKeyLightOrange = "light_orange_theme";
const String kThemeKeyLightGreen = "light_green_theme";
const String kThemeKeyDarkOrange = "dark_orange_theme";
const String kThemeKeyDarkYellow = "dark_yellow_theme";
const String kThemeKeyBlackBlue = "black_blue_theme";

const int kDefaultInitialTime = 15;
const bool kDefaultGlow = true;
const int kStartTimerDelay = 1500;
const double kAdHeight = 80;

const Color kStatusBarOverlay = Colors.transparent;
