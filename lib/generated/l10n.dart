// GENERATED CODE - DO NOT MODIFY BY HAND
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'intl/messages_all.dart';

// **************************************************************************
// Generator: Flutter Intl IDE plugin
// Made by Localizely
// **************************************************************************

// ignore_for_file: non_constant_identifier_names, lines_longer_than_80_chars
// ignore_for_file: join_return_with_assignment, prefer_final_in_for_each
// ignore_for_file: avoid_redundant_argument_values

class S {
  S();
  
  static S current;
  
  static const AppLocalizationDelegate delegate =
    AppLocalizationDelegate();

  static Future<S> load(Locale locale) {
    final name = (locale.countryCode?.isEmpty ?? false) ? locale.languageCode : locale.toString();
    final localeName = Intl.canonicalizedLocale(name); 
    return initializeMessages(localeName).then((_) {
      Intl.defaultLocale = localeName;
      S.current = S();
      
      return S.current;
    });
  } 

  static S of(BuildContext context) {
    return Localizations.of<S>(context, S);
  }

  /// `Automate your daily sleep routine`
  String get introAutomateSleepRoutineTitle {
    return Intl.message(
      'Automate your daily sleep routine',
      name: 'introAutomateSleepRoutineTitle',
      desc: '',
      args: [],
    );
  }

  /// `Are you tired of adjusting the same settings every night in order to sleep well?`
  String get introAutomateSleepRoutineSubtitle {
    return Intl.message(
      'Are you tired of adjusting the same settings every night in order to sleep well?',
      name: 'introAutomateSleepRoutineSubtitle',
      desc: '',
      args: [],
    );
  }

  /// `No interruptions`
  String get introNoInterruptionsTitle {
    return Intl.message(
      'No interruptions',
      name: 'introNoInterruptionsTitle',
      desc: '',
      args: [],
    );
  }

  /// `Don't get distracted by incoming messages or notifications`
  String get introNoInterruptionsSubtitle {
    return Intl.message(
      'Don\'t get distracted by incoming messages or notifications',
      name: 'introNoInterruptionsSubtitle',
      desc: '',
      args: [],
    );
  }

  /// `Listen to your favorite music`
  String get introMediaTitle {
    return Intl.message(
      'Listen to your favorite music',
      name: 'introMediaTitle',
      desc: '',
      args: [],
    );
  }

  /// `Do you like to hear music or watch a movie while falling asleep?`
  String get IntroMediaSubtitle {
    return Intl.message(
      'Do you like to hear music or watch a movie while falling asleep?',
      name: 'IntroMediaSubtitle',
      desc: '',
      args: [],
    );
  }

  /// `Have a good night`
  String get introGoodNightTitle {
    return Intl.message(
      'Have a good night',
      name: 'introGoodNightTitle',
      desc: '',
      args: [],
    );
  }

  /// `Just set the timer and you're done.\nRelax and let your dreams come true ...`
  String get introGoodNightSubtitle {
    return Intl.message(
      'Just set the timer and you\'re done.\nRelax and let your dreams come true ...',
      name: 'introGoodNightSubtitle',
      desc: '',
      args: [],
    );
  }

  /// `SKIP`
  String get introButtonSkip {
    return Intl.message(
      'SKIP',
      name: 'introButtonSkip',
      desc: '',
      args: [],
    );
  }

  /// `DONE`
  String get introButtonDone {
    return Intl.message(
      'DONE',
      name: 'introButtonDone',
      desc: '',
      args: [],
    );
  }

  /// `Next`
  String get introButtonNext {
    return Intl.message(
      'Next',
      name: 'introButtonNext',
      desc: '',
      args: [],
    );
  }

  /// `Sleep Timer`
  String get sleepTimer {
    return Intl.message(
      'Sleep Timer',
      name: 'sleepTimer',
      desc: '',
      args: [],
    );
  }

  /// `Settings`
  String get settings {
    return Intl.message(
      'Settings',
      name: 'settings',
      desc: '',
      args: [],
    );
  }

  /// `{minutes} Min`
  String numberOfMinutesShort(Object minutes) {
    return Intl.message(
      '$minutes Min',
      name: 'numberOfMinutesShort',
      desc: '',
      args: [minutes],
    );
  }

  /// `Tap here to see your timer`
  String get buttonOpenSavedTimer {
    return Intl.message(
      'Tap here to see your timer',
      name: 'buttonOpenSavedTimer',
      desc: '',
      args: [],
    );
  }

