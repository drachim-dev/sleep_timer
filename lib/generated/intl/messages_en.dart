// DO NOT EDIT. This is code generated via package:intl/generate_localized.dart
// This is a library that provides messages for a en locale. All the
// messages from the main program should be duplicated here with the same
// function name.

// Ignore issues from commonly used lints in this file.
// ignore_for_file:unnecessary_brace_in_string_interps, unnecessary_new
// ignore_for_file:prefer_single_quotes,comment_references, directives_ordering
// ignore_for_file:annotate_overrides,prefer_generic_function_type_aliases
// ignore_for_file:unused_import, file_names

import 'package:intl/intl.dart';
import 'package:intl/message_lookup_by_library.dart';

final messages = new MessageLookup();

typedef String MessageIfAbsent(String messageStr, List<dynamic> args);

class MessageLookup extends MessageLookupByLibrary {
  String get localeName => 'en';

  static m0(time) => "${time} expired. ";

  static m1(timeLeft) => "Time left: ${timeLeft}";

  static m2(time) => "Timer set to ${time} minutes";

  static m3(minutes) => "${minutes} minutes";

  static m4(minutes) => "${minutes} min";

  static m5(durationString) => "${durationString} minutes";

  final messages = _notInlinedMessages(_notInlinedMessages);
  static _notInlinedMessages(_) => <String, Function> {
    "IntroMediaSubtitle" : MessageLookupByLibrary.simpleMessage("Do you like to hear music or watch a movie while falling asleep?"),
    "actionDoNotDisturbDescription" : MessageLookupByLibrary.simpleMessage("Enable do not disturb"),
    "actionDoNotDisturbTitle" : MessageLookupByLibrary.simpleMessage("Do not disturb"),
    "actionPlayMusicTitle" : MessageLookupByLibrary.simpleMessage("Play music"),
    "actionToggleBluetoothDescription" : MessageLookupByLibrary.simpleMessage("Disable bluetooth"),
    "actionToggleBluetoothTitle" : MessageLookupByLibrary.simpleMessage("Bluetooth"),
    "actionToggleMediaDescription" : MessageLookupByLibrary.simpleMessage("Stop media playback"),
    "actionToggleMediaTitle" : MessageLookupByLibrary.simpleMessage("Media"),
    "actionToggleScreenDescription" : MessageLookupByLibrary.simpleMessage("Turn screen off"),
    "actionToggleScreenTitle" : MessageLookupByLibrary.simpleMessage("Screen"),
    "actionToggleWifiDescription" : MessageLookupByLibrary.simpleMessage("Disable wifi"),
    "actionToggleWifiTitle" : MessageLookupByLibrary.simpleMessage("Wifi"),
    "actionVolumeDescription" : MessageLookupByLibrary.simpleMessage("Set media volume to"),
    "actionVolumeTitle" : MessageLookupByLibrary.simpleMessage("Volume"),
    "adLoadFailure" : MessageLookupByLibrary.simpleMessage("Failed to load the ad"),
    "advancedSectionTitle" : MessageLookupByLibrary.simpleMessage("Advanced"),
    "alreadyPurchased" : MessageLookupByLibrary.simpleMessage("Already\npurchased"),
    "appearanceSectionTitle" : MessageLookupByLibrary.simpleMessage("Appearance"),
    "buttonOpenSavedTimer" : MessageLookupByLibrary.simpleMessage("Tap here to see your timer"),
    "buttonSetupTimer" : MessageLookupByLibrary.simpleMessage("Set your routine"),
    "buttonShowAlarmApps" : MessageLookupByLibrary.simpleMessage("Alarm"),
    "buttonShowPlayerApps" : MessageLookupByLibrary.simpleMessage("Player"),
    "buttonTimerContinue" : MessageLookupByLibrary.simpleMessage("Continue"),
    "buttonTimerPause" : MessageLookupByLibrary.simpleMessage("Pause"),
    "buttonTimerStart" : MessageLookupByLibrary.simpleMessage("Start timer"),
    "chooseThemeTitle" : MessageLookupByLibrary.simpleMessage("Theme"),
    "creditsAppTitle" : MessageLookupByLibrary.simpleMessage("Credits"),
    "creditsLibraries" : MessageLookupByLibrary.simpleMessage("Libraries"),
    "extendTimeByShakeMenuToolTip" : MessageLookupByLibrary.simpleMessage("Tap to change time"),
    "faqShort" : MessageLookupByLibrary.simpleMessage("FAQ"),
    "faqTitle" : MessageLookupByLibrary.simpleMessage("Frequently asked questions"),
    "introAutomateSleepRoutineSubtitle" : MessageLookupByLibrary.simpleMessage("Are you tired of adjusting the same settings every night in order to sleep well?"),
    "introAutomateSleepRoutineTitle" : MessageLookupByLibrary.simpleMessage("Automate your daily sleep routine"),
    "introButtonDone" : MessageLookupByLibrary.simpleMessage("DONE"),
    "introButtonNext" : MessageLookupByLibrary.simpleMessage("Next"),
    "introButtonSkip" : MessageLookupByLibrary.simpleMessage("SKIP"),
    "introGoodNightSubtitle" : MessageLookupByLibrary.simpleMessage("Just set the timer and you\'re done.\nRelax and let your dreams come true ..."),
    "introGoodNightTitle" : MessageLookupByLibrary.simpleMessage("Have a good night"),
    "introMediaTitle" : MessageLookupByLibrary.simpleMessage("Listen to your favorite music"),
    "introNoInterruptionsSubtitle" : MessageLookupByLibrary.simpleMessage("Don\'t get distracted by incoming messages or notifications"),
    "introNoInterruptionsTitle" : MessageLookupByLibrary.simpleMessage("No interruptions"),
    "notificationActionCancel" : MessageLookupByLibrary.simpleMessage("Cancel"),
    "notificationActionContinue" : MessageLookupByLibrary.simpleMessage("Continue"),
    "notificationActionPause" : MessageLookupByLibrary.simpleMessage("Pause"),
    "notificationActionRestart" : MessageLookupByLibrary.simpleMessage("Restart"),
    "notificationNoActionsExecuted" : MessageLookupByLibrary.simpleMessage("No actions selected for execution."),
    "notificationStatusElapsed" : MessageLookupByLibrary.simpleMessage("Sleep Timer elapsed"),
    "notificationStatusPausing" : MessageLookupByLibrary.simpleMessage("Sleep Timer pausing"),
    "notificationStatusRunning" : MessageLookupByLibrary.simpleMessage("Sleep Timer running"),
    "notificationTimeExpired" : m0,
    "notificationTimeLeft" : m1,
    "notificationTimerSet" : m2,
    "numberOfMinutesLong" : m3,
    "numberOfMinutesShort" : m4,
    "otherSectionTitle" : MessageLookupByLibrary.simpleMessage("Other"),
    "prefsDeviceAdmin" : MessageLookupByLibrary.simpleMessage("Device admin"),
    "prefsDeviceAdminDescription" : MessageLookupByLibrary.simpleMessage("Allows app to manage device functions\nEnables screen off action"),
    "prefsExtendTimeOnShake" : MessageLookupByLibrary.simpleMessage("Extend on phone shake"),
    "prefsHintEnableAccessToNotificationSettings" : MessageLookupByLibrary.simpleMessage("Please enable access to notification settings"),
    "prefsHintEnableDeviceAdmin" : MessageLookupByLibrary.simpleMessage("Please enable device admin"),
    "prefsNotificationSettingsAccess" : MessageLookupByLibrary.simpleMessage("Notification Settings Access"),
    "prefsNotificationSettingsAccessDescription" : MessageLookupByLibrary.simpleMessage("Allows access to notification settings\nEnables do not disturb action"),
    "purchasesSectionTitle" : MessageLookupByLibrary.simpleMessage("Support me"),
    "quickLaunchTitle" : MessageLookupByLibrary.simpleMessage("Quick launch"),
    "setVolumeTitle" : MessageLookupByLibrary.simpleMessage("Set volume"),
    "settings" : MessageLookupByLibrary.simpleMessage("Settings"),
    "showTimerGlow" : MessageLookupByLibrary.simpleMessage("Timer glow effect"),
    "showTimerGlowDescription" : MessageLookupByLibrary.simpleMessage("Shows a glow effect around the timers progress bar"),
    "sleepTimer" : MessageLookupByLibrary.simpleMessage("Sleep Timer"),
    "timerEndsActionsTitle" : MessageLookupByLibrary.simpleMessage("When time is up"),
    "timerNoAlarm" : MessageLookupByLibrary.simpleMessage("The timer doesn\'t work sometimes"),
    "timerNoAlarmDescription" : MessageLookupByLibrary.simpleMessage("Make sure that battery optimization is disabled for the app. Some device manufacturers such as Samsung or Huawei require additional settings to allow the app to be running in background."),
    "timerNoWifi" : MessageLookupByLibrary.simpleMessage("Wifi option is missing"),
    "timerNoWifiDescription" : MessageLookupByLibrary.simpleMessage("Starting with Android 10 it is not possible to disable wifi anymore."),
    "timerSettingsSectionTitle" : MessageLookupByLibrary.simpleMessage("Timer settings"),
    "timerStartsActionsTitle" : MessageLookupByLibrary.simpleMessage("When timer starts"),
    "unitMinute" : m5
  };
}
