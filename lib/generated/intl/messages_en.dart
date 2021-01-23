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

  static m0(count) => "${Intl.plural(count, one: '1 device found', other: '${count} devices found')}";

  static m1(count) => "${count} lights";

  static m2(time) => "${time} expired. ";

  static m3(timeLeft) => "Time left: ${timeLeft}";

  static m4(time) => "Timer set to ${time} minutes";

  static m5(minutes) => "${minutes} minutes";

  static m6(minutes) => "${minutes} min";

  static m7(durationString) => "${durationString} minutes";

  static m8(bridge) => "Unlink ${bridge}";

  final messages = _notInlinedMessages(_notInlinedMessages);
  static _notInlinedMessages(_) => <String, Function> {
    "IntroMediaSubtitle" : MessageLookupByLibrary.simpleMessage("Do you like to hear music or watch a movie while falling asleep?"),
    "actionDoNotDisturbDescription" : MessageLookupByLibrary.simpleMessage("Enable do not disturb"),
    "actionDoNotDisturbTitle" : MessageLookupByLibrary.simpleMessage("Do not disturb"),
    "actionPlayMusicTitle" : MessageLookupByLibrary.simpleMessage("Play music"),
    "actionToggleBluetoothDescription" : MessageLookupByLibrary.simpleMessage("Disable bluetooth"),
    "actionToggleBluetoothTitle" : MessageLookupByLibrary.simpleMessage("Bluetooth"),
    "actionToggleLightDescription" : MessageLookupByLibrary.simpleMessage("Turn the lights off"),
    "actionToggleLightTitle" : MessageLookupByLibrary.simpleMessage("Light"),
    "actionToggleMediaDescription" : MessageLookupByLibrary.simpleMessage("Stop media playback"),
    "actionToggleMediaTitle" : MessageLookupByLibrary.simpleMessage("Media"),
    "actionToggleScreenDescription" : MessageLookupByLibrary.simpleMessage("Turn screen off"),
    "actionToggleScreenTitle" : MessageLookupByLibrary.simpleMessage("Screen"),
    "actionToggleWifiDescription" : MessageLookupByLibrary.simpleMessage("Disable wifi"),
    "actionToggleWifiTitle" : MessageLookupByLibrary.simpleMessage("Wifi"),
    "actionVolumeTitle" : MessageLookupByLibrary.simpleMessage("Volume"),
    "adLoadFailure" : MessageLookupByLibrary.simpleMessage("Failed to load the ad"),
    "advancedSectionTitle" : MessageLookupByLibrary.simpleMessage("Advanced"),
    "alreadyPurchased" : MessageLookupByLibrary.simpleMessage("Already\npurchased"),
    "appearanceSectionTitle" : MessageLookupByLibrary.simpleMessage("Appearance"),
    "buttonOpenSavedTimer" : MessageLookupByLibrary.simpleMessage("Tap here to see your timer"),
    "buttonSearchAgain" : MessageLookupByLibrary.simpleMessage("Search again"),
    "buttonSetupTimer" : MessageLookupByLibrary.simpleMessage("Set your routine"),
    "buttonShowAlarmApps" : MessageLookupByLibrary.simpleMessage("Alarm"),
    "buttonShowPlayerApps" : MessageLookupByLibrary.simpleMessage("Player"),
    "buttonTimerContinue" : MessageLookupByLibrary.simpleMessage("Resume"),
    "buttonTimerPause" : MessageLookupByLibrary.simpleMessage("Pause"),
    "buttonTimerStart" : MessageLookupByLibrary.simpleMessage("Start timer"),
    "chooseThemeTitle" : MessageLookupByLibrary.simpleMessage("Theme"),
    "connectionStateConnected" : MessageLookupByLibrary.simpleMessage("Connected"),
    "connectionStateFailed" : MessageLookupByLibrary.simpleMessage("Failed"),
    "countDevicesFound" : m0,
    "countLights" : m1,
    "creditsAppTitle" : MessageLookupByLibrary.simpleMessage("Credits"),
    "creditsLibraries" : MessageLookupByLibrary.simpleMessage("Libraries"),
    "dialogCancel" : MessageLookupByLibrary.simpleMessage("CANCEL"),
    "dialogConnect" : MessageLookupByLibrary.simpleMessage("CONNECT"),
    "dialogDone" : MessageLookupByLibrary.simpleMessage("DONE"),
    "dialogUnlink" : MessageLookupByLibrary.simpleMessage("UNLINK"),
    "errorNoConnection" : MessageLookupByLibrary.simpleMessage("Check your connection"),
    "errorNoDevices" : MessageLookupByLibrary.simpleMessage("No devices found"),
    "extendTimeByShakeMenuToolTip" : MessageLookupByLibrary.simpleMessage("Tap to change time"),
    "faqShort" : MessageLookupByLibrary.simpleMessage("FAQ"),
    "faqTitle" : MessageLookupByLibrary.simpleMessage("Frequently asked questions"),
    "hintTurnsOffLightAction" : MessageLookupByLibrary.simpleMessage("This will turn off the light action."),
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
    "linkBridge" : MessageLookupByLibrary.simpleMessage("Link bridge"),
    "linkBridgeInstruction" : MessageLookupByLibrary.simpleMessage("1. Push the button on the bridge\n2. Tap on connect"),
    "longPressToAdjust" : MessageLookupByLibrary.simpleMessage("Long press to adjust settings"),
    "noLightsFound" : MessageLookupByLibrary.simpleMessage("No lights found"),
    "notSupported" : MessageLookupByLibrary.simpleMessage("Not supported by device"),
    "notificationActionCancel" : MessageLookupByLibrary.simpleMessage("Cancel"),
    "notificationActionContinue" : MessageLookupByLibrary.simpleMessage("Continue"),
    "notificationActionPause" : MessageLookupByLibrary.simpleMessage("Pause"),
    "notificationActionRestart" : MessageLookupByLibrary.simpleMessage("Restart"),
    "notificationNoActionsExecuted" : MessageLookupByLibrary.simpleMessage("No actions selected for execution."),
    "notificationStatusElapsed" : MessageLookupByLibrary.simpleMessage("Sleep Timer elapsed"),
    "notificationStatusPausing" : MessageLookupByLibrary.simpleMessage("Sleep Timer pausing"),
    "notificationStatusRunning" : MessageLookupByLibrary.simpleMessage("Sleep Timer running"),
    "notificationTimeExpired" : m2,
    "notificationTimeLeft" : m3,
    "notificationTimerSet" : m4,
    "numberOfMinutesLong" : m5,
    "numberOfMinutesShort" : m6,
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
    "stateSearching" : MessageLookupByLibrary.simpleMessage("Searching ..."),
    "tapToConnect" : MessageLookupByLibrary.simpleMessage("Tap to\nconnect"),
    "timerEndsActionsTitle" : MessageLookupByLibrary.simpleMessage("When time is up"),
    "timerNoAlarm" : MessageLookupByLibrary.simpleMessage("The timer doesn\'t work sometimes"),
    "timerNoAlarmDescription" : MessageLookupByLibrary.simpleMessage("Make sure that battery optimization is disabled for the app. Some device manufacturers such as Samsung or Huawei require additional settings to allow the app to be running in background."),
    "timerNoWifi" : MessageLookupByLibrary.simpleMessage("Wifi option is missing"),
    "timerNoWifiDescription" : MessageLookupByLibrary.simpleMessage("Starting with Android 10 it is not possible to disable wifi anymore."),
    "timerSettingsSectionTitle" : MessageLookupByLibrary.simpleMessage("Timer settings"),
    "timerStartsActionsTitle" : MessageLookupByLibrary.simpleMessage("When timer starts"),
    "titleLightGroups" : MessageLookupByLibrary.simpleMessage("Light groups"),
    "unitMinute" : m7,
    "unlinkBridgeName" : m8
  };
}