  /// `Set your routine`
  String get buttonSetupTimer {
    return Intl.message(
      'Set your routine',
      name: 'buttonSetupTimer',
      desc: '',
      args: [],
    );
  }

  /// `Quick launch`
  String get quickLaunchTitle {
    return Intl.message(
      'Quick launch',
      name: 'quickLaunchTitle',
      desc: '',
      args: [],
    );
  }

  /// `When timer starts`
  String get timerStartsActionsTitle {
    return Intl.message(
      'When timer starts',
      name: 'timerStartsActionsTitle',
      desc: '',
      args: [],
    );
  }

  /// `When time is up`
  String get timerEndsActionsTitle {
    return Intl.message(
      'When time is up',
      name: 'timerEndsActionsTitle',
      desc: '',
      args: [],
    );
  }

  /// `Player`
  String get buttonShowPlayerApps {
    return Intl.message(
      'Player',
      name: 'buttonShowPlayerApps',
      desc: '',
      args: [],
    );
  }

  /// `Alarm`
  String get buttonShowAlarmApps {
    return Intl.message(
      'Alarm',
      name: 'buttonShowAlarmApps',
      desc: '',
      args: [],
    );
  }

  /// `Set volume`
  String get setVolumeTitle {
    return Intl.message(
      'Set volume',
      name: 'setVolumeTitle',
      desc: '',
      args: [],
    );
  }

  /// `Pause`
  String get buttonTimerPause {
    return Intl.message(
      'Pause',
      name: 'buttonTimerPause',
      desc: '',
      args: [],
    );
  }

  /// `Start timer`
  String get buttonTimerStart {
    return Intl.message(
      'Start timer',
      name: 'buttonTimerStart',
      desc: '',
      args: [],
    );
  }

  /// `Continue`
  String get buttonTimerContinue {
    return Intl.message(
      'Continue',
      name: 'buttonTimerContinue',
      desc: '',
      args: [],
    );
  }

  /// `Frequently asked questions`
  String get faqTitle {
    return Intl.message(
      'Frequently asked questions',
      name: 'faqTitle',
      desc: '',
      args: [],
    );
  }

  /// `The timer doesn't work sometimes`
  String get timerNoAlarm {
    return Intl.message(
      'The timer doesn\'t work sometimes',
      name: 'timerNoAlarm',
      desc: '',
      args: [],
    );
  }

  /// `Make sure that battery optimization is disabled for the app. Some device manufacturers such as Samsung or Huawei require additional settings to allow the app to be running in background.`
  String get timerNoAlarmDescription {
    return Intl.message(
      'Make sure that battery optimization is disabled for the app. Some device manufacturers such as Samsung or Huawei require additional settings to allow the app to be running in background.',
      name: 'timerNoAlarmDescription',
      desc: '',
      args: [],
    );
  }

  /// `Wifi option is missing`
  String get timerNoWifi {
    return Intl.message(
      'Wifi option is missing',
      name: 'timerNoWifi',
      desc: '',
      args: [],
    );
  }

  /// `Starting with Android 10 it is not possible to disable wifi anymore.`
  String get timerNoWifiDescription {
    return Intl.message(
      'Starting with Android 10 it is not possible to disable wifi anymore.',
      name: 'timerNoWifiDescription',
      desc: '',
      args: [],
    );
  }

  /// `Credits`
  String get creditsAppTitle {
    return Intl.message(
      'Credits',
      name: 'creditsAppTitle',
      desc: '',
      args: [],
    );
  }

  /// `Appearance`
  String get appearanceSectionTitle {
    return Intl.message(
      'Appearance',
      name: 'appearanceSectionTitle',
      desc: '',
      args: [],
    );
  }

  /// `Support me`
  String get purchasesSectionTitle {
    return Intl.message(
      'Support me',
      name: 'purchasesSectionTitle',
      desc: '',
      args: [],
    );
  }

  /// `Advanced`
  String get advancedSectionTitle {
    return Intl.message(
      'Advanced',
      name: 'advancedSectionTitle',
      desc: '',
      args: [],
    );
  }

  /// `Other`
  String get otherSectionTitle {
    return Intl.message(
      'Other',
      name: 'otherSectionTitle',
      desc: '',
      args: [],
    );
  }

  /// `FAQ`
  String get faqShort {
    return Intl.message(
      'FAQ',
      name: 'faqShort',
      desc: '',
      args: [],
    );
  }

