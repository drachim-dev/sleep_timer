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

  static m0(count) => "${Intl.plural(count, one: '1 Geräte gefunden', other: '${count} Geräte gefunden')}";

  static m1(count) => "${Intl.plural(count, one: '1 Licht', other: '${count} Lichter')}";

  static m2(time) => "${time} abgelaufen. ";

  static m3(timeLeft) => "Verbleibende Zeit: ${timeLeft}";

  static m4(time) => "Zeit auf ${time} Minuten festgelegt";

  static m5(minutes) => "${minutes} Minuten";

  static m6(minutes) => "${minutes} Min";

  static m7(durationString) => "${durationString} Minuten";

  static m8(bridge) => "Unlink ${bridge}";

  final messages = _notInlinedMessages(_notInlinedMessages);
  static _notInlinedMessages(_) => <String, Function> {
    "IntroMediaSubtitle" : MessageLookupByLibrary.simpleMessage("Hörst du zum Einschlafen gerne Musik oder schaust dir einen Film an?"),
    "actionDoNotDisturbDescription" : MessageLookupByLibrary.simpleMessage("Aktiviert \"Nicht stören\""),
    "actionDoNotDisturbTitle" : MessageLookupByLibrary.simpleMessage("Nicht stören"),
    "actionPlayMusicTitle" : MessageLookupByLibrary.simpleMessage("Spielt Musik ab"),
    "actionToggleBluetoothDescription" : MessageLookupByLibrary.simpleMessage("Deaktiviert Bluetooth"),
    "actionToggleBluetoothTitle" : MessageLookupByLibrary.simpleMessage("Bluetooth"),
    "actionToggleLightDescription" : MessageLookupByLibrary.simpleMessage("Schaltet deine Philips Hue Lampen aus"),
    "actionToggleLightTitle" : MessageLookupByLibrary.simpleMessage("Lampen"),
    "actionToggleMediaDescription" : MessageLookupByLibrary.simpleMessage("Stoppt Medienwiedergabe"),
    "actionToggleMediaTitle" : MessageLookupByLibrary.simpleMessage("Medien"),
    "actionToggleScreenDescription" : MessageLookupByLibrary.simpleMessage("Schaltet das Display aus"),
    "actionToggleScreenTitle" : MessageLookupByLibrary.simpleMessage("Display"),
    "actionToggleWifiDescription" : MessageLookupByLibrary.simpleMessage("Deaktiviert WLAN"),
    "actionToggleWifiTitle" : MessageLookupByLibrary.simpleMessage("WLAN"),
    "actionVolumeTitle" : MessageLookupByLibrary.simpleMessage("Lautstärke"),
    "adLoadFailure" : MessageLookupByLibrary.simpleMessage("Fehler beim Laden der Werbung"),
    "advancedSectionTitle" : MessageLookupByLibrary.simpleMessage("Fortgeschrittene Funktionen"),
    "alreadyPurchased" : MessageLookupByLibrary.simpleMessage("Bereits\ngekauft"),
    "appearanceSectionTitle" : MessageLookupByLibrary.simpleMessage("Erscheinungsbild"),
    "buttonOpenSavedTimer" : MessageLookupByLibrary.simpleMessage("Hier tippen, um deinen Timer zu sehen"),
    "buttonSearchAgain" : MessageLookupByLibrary.simpleMessage("Suche wiederholen"),
    "buttonShowAlarmApps" : MessageLookupByLibrary.simpleMessage("Wecker"),
    "buttonShowPlayerApps" : MessageLookupByLibrary.simpleMessage("Player"),
    "buttonTimerContinue" : MessageLookupByLibrary.simpleMessage("Fortsetzen"),
    "buttonTimerPause" : MessageLookupByLibrary.simpleMessage("Pause"),
    "buttonTimerStart" : MessageLookupByLibrary.simpleMessage("Timer starten"),
    "cannotUninstallDesc" : MessageLookupByLibrary.simpleMessage("Um die App deinstallieren zu können, muss \"Erweiterte Gerätefunktionen\" in den Einstellungen deaktiviert werden."),
    "cannotUninstallTitle" : MessageLookupByLibrary.simpleMessage("Deinstallieren"),
    "chooseThemeTitle" : MessageLookupByLibrary.simpleMessage("Theme"),
    "connectionStateConnected" : MessageLookupByLibrary.simpleMessage("Verbunden"),
    "connectionStateFailed" : MessageLookupByLibrary.simpleMessage("Fehlgeschlagen"),
    "countDevicesFound" : m0,
    "countLights" : m1,
    "creditsAppTitle" : MessageLookupByLibrary.simpleMessage("Credits"),
    "creditsLibraries" : MessageLookupByLibrary.simpleMessage("Bibliotheken"),
    "dialogCancel" : MessageLookupByLibrary.simpleMessage("ABBRECHEN"),
    "dialogConnect" : MessageLookupByLibrary.simpleMessage("VERBINDEN"),
    "dialogDone" : MessageLookupByLibrary.simpleMessage("FERTIG"),
    "dialogUnlink" : MessageLookupByLibrary.simpleMessage("UNLINK"),
    "errorNoConnection" : MessageLookupByLibrary.simpleMessage("Überprüfe deine Verbindung"),
    "errorNoDevices" : MessageLookupByLibrary.simpleMessage("Kein Gerät gefunden"),
    "extendTimeByShakeMenuToolTip" : MessageLookupByLibrary.simpleMessage("Tippen, um die Zeit zu ändern"),
    "faqShort" : MessageLookupByLibrary.simpleMessage("FAQ"),
    "faqTitle" : MessageLookupByLibrary.simpleMessage("Häufig gestellte Fragen"),
    "hintTurnsOffLightAction" : MessageLookupByLibrary.simpleMessage("Dadurch wird die Lichtfunktion ausgeschaltet."),
    "introAutomateSleepRoutineSubtitle" : MessageLookupByLibrary.simpleMessage("Bist du es es leid, jede Nacht die gleichen Einstellungen vorzunehmen, um gut einschlafen zu können?"),
    "introAutomateSleepRoutineTitle" : MessageLookupByLibrary.simpleMessage("Automatisiere deine Schlafroutine"),
    "introButtonDone" : MessageLookupByLibrary.simpleMessage("FERTIG"),
    "introButtonNext" : MessageLookupByLibrary.simpleMessage("WEITER"),
    "introButtonSkip" : MessageLookupByLibrary.simpleMessage("ÜBER-\nSPRINGEN"),
    "introGoodNightSubtitle" : MessageLookupByLibrary.simpleMessage("Stell einfach den Timer und fertig.\nEntspann dich und träum schön :)"),
    "introGoodNightTitle" : MessageLookupByLibrary.simpleMessage("Gute Nacht"),
    "introMediaTitle" : MessageLookupByLibrary.simpleMessage("Hör deine Lieblingsmusik"),
    "introNoInterruptionsSubtitle" : MessageLookupByLibrary.simpleMessage("Lass dich nicht durch eingehende Benachrichtigungen ablenken."),
    "introNoInterruptionsTitle" : MessageLookupByLibrary.simpleMessage("Keine Unterbrechungen"),
    "linkBridge" : MessageLookupByLibrary.simpleMessage("Verbinde Bridge"),
    "linkBridgeInstruction" : MessageLookupByLibrary.simpleMessage("1. Drücke den Knopf auf der Bridge\n2. Tippe auf verbinden"),
    "longPressToAdjustDesc" : MessageLookupByLibrary.simpleMessage("Einige Aktionen können konfiguriert werden. Halte die Aktion dazu gedrückt."),
    "longPressToAdjustTitle" : MessageLookupByLibrary.simpleMessage("Gedrückthalten"),
    "noLightsFound" : MessageLookupByLibrary.simpleMessage("Keine Philips Hue Lampen gefunden"),
    "notSupported" : MessageLookupByLibrary.simpleMessage("Nicht vom Gerät unterstützt"),
    "notificationActionCancel" : MessageLookupByLibrary.simpleMessage("Abbrechen"),
    "notificationActionContinue" : MessageLookupByLibrary.simpleMessage("Fortsetzen"),
    "notificationActionPause" : MessageLookupByLibrary.simpleMessage("Pause"),
    "notificationActionRestart" : MessageLookupByLibrary.simpleMessage("Neustarten"),
    "notificationNoActionsExecuted" : MessageLookupByLibrary.simpleMessage("Keine Aktionen zur Ausführung ausgewählt."),
    "notificationStatusElapsed" : MessageLookupByLibrary.simpleMessage("Sleep Timer abgelaufen"),
    "notificationStatusPausing" : MessageLookupByLibrary.simpleMessage("Sleep Timer pausiert"),
    "notificationStatusRunning" : MessageLookupByLibrary.simpleMessage("Sleep Timer läuft"),
    "notificationTimeExpired" : m2,
    "notificationTimeLeft" : m3,
    "notificationTimerSet" : m4,
    "numberOfMinutesLong" : m5,
    "numberOfMinutesShort" : m6,
    "otherSectionTitle" : MessageLookupByLibrary.simpleMessage("Sonstige"),
    "prefsDeviceAdmin" : MessageLookupByLibrary.simpleMessage("Erweiterte Gerätefunktionen"),
    "prefsDeviceAdminDescription" : MessageLookupByLibrary.simpleMessage("Verwaltung von Gerätefunktionen\nErmöglicht Ausschalten des Displays"),
    "prefsExtendTimeOnShake" : MessageLookupByLibrary.simpleMessage("Verlängern durch Schütteln"),
    "prefsHintEnableAccessToNotificationSettings" : MessageLookupByLibrary.simpleMessage("Aktiviere den Zugriff auf Benachrichtigungs­einstellungen"),
    "prefsHintEnableDeviceAdmin" : MessageLookupByLibrary.simpleMessage("Aktiviere die erweiterten Gerätefunktionen"),
    "prefsNotificationSettingsAccess" : MessageLookupByLibrary.simpleMessage("Zugriff auf Nicht stören"),
    "prefsNotificationSettingsAccessDescription" : MessageLookupByLibrary.simpleMessage("Aktiviert die \"Nicht stören\"-Aktion"),
    "purchasesSectionTitle" : MessageLookupByLibrary.simpleMessage("In-App-Käufe"),
    "quickLaunchTitle" : MessageLookupByLibrary.simpleMessage("Schnellstart"),
    "setVolumeTitle" : MessageLookupByLibrary.simpleMessage("Lautstärke ändern"),
    "settings" : MessageLookupByLibrary.simpleMessage("Einstellungen"),
    "showTimerGlow" : MessageLookupByLibrary.simpleMessage("Timer Leuchteffekt"),
    "showTimerGlowDescription" : MessageLookupByLibrary.simpleMessage("Zeigt einen Leuchteffekt rund um den Fortschrittsbalken des Timers"),
    "sleepTimer" : MessageLookupByLibrary.simpleMessage("Sleep Timer"),
    "stateSearching" : MessageLookupByLibrary.simpleMessage("Suche ..."),
    "tapToConnect" : MessageLookupByLibrary.simpleMessage("Tippe zum\nVerbinden"),
    "tapToToggleDesc" : MessageLookupByLibrary.simpleMessage("Startaktionen werden beim nächsten Start des Timers ausgeführt."),
    "tapToToggleTitle" : MessageLookupByLibrary.simpleMessage("Antippen zum Aktivieren"),
    "timerEndsActionsTitle" : MessageLookupByLibrary.simpleMessage("Nach Ablauf der Zeit"),
    "timerNoAlarm" : MessageLookupByLibrary.simpleMessage("Timer löst manchmal nicht aus"),
    "timerNoAlarmDescription" : MessageLookupByLibrary.simpleMessage("Stell bitte sicher, dass die Batterieoptimierung für die App deaktiviert ist. Einige Gerätehersteller wie Samsung oder Huawei benötigen zusätzliche Einstellungen, damit die App im Hintergrund ausgeführt werden kann."),
    "timerNoWifi" : MessageLookupByLibrary.simpleMessage("WLAN Aktion fehlt"),
    "timerNoWifiDescription" : MessageLookupByLibrary.simpleMessage("Seit Android 10 ist es nicht mehr möglich, WLAN über eine App an- oder auszuschalten."),
    "timerSettingsSectionTitle" : MessageLookupByLibrary.simpleMessage("Timer"),
    "timerStartsActionsTitle" : MessageLookupByLibrary.simpleMessage("Beim Starten des Timers"),
    "titleLightGroups" : MessageLookupByLibrary.simpleMessage("Lampen ausschalten"),
    "unitMinute" : m7,
    "unlinkBridgeName" : m8
  };
}
