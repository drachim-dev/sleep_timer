import 'package:flutter/material.dart';

const String kAppTitle = String.fromEnvironment('APP_NAME');

const double kVerticalPaddingSmall = kVerticalPadding / 2;
const double kVerticalPadding = 16;
const double kVerticalPaddingBig = kVerticalPadding * 2;
const double kFloatingActionButtonHeight = 72;
const double kHorizontalPaddingSmall = kHorizontalPadding / 2;
const double kHorizontalPadding = 16;
const double kPreferenceTitleLeftPadding = 16;
const double kBottomSheetPadding = 24;

const String kProductRemoveAds = 'remove_ads';
const String kProductDonation = 'donate';
const Set<String> kProducts = {kProductRemoveAds, kProductDonation};

const String kPrefKeyTheme = 'pref_key_theme';
const String kPrefKeyGlow = 'pref_key_glow';
const String kPrefKeyBridgeAccess = 'pref_key_bridge_access';
const String kKeyVolumeLevel = 'pref_key_volume_level';
const String kPrefKeyInitialTime = 'pref_key_initial_time';

// deprecated
const String kPrefKeyAskForReviewOld = 'pref_key_ask_for_review';

const String kPrefKeyAskForReview = 'pref_key_ask_for_review_home';
const String kPrefKeyFirstLaunch = 'pref_key_first_launch';
const String kPrefKeyNumTimerElapsed = 'pref_key_num_launches';
const String kPrefKeyInstallDate = 'pref_key_install_date';
const String kPrefKeyDefaultExtendTimeByShake =
    'pref_key_default_extend_time_by_shake';
const String kPrefKeyShowTapHintForStartActions =
    'key_show_tap_hint_for_start_actions';
const String kPrefKeyShowLongPressHintForStartActions =
    'key_show_long_press_hint_for_start_actions';

const String kHueBridgeUsername = String.fromEnvironment('APP_NAME');
const String kPrefKeyHueBridges = 'pref_key_hue_bridges';

const String kThemeKeyLightOrange = 'light_orange_theme';
const String kThemeKeyLightGreen = 'light_green_theme';
const String kThemeKeyDarkOrange = 'dark_orange_theme';
const String kThemeKeyDarkYellow = 'dark_yellow_theme';
const String kThemeKeyBlackBlue = 'black_blue_theme';

const int kDefaultInitialTime = 15;
const bool kDefaultGlow = true;
const int kStartTimerDelay = 1500;
const double kAdHeight = 90;
const List<int> kExtendTimeByShakeOptions = [15, 30, 45, 60];
const int kDefaultExtendTimeByShake = 15;

const Color kStatusBarOverlay = Colors.transparent;