  /// `Already\npurchased`
  String get alreadyPurchased {
    return Intl.message(
      'Already\npurchased',
      name: 'alreadyPurchased',
      desc: '',
      args: [],
    );
  }

  /// `Theme`
  String get chooseThemeTitle {
    return Intl.message(
      'Theme',
      name: 'chooseThemeTitle',
      desc: '',
      args: [],
    );
  }

  /// `Timer glow effect`
  String get showTimerGlow {
    return Intl.message(
      'Timer glow effect',
      name: 'showTimerGlow',
      desc: '',
      args: [],
    );
  }

  /// `Shows a glow effect around the timers progress bar`
  String get showTimerGlowDescription {
    return Intl.message(
      'Shows a glow effect around the timers progress bar',
      name: 'showTimerGlowDescription',
      desc: '',
      args: [],
    );
  }

  /// `Device admin`
  String get prefsDeviceAdmin {
    return Intl.message(
      'Device admin',
      name: 'prefsDeviceAdmin',
      desc: '',
      args: [],
    );
  }

  /// `Allows app to manage device functions\nEnables screen off action`
  String get prefsDeviceAdminDescription {
    return Intl.message(
      'Allows app to manage device functions\nEnables screen off action',
      name: 'prefsDeviceAdminDescription',
      desc: '',
      args: [],
    );
  }

  /// `Notification Settings Access`
  String get prefsNotificationSettingsAccess {
    return Intl.message(
      'Notification Settings Access',
      name: 'prefsNotificationSettingsAccess',
      desc: '',
      args: [],
    );
  }

  /// `Allows access to notification settings\nEnables do not disturb action`
  String get prefsNotificationSettingsAccessDescription {
    return Intl.message(
      'Allows access to notification settings\nEnables do not disturb action',
      name: 'prefsNotificationSettingsAccessDescription',
      desc: '',
      args: [],
    );
  }

  /// `Please enable device admin`
  String get prefsHintEnableDeviceAdmin {
    return Intl.message(
      'Please enable device admin',
      name: 'prefsHintEnableDeviceAdmin',
      desc: '',
      args: [],
    );
  }

  /// `Please enable access to notification settings`
  String get prefsHintEnableAccessToNotificationSettings {
    return Intl.message(
      'Please enable access to notification settings',
      name: 'prefsHintEnableAccessToNotificationSettings',
      desc: '',
      args: [],
    );
  }

  /// `Failed to load the ad`
  String get adLoadFailure {
    return Intl.message(
      'Failed to load the ad',
      name: 'adLoadFailure',
      desc: '',
      args: [],
    );
  }

  /// `Libraries`
  String get creditsLibraries {
    return Intl.message(
      'Libraries',
      name: 'creditsLibraries',
      desc: '',
      args: [],
    );
  }

  /// `Volume`
  String get actionVolumeTitle {
    return Intl.message(
      'Volume',
      name: 'actionVolumeTitle',
      desc: '',
      args: [],
    );
  }

  /// `Set media volume to`
  String get actionVolumeDescription {
    return Intl.message(
      'Set media volume to',
      name: 'actionVolumeDescription',
      desc: '',
      args: [],
    );
  }

  /// `Play music`
  String get actionPlayMusicTitle {
    return Intl.message(
      'Play music',
      name: 'actionPlayMusicTitle',
      desc: '',
      args: [],
    );
  }

  /// `Do not disturb`
  String get actionDoNotDisturbTitle {
    return Intl.message(
      'Do not disturb',
      name: 'actionDoNotDisturbTitle',
      desc: '',
      args: [],
    );
  }

  /// `Enable do not disturb`
  String get actionDoNotDisturbDescription {
    return Intl.message(
      'Enable do not disturb',
      name: 'actionDoNotDisturbDescription',
      desc: '',
      args: [],
    );
  }

  /// `Media`
  String get actionToggleMediaTitle {
    return Intl.message(
      'Media',
      name: 'actionToggleMediaTitle',
      desc: '',
      args: [],
    );
  }

  /// `Stop media playback`
  String get actionToggleMediaDescription {
    return Intl.message(
      'Stop media playback',
      name: 'actionToggleMediaDescription',
      desc: '',
      args: [],
    );
  }

  /// `Wifi`
  String get actionToggleWifiTitle {
    return Intl.message(
      'Wifi',
      name: 'actionToggleWifiTitle',
      desc: '',
      args: [],
    );
  }

