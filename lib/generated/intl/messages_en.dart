// DO NOT EDIT. This is code generated via package:intl/generate_localized.dart
// This is a library that provides messages for a en locale. All the
// messages from the main program should be duplicated here with the same
// function name.

// Ignore issues from commonly used lints in this file.
// ignore_for_file:unnecessary_brace_in_string_interps, unnecessary_new
// ignore_for_file:prefer_single_quotes,comment_references, directives_ordering
// ignore_for_file:annotate_overrides,prefer_generic_function_type_aliases
// ignore_for_file:unused_import, file_names, avoid_escaping_inner_quotes
// ignore_for_file:unnecessary_string_interpolations, unnecessary_string_escapes

import 'package:intl/intl.dart';
import 'package:intl/message_lookup_by_library.dart';

final messages = new MessageLookup();

typedef String MessageIfAbsent(String messageStr, List<dynamic> args);

class MessageLookup extends MessageLookupByLibrary {
  String get localeName => 'en';

  static String m0(minutes) => "by ${minutes} minutes";

  static String m1(count) =>
      "${Intl.plural(count, one: '1 device found', other: '${count} devices found')}";

  static String m2(count) =>
      "${Intl.plural(count, one: '1 light', other: '${count} lights')}";

  static String m3(status) => "Status: ${status}";

  static String m4(minutes) => "${minutes} minutes";

  static String m5(minutes) => "${minutes} min";

  static String m6(action, value) => "${action} to ${value}";

  static String m7(bridge) => "Unlink ${bridge}";

  final messages = _notInlinedMessages(_notInlinedMessages);
  static Map<String, Function> _notInlinedMessages(_) => <String, Function>{
        "actionDoNotDisturbDescription":
            MessageLookupByLibrary.simpleMessage("Do not disturb enabled"),
        "actionDoNotDisturbTitle":
            MessageLookupByLibrary.simpleMessage("Do not disturb"),
        "actionNotSupported":
            MessageLookupByLibrary.simpleMessage("Action not supported"),
        "actionPlayMusicTitle":
            MessageLookupByLibrary.simpleMessage("Play music"),
        "actionToggleBluetoothDescription":
            MessageLookupByLibrary.simpleMessage("Bluetooth disabled"),
        "actionToggleBluetoothTitle":
            MessageLookupByLibrary.simpleMessage("Bluetooth"),
        "actionToggleLightDescription":
            MessageLookupByLibrary.simpleMessage("Lights turned off"),
        "actionToggleLightTitle":
            MessageLookupByLibrary.simpleMessage("Hue lights"),
        "actionToggleMediaDescription":
            MessageLookupByLibrary.simpleMessage("Media paused"),
        "actionToggleMediaTitle": MessageLookupByLibrary.simpleMessage("Media"),
        "actionToggleScreenDescription":
            MessageLookupByLibrary.simpleMessage("Screen off"),
        "actionToggleScreenTitle":
            MessageLookupByLibrary.simpleMessage("Screen"),
        "actionToggleWifiDescription":
            MessageLookupByLibrary.simpleMessage("Wifi disabled"),
        "actionToggleWifiTitle": MessageLookupByLibrary.simpleMessage("Wifi"),
        "actionVolumeTitle": MessageLookupByLibrary.simpleMessage("Volume"),
        "adLoadFailure":
            MessageLookupByLibrary.simpleMessage("Failed to load the ad"),
        "advancedSectionTitle":
            MessageLookupByLibrary.simpleMessage("Advanced"),
        "alreadyPurchased":
            MessageLookupByLibrary.simpleMessage("Already\npurchased"),
        "appearanceSectionTitle":
            MessageLookupByLibrary.simpleMessage("Appearance"),
        "bluetoothNotSupportedExplanation": MessageLookupByLibrary.simpleMessage(
            "Since Android 13, toggling bluetooth is not possible anymore."),
        "bluetoothToggleNotSupportedTitle":
            MessageLookupByLibrary.simpleMessage("Bluetooth action removed"),
        "buttonOpenSavedTimer":
            MessageLookupByLibrary.simpleMessage("Tap here to see your timer"),
        "buttonSearchAgain":
            MessageLookupByLibrary.simpleMessage("Search again"),
        "buttonShowAlarmApps": MessageLookupByLibrary.simpleMessage("Alarm"),
        "buttonShowPlayerApps": MessageLookupByLibrary.simpleMessage("Player"),
        "buttonTimerContinue": MessageLookupByLibrary.simpleMessage("Resume"),
        "buttonTimerPause": MessageLookupByLibrary.simpleMessage("Pause"),
        "buttonTimerStart": MessageLookupByLibrary.simpleMessage("Start timer"),
        "byNumberOfMinutesLong": m0,
        "cannotUninstallDesc": MessageLookupByLibrary.simpleMessage(
            "If you can\'t uninstall the app, make sure that device admin is disabled in settings."),
        "cannotUninstallTitle":
            MessageLookupByLibrary.simpleMessage("Uninstall"),
        "chooseThemeTitle": MessageLookupByLibrary.simpleMessage("Theme"),
        "commonDialogOk": MessageLookupByLibrary.simpleMessage("OK"),
        "connectionStateConnected":
            MessageLookupByLibrary.simpleMessage("Connected"),
        "connectionStateFailed": MessageLookupByLibrary.simpleMessage("Failed"),
        "countDevicesFound": m1,
        "countLights": m2,
        "creditsAppTitle": MessageLookupByLibrary.simpleMessage("Credits"),
        "creditsLibraries": MessageLookupByLibrary.simpleMessage("Libraries"),
        "dialogCancel": MessageLookupByLibrary.simpleMessage("Cancel"),
        "dialogConnect": MessageLookupByLibrary.simpleMessage("Connect"),
        "dialogDone": MessageLookupByLibrary.simpleMessage("OK"),
        "dialogUnlink": MessageLookupByLibrary.simpleMessage("Unlink"),
        "errorNoConnection":
            MessageLookupByLibrary.simpleMessage("Check your connection"),
        "errorNoDevices":
            MessageLookupByLibrary.simpleMessage("No devices found"),
        "extendTimeByShakeMenuToolTip": MessageLookupByLibrary.simpleMessage(
            "Select the time to be extended by on phone shake"),
        "faqShort": MessageLookupByLibrary.simpleMessage("FAQ"),
        "faqTitle":
            MessageLookupByLibrary.simpleMessage("Frequently asked questions"),
        "hintTurnsOffLightAction": MessageLookupByLibrary.simpleMessage(
            "This will turn off the light action."),
        "introAutomateSleepRoutineSubtitle": MessageLookupByLibrary.simpleMessage(
            "Are you tired of adjusting the same settings every night in order to fall asleep well?"),
        "introAutomateSleepRoutineTitle": MessageLookupByLibrary.simpleMessage(
            "Automate your daily sleep routine"),
        "introButtonDone": MessageLookupByLibrary.simpleMessage("DONE"),
        "introButtonNext": MessageLookupByLibrary.simpleMessage("NEXT"),
        "introButtonSkip": MessageLookupByLibrary.simpleMessage("SKIP"),
        "introGoodNightSubtitle": MessageLookupByLibrary.simpleMessage(
            "Just set the timer and you\'re done.\nRelax and let your dreams come true ..."),
        "introGoodNightTitle":
            MessageLookupByLibrary.simpleMessage("Have a good night"),
        "introMediaSubtitle": MessageLookupByLibrary.simpleMessage(
            "Do you like to hear music or watch a movie while falling asleep?"),
        "introMediaTitle": MessageLookupByLibrary.simpleMessage(
            "Listen to your favorite music"),
        "introNoInterruptionsSubtitle": MessageLookupByLibrary.simpleMessage(
            "Don\'t get distracted by incoming messages or notifications."),
        "introNoInterruptionsTitle":
            MessageLookupByLibrary.simpleMessage("No interruptions"),
        "linkBridge": MessageLookupByLibrary.simpleMessage("Link bridge"),
        "linkBridgeInstruction": MessageLookupByLibrary.simpleMessage(
            "1. Push the button on the bridge\n2. Tap on connect"),
        "linkingFailed":
            MessageLookupByLibrary.simpleMessage("Connection failed"),
        "linkingPending": MessageLookupByLibrary.simpleMessage("Connecting"),
        "linkingState": m3,
        "linkingUnknownError": MessageLookupByLibrary.simpleMessage("Unknown"),
        "longPressToAdjustDesc": MessageLookupByLibrary.simpleMessage(
            "Some actions can be configured. Press and hold the action to do so."),
        "longPressToAdjustTitle":
            MessageLookupByLibrary.simpleMessage("Press and hold"),
        "noLightsFound":
            MessageLookupByLibrary.simpleMessage("No Philips Hue lights found"),
        "notSupported":
            MessageLookupByLibrary.simpleMessage("Not supported by device"),
        "notificationActionCancel":
            MessageLookupByLibrary.simpleMessage("Cancel"),
        "notificationActionContinue":
            MessageLookupByLibrary.simpleMessage("Resume"),
        "notificationActionPause":
            MessageLookupByLibrary.simpleMessage("Pause"),
        "notificationActionRestart":
            MessageLookupByLibrary.simpleMessage("Restart"),
        "notificationNoActionsExecuted": MessageLookupByLibrary.simpleMessage(
            "No actions selected for execution."),
        "notificationStatusActive":
            MessageLookupByLibrary.simpleMessage("Sleep Timer active"),
        "notificationStatusElapsed":
            MessageLookupByLibrary.simpleMessage("Sleep Timer elapsed"),
        "notificationStatusPausing":
            MessageLookupByLibrary.simpleMessage("Sleep Timer paused"),
        "notificationTimeLeft":
            MessageLookupByLibrary.simpleMessage("Time left: %s"),
        "notificationsRequired": MessageLookupByLibrary.simpleMessage(
            "Timer notifications required"),
        "numberOfMinutesLong": m4,
        "numberOfMinutesShort": m5,
        "otherSectionTitle": MessageLookupByLibrary.simpleMessage("Other"),
        "prefsDeviceAdmin":
            MessageLookupByLibrary.simpleMessage("Device admin"),
        "prefsDeviceAdminDescription": MessageLookupByLibrary.simpleMessage(
            "Required to turn screen off. Disable before uninstall."),
        "prefsExtendTimeOnShake":
            MessageLookupByLibrary.simpleMessage("Extend on phone shake"),
        "prefsHintEnableAccessToNotificationSettings":
            MessageLookupByLibrary.simpleMessage(
                "Please allow Do Not Disturb access to enable DND action"),
        "prefsHintEnableDeviceAdmin": MessageLookupByLibrary.simpleMessage(
            "Please activate device admin"),
        "prefsNotificationSettingsAccess":
            MessageLookupByLibrary.simpleMessage("Do Not Disturb Access"),
        "prefsNotificationSettingsAccessDescription":
            MessageLookupByLibrary.simpleMessage("Required to enable DND"),
        "purchasesSectionTitle":
            MessageLookupByLibrary.simpleMessage("Support me"),
        "quickLaunchTitle":
            MessageLookupByLibrary.simpleMessage("Quick launch"),
        "rateAppPrice": MessageLookupByLibrary.simpleMessage("Free"),
        "rateAppSubtitle":
            MessageLookupByLibrary.simpleMessage("Any feedback is appreciated"),
        "rateAppTitle":
            MessageLookupByLibrary.simpleMessage("Do you like the app?"),
        "setToValue": m6,
        "setVolumeTitle": MessageLookupByLibrary.simpleMessage("Set volume"),
        "settings": MessageLookupByLibrary.simpleMessage("Settings"),
        "showTimerGlow":
            MessageLookupByLibrary.simpleMessage("Timer glow effect"),
        "showTimerGlowDescription": MessageLookupByLibrary.simpleMessage(
            "Shows a glow effect around the timers progress bar"),
        "sleepTimer": MessageLookupByLibrary.simpleMessage("Sleep Timer"),
        "stateSearching": MessageLookupByLibrary.simpleMessage("Searching ..."),
        "tapToChangeTime":
            MessageLookupByLibrary.simpleMessage("Tap to change time"),
        "tapToConnect": MessageLookupByLibrary.simpleMessage("Tap to\nconnect"),
        "tapToToggleDesc": MessageLookupByLibrary.simpleMessage(
            "Start actions will be executed next time you start the timer."),
        "tapToToggleTitle":
            MessageLookupByLibrary.simpleMessage("Tap to enable"),
        "timerEndsActionsTitle":
            MessageLookupByLibrary.simpleMessage("When time is up"),
        "timerNoAlarm": MessageLookupByLibrary.simpleMessage(
            "Timer doesn\'t work sometimes"),
        "timerNoAlarmDescription": MessageLookupByLibrary.simpleMessage(
            "Make sure that battery optimization is disabled for the app. Some device manufacturers such as Samsung or Huawei require additional settings to allow the app to be running in background."),
        "timerNoBluetooth":
            MessageLookupByLibrary.simpleMessage("No bluetooth action"),
        "timerNoBluetoothDescription": MessageLookupByLibrary.simpleMessage(
            "Starting with Android 13, it is no longer possible to toggle Bluetooth from within an app."),
        "timerNoWifi": MessageLookupByLibrary.simpleMessage("No wifi action"),
        "timerNoWifiDescription": MessageLookupByLibrary.simpleMessage(
            "Starting with Android 10, it is no longer possible to toggle Wifi from within an app."),
        "timerSettingsSectionTitle":
            MessageLookupByLibrary.simpleMessage("Timer settings"),
        "timerStartsActionsTitle":
            MessageLookupByLibrary.simpleMessage("When timer starts"),
        "titleLightGroups":
            MessageLookupByLibrary.simpleMessage("Turn off the light"),
        "unlinkBridgeName": m7
      };
}
