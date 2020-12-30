// DO NOT EDIT. This is code generated via package:intl/generate_localized.dart
// This is a library that provides messages for a de locale. All the
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
  String get localeName => 'de';

  static m0(time) => "${time} abgelaufen. ";

  static m1(timeLeft) => "Verbleibende Zeit: ${timeLeft}";

  static m2(time) => "Zeit auf ${time} Minuten festgelegt";

  static m3(minutes) => "${minutes} Minuten";

  static m4(minutes) => "${minutes} Min";

  static m5(durationString) => "${durationString} Minuten";

  final messages = _notInlinedMessages(_notInlinedMessages);
  static _notInlinedMessages(_) => <String, Function> {
    "IntroMediaSubtitle" : MessageLookupByLibrary.simpleMessage("Hörst du zum Einschlafen gerne Musik oder schaust dir einen Film an?"),
    "actionDoNotDisturbDescription" : MessageLookupByLibrary.simpleMessage("Aktiviert \"Bitte nicht stören\""),
    "actionDoNotDisturbTitle" : MessageLookupByLibrary.simpleMessage("Bitte nicht stören"),
    "actionPlayMusicTitle" : MessageLookupByLibrary.simpleMessage("Spielt Musik ab"),
    "actionToggleBluetoothDescription" : MessageLookupByLibrary.simpleMessage("Deaktiviert Bluetooth"),
    "actionToggleBluetoothTitle" : MessageLookupByLibrary.simpleMessage("Bluetooth"),
    "actionToggleMediaDescription" : MessageLookupByLibrary.simpleMessage("Stoppt Medienwiedergabe"),
    "actionToggleMediaTitle" : MessageLookupByLibrary.simpleMessage("Medien"),
    "actionToggleScreenDescription" : MessageLookupByLibrary.simpleMessage("Schaltet das Display aus"),
    "actionToggleScreenTitle" : MessageLookupByLibrary.simpleMessage("Display"),
    "actionToggleWifiDescription" : MessageLookupByLibrary.simpleMessage("Deaktiviert WLAN"),
    "actionToggleWifiTitle" : MessageLookupByLibrary.simpleMessage("WLAN"),
    "actionVolumeDescription" : MessageLookupByLibrary.simpleMessage("Ändert Lautstärke auf"),
    "actionVolumeTitle" : MessageLookupByLibrary.simpleMessage("Lautstärke"),
    "adLoadFailure" : MessageLookupByLibrary.simpleMessage("Fehler beim Laden der Werbung"),
    "advancedSectionTitle" : MessageLookupByLibrary.simpleMessage("Fortgeschrittene Funktionen"),
    "alreadyPurchased" : MessageLookupByLibrary.simpleMessage("Bereits\ngekauft"),
    "appearanceSectionTitle" : MessageLookupByLibrary.simpleMessage("Erscheinungsbild"),
    "buttonOpenSavedTimer" : MessageLookupByLibrary.simpleMessage("Hier tippen, um deinen Timer zu sehen"),
    "buttonSetupTimer" : MessageLookupByLibrary.simpleMessage("Schlafen gehen"),
    "buttonShowAlarmApps" : MessageLookupByLibrary.simpleMessage("Wecker"),
    "buttonShowPlayerApps" : MessageLookupByLibrary.simpleMessage("Player"),
    "buttonTimerContinue" : MessageLookupByLibrary.simpleMessage("Fortsetzen"),
    "buttonTimerPause" : MessageLookupByLibrary.simpleMessage("Pause"),
    "buttonTimerStart" : MessageLookupByLibrary.simpleMessage("Timer starten"),
    "chooseThemeTitle" : MessageLookupByLibrary.simpleMessage("Theme"),
    "creditsAppTitle" : MessageLookupByLibrary.simpleMessage("Credits"),
    "creditsLibraries" : MessageLookupByLibrary.simpleMessage("Bibliotheken"),
    "extendTimeByShakeMenuToolTip" : MessageLookupByLibrary.simpleMessage("Tippen, um die Zeit zu ändern"),
    "faqShort" : MessageLookupByLibrary.simpleMessage("FAQ"),
    "faqTitle" : MessageLookupByLibrary.simpleMessage("Häufig gestellte Fragen"),
    "introAutomateSleepRoutineSubtitle" : MessageLookupByLibrary.simpleMessage("Bist du es es leid, jede Nacht die gleichen Einstellungen vorzunehmen, um gut schlafen zu können?"),
    "introAutomateSleepRoutineTitle" : MessageLookupByLibrary.simpleMessage("Automatisiere deine Schlafroutine"),
    "introButtonDone" : MessageLookupByLibrary.simpleMessage("FERTIG"),
    "introButtonNext" : MessageLookupByLibrary.simpleMessage("WEITER"),
    "introButtonSkip" : MessageLookupByLibrary.simpleMessage("ÜBER-\nSPRINGEN"),
    "introGoodNightSubtitle" : MessageLookupByLibrary.simpleMessage("Stell einfach den Timer und fertig.\nEntspann dich und träum schön :)"),
    "introGoodNightTitle" : MessageLookupByLibrary.simpleMessage("Gute Nacht"),
    "introMediaTitle" : MessageLookupByLibrary.simpleMessage("Hör deine Lieblingsmusik"),
    "introNoInterruptionsSubtitle" : MessageLookupByLibrary.simpleMessage("Lass dich nicht durch eingehende Benachrichtigungen ablenken"),
    "introNoInterruptionsTitle" : MessageLookupByLibrary.simpleMessage("Keine Unterbrechungen"),
    "notSupported" : MessageLookupByLibrary.simpleMessage("Nicht vom Gerät unterstützt"),
    "notificationActionCancel" : MessageLookupByLibrary.simpleMessage("Abbrechen"),
    "notificationActionContinue" : MessageLookupByLibrary.simpleMessage("Fortsetzen"),
    "notificationActionPause" : MessageLookupByLibrary.simpleMessage("Pause"),
    "notificationActionRestart" : MessageLookupByLibrary.simpleMessage("Neustarten"),
    "notificationNoActionsExecuted" : MessageLookupByLibrary.simpleMessage("Keine Aktionen zur Ausführung ausgewählt."),
    "notificationStatusElapsed" : MessageLookupByLibrary.simpleMessage("Sleep Timer abgelaufen"),
    "notificationStatusPausing" : MessageLookupByLibrary.simpleMessage("Sleep Timer pausiert"),
    "notificationStatusRunning" : MessageLookupByLibrary.simpleMessage("Sleep Timer läuft"),
    "notificationTimeExpired" : m0,
    "notificationTimeLeft" : m1,
    "notificationTimerSet" : m2,
    "numberOfMinutesLong" : m3,
    "numberOfMinutesShort" : m4,
    "otherSectionTitle" : MessageLookupByLibrary.simpleMessage("Sonstige"),
    "prefsDeviceAdmin" : MessageLookupByLibrary.simpleMessage("Erweiterte Gerätefunktionen"),
    "prefsDeviceAdminDescription" : MessageLookupByLibrary.simpleMessage("Erlaubt Verwaltung von Gerätefunktionen\nErmöglicht Bildschirmabschaltung"),
    "prefsExtendTimeOnShake" : MessageLookupByLibrary.simpleMessage("Verlängern durch Schütteln"),
    "prefsHintEnableAccessToNotificationSettings" : MessageLookupByLibrary.simpleMessage("Aktiviere den Zugriff auf Benachrichtigungs­einstellungen"),
    "prefsHintEnableDeviceAdmin" : MessageLookupByLibrary.simpleMessage("Aktiviere die erweiterten Gerätefunktionen"),
    "prefsNotificationSettingsAccess" : MessageLookupByLibrary.simpleMessage("Zugriff auf \"Bitte nicht stören\""),
    "prefsNotificationSettingsAccessDescription" : MessageLookupByLibrary.simpleMessage("Erlaubt den Zugriff auf Benachrichtigungs­einstellungen"),
    "purchasesSectionTitle" : MessageLookupByLibrary.simpleMessage("In-App-Käufe"),
    "quickLaunchTitle" : MessageLookupByLibrary.simpleMessage("Schnellstart"),
    "setVolumeTitle" : MessageLookupByLibrary.simpleMessage("Lautstärke ändern"),
    "settings" : MessageLookupByLibrary.simpleMessage("Einstellungen"),
    "showTimerGlow" : MessageLookupByLibrary.simpleMessage("Timer Leuchteffekt"),
    "showTimerGlowDescription" : MessageLookupByLibrary.simpleMessage("Zeigt einen Leuchteffekt rund um den Fortschrittsbalken des Timers"),
    "sleepTimer" : MessageLookupByLibrary.simpleMessage("Sleep Timer"),
    "timerEndsActionsTitle" : MessageLookupByLibrary.simpleMessage("Nach Ablauf der Zeit"),
    "timerNoAlarm" : MessageLookupByLibrary.simpleMessage("Der Timer löst manchmal nicht aus"),
    "timerNoAlarmDescription" : MessageLookupByLibrary.simpleMessage("Stell bitte sicher, dass die Batterieoptimierung für die App deaktiviert ist. Einige Gerätehersteller wie Samsung oder Huawei benötigen zusätzliche Einstellungen, damit die App im Hintergrund ausgeführt werden kann."),
    "timerNoWifi" : MessageLookupByLibrary.simpleMessage("Die WLAN Aktion fehlt"),
    "timerNoWifiDescription" : MessageLookupByLibrary.simpleMessage("Ab Android 10 ist es nicht mehr möglich, WLAN über eine App auszuschalten."),
    "timerSettingsSectionTitle" : MessageLookupByLibrary.simpleMessage("Timer"),
    "timerStartsActionsTitle" : MessageLookupByLibrary.simpleMessage("Beim Starten des Timers"),
    "unitMinute" : m5
  };
}