  /// `Disable wifi`
  String get actionToggleWifiDescription {
    return Intl.message(
      'Disable wifi',
      name: 'actionToggleWifiDescription',
      desc: '',
      args: [],
    );
  }

  /// `Bluetooth`
  String get actionToggleBluetoothTitle {
    return Intl.message(
      'Bluetooth',
      name: 'actionToggleBluetoothTitle',
      desc: '',
      args: [],
    );
  }

  /// `Disable bluetooth`
  String get actionToggleBluetoothDescription {
    return Intl.message(
      'Disable bluetooth',
      name: 'actionToggleBluetoothDescription',
      desc: '',
      args: [],
    );
  }

  /// `Screen`
  String get actionToggleScreenTitle {
    return Intl.message(
      'Screen',
      name: 'actionToggleScreenTitle',
      desc: '',
      args: [],
    );
  }

  /// `Turn screen off`
  String get actionToggleScreenDescription {
    return Intl.message(
      'Turn screen off',
      name: 'actionToggleScreenDescription',
      desc: '',
      args: [],
    );
  }

  /// `{durationString} minutes`
  String unitMinute(Object durationString) {
    return Intl.message(
      '$durationString minutes',
      name: 'unitMinute',
      desc: '',
      args: [durationString],
    );
  }

  /// `Sleep Timer running`
  String get notificationStatusRunning {
    return Intl.message(
      'Sleep Timer running',
      name: 'notificationStatusRunning',
      desc: '',
      args: [],
    );
  }

  /// `Pause`
  String get notificationActionPause {
    return Intl.message(
      'Pause',
      name: 'notificationActionPause',
      desc: '',
      args: [],
    );
  }

  /// `Sleep Timer pausing`
  String get notificationStatusPausing {
    return Intl.message(
      'Sleep Timer pausing',
      name: 'notificationStatusPausing',
      desc: '',
      args: [],
    );
  }

  /// `Cancel`
  String get notificationActionCancel {
    return Intl.message(
      'Cancel',
      name: 'notificationActionCancel',
      desc: '',
      args: [],
    );
  }

  /// `Continue`
  String get notificationActionContinue {
    return Intl.message(
      'Continue',
      name: 'notificationActionContinue',
      desc: '',
      args: [],
    );
  }

  /// `Sleep Timer elapsed`
  String get notificationStatusElapsed {
    return Intl.message(
      'Sleep Timer elapsed',
      name: 'notificationStatusElapsed',
      desc: '',
      args: [],
    );
  }

  /// `Restart`
  String get notificationActionRestart {
    return Intl.message(
      'Restart',
      name: 'notificationActionRestart',
      desc: '',
      args: [],
    );
  }

  /// `No actions selected for execution.`
  String get notificationNoActionsExecuted {
    return Intl.message(
      'No actions selected for execution.',
      name: 'notificationNoActionsExecuted',
      desc: '',
      args: [],
    );
  }

  /// `{time} expired. `
  String notificationTimeExpired(Object time) {
    return Intl.message(
      '$time expired. ',
      name: 'notificationTimeExpired',
      desc: '',
      args: [time],
    );
  }

  /// `Time left: {timeLeft}`
  String notificationTimeLeft(Object timeLeft) {
    return Intl.message(
      'Time left: $timeLeft',
      name: 'notificationTimeLeft',
      desc: '',
      args: [timeLeft],
    );
  }

  /// `Timer set to {time} minutes`
  String notificationTimerSet(Object time) {
    return Intl.message(
      'Timer set to $time minutes',
      name: 'notificationTimerSet',
      desc: '',
      args: [time],
    );
  }
}

class AppLocalizationDelegate extends LocalizationsDelegate<S> {
  const AppLocalizationDelegate();

  List<Locale> get supportedLocales {
    return const <Locale>[
      Locale.fromSubtags(languageCode: 'en'),
      Locale.fromSubtags(languageCode: 'de'),
    ];
  }

  @override
  bool isSupported(Locale locale) => _isSupported(locale);
  @override
  Future<S> load(Locale locale) => S.load(locale);
  @override
  bool shouldReload(AppLocalizationDelegate old) => false;

  bool _isSupported(Locale locale) {
    if (locale != null) {
      for (var supportedLocale in supportedLocales) {
        if (supportedLocale.languageCode == locale.languageCode) {
          return true;
        }
      }
    }
    return false;
  }
}