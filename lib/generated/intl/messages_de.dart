// DO NOT EDIT. This is code generated via package:intl/generate_localized.dart
// This is a library that provides messages for a de locale. All the
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
  String get localeName => 'de';

  static String m0(minutes) => "um ${minutes} Minuten";

  static String m1(count) =>
      "${Intl.plural(count, one: '1 Geräte gefunden', other: '${count} Geräte gefunden')}";

  static String m2(count) =>
      "${Intl.plural(count, one: '1 Licht', other: '${count} Lichter')}";

  static String m3(status) => "Status: ${status}";

  static String m4(minutes) => "${minutes} Minuten";

  static String m5(minutes) => "${minutes} Min";

  static String m6(action, value) => "Stelle ${action} auf ${value}";

  static String m7(bridge) => "Unlink ${bridge}";

  final messages = _notInlinedMessages(_notInlinedMessages);
  static Map<String, Function> _notInlinedMessages(_) => <String, Function>{
        "actionDoNotDisturbDescription":
            MessageLookupByLibrary.simpleMessage("Aktiviert \"Nicht stören\""),
        "actionDoNotDisturbTitle":
            MessageLookupByLibrary.simpleMessage("Nicht stören"),
        "actionNotSupported":
            MessageLookupByLibrary.simpleMessage("Aktion nicht unterstützt"),
        "actionPlayMusicTitle":
            MessageLookupByLibrary.simpleMessage("Spielt Musik ab"),
        "actionToggleBluetoothDescription":
            MessageLookupByLibrary.simpleMessage("Deaktiviert Bluetooth"),
        "actionToggleBluetoothTitle":
            MessageLookupByLibrary.simpleMessage("Bluetooth"),
        "actionToggleLightDescription": MessageLookupByLibrary.simpleMessage(
            "Schaltet deine Philips Hue Lampen aus"),
        "actionToggleLightTitle":
            MessageLookupByLibrary.simpleMessage("Lampen"),
        "actionToggleMediaDescription":
            MessageLookupByLibrary.simpleMessage("Stoppt Medienwiedergabe"),
        "actionToggleMediaTitle":
            MessageLookupByLibrary.simpleMessage("Medien"),
        "actionToggleScreenDescription":
            MessageLookupByLibrary.simpleMessage("Schaltet das Display aus"),
        "actionToggleScreenTitle":
            MessageLookupByLibrary.simpleMessage("Display"),
        "actionToggleWifiDescription":
            MessageLookupByLibrary.simpleMessage("Deaktiviert WLAN"),
        "actionToggleWifiTitle": MessageLookupByLibrary.simpleMessage("WLAN"),
        "actionVolumeTitle": MessageLookupByLibrary.simpleMessage("Lautstärke"),
        "adLoadFailure": MessageLookupByLibrary.simpleMessage(
            "Fehler beim Laden der Werbung"),
        "advancedSectionTitle":
            MessageLookupByLibrary.simpleMessage("Fortgeschrittene Funktionen"),
        "alreadyPurchased":
            MessageLookupByLibrary.simpleMessage("Bereits\ngekauft"),
        "appearanceSectionTitle":
            MessageLookupByLibrary.simpleMessage("Erscheinungsbild"),
        "bluetoothNotSupportedExplanation": MessageLookupByLibrary.simpleMessage(
            "Seit Android 13 ist das Deaktivieren von Bluetooth nicht mehr möglich."),
        "bluetoothToggleNotSupportedTitle":
            MessageLookupByLibrary.simpleMessage("Bluetooth-Aktion entfernt"),
        "buttonOpenSavedTimer": MessageLookupByLibrary.simpleMessage(
            "Hier tippen, um deinen Timer zu sehen"),
        "buttonSearchAgain":
            MessageLookupByLibrary.simpleMessage("Suche wiederholen"),
        "buttonShowAlarmApps": MessageLookupByLibrary.simpleMessage("Wecker"),
        "buttonShowPlayerApps": MessageLookupByLibrary.simpleMessage("Player"),
        "buttonTimerContinue":
            MessageLookupByLibrary.simpleMessage("Fortsetzen"),
        "buttonTimerPause": MessageLookupByLibrary.simpleMessage("Pause"),
        "buttonTimerStart":
            MessageLookupByLibrary.simpleMessage("Timer starten"),
        "byNumberOfMinutesLong": m0,
        "cannotUninstallDesc": MessageLookupByLibrary.simpleMessage(
            "Um die App deinstallieren zu können, muss \"Erweiterte Gerätefunktionen\" in den Einstellungen deaktiviert werden."),
        "cannotUninstallTitle":
            MessageLookupByLibrary.simpleMessage("Deinstallieren"),
        "chooseThemeTitle": MessageLookupByLibrary.simpleMessage("Theme"),
        "commonDialogOk": MessageLookupByLibrary.simpleMessage("OK"),
        "connectionStateConnected":
            MessageLookupByLibrary.simpleMessage("Verbunden"),
        "connectionStateFailed":
            MessageLookupByLibrary.simpleMessage("Fehlgeschlagen"),
        "countDevicesFound": m1,
        "countLights": m2,
        "creditsAppTitle": MessageLookupByLibrary.simpleMessage("Credits"),
        "creditsLibraries":
            MessageLookupByLibrary.simpleMessage("Bibliotheken"),
        "dialogCancel": MessageLookupByLibrary.simpleMessage("Abbrechen"),
        "dialogConnect": MessageLookupByLibrary.simpleMessage("Verbinden"),
        "dialogDone": MessageLookupByLibrary.simpleMessage("OK"),
        "dialogUnlink": MessageLookupByLibrary.simpleMessage("Unlink"),
        "errorNoConnection":
            MessageLookupByLibrary.simpleMessage("Überprüfe deine Verbindung"),
        "errorNoDevices":
            MessageLookupByLibrary.simpleMessage("Kein Gerät gefunden"),
        "extendTimeByShakeMenuToolTip": MessageLookupByLibrary.simpleMessage(
            "Tippen, um die Zeit zu ändern"),
        "faqShort": MessageLookupByLibrary.simpleMessage("FAQ"),
        "faqTitle":
            MessageLookupByLibrary.simpleMessage("Häufig gestellte Fragen"),
        "hintTurnsOffLightAction": MessageLookupByLibrary.simpleMessage(
            "Dadurch wird die Lichtfunktion ausgeschaltet."),
        "introAutomateSleepRoutineSubtitle": MessageLookupByLibrary.simpleMessage(
            "Bist du es es leid, jede Nacht die gleichen Einstellungen vorzunehmen, um gut einschlafen zu können?"),
        "introAutomateSleepRoutineTitle": MessageLookupByLibrary.simpleMessage(
            "Automatisiere deine Schlafroutine"),
        "introButtonDone": MessageLookupByLibrary.simpleMessage("FERTIG"),
        "introButtonNext": MessageLookupByLibrary.simpleMessage("WEITER"),
        "introButtonSkip":
            MessageLookupByLibrary.simpleMessage("ÜBER-\nSPRINGEN"),
        "introGoodNightSubtitle": MessageLookupByLibrary.simpleMessage(
            "Stell einfach den Timer und fertig.\nEntspann dich und träum schön :)"),
        "introGoodNightTitle":
            MessageLookupByLibrary.simpleMessage("Gute Nacht"),
        "introMediaSubtitle": MessageLookupByLibrary.simpleMessage(
            "Hörst du zum Einschlafen gerne Musik oder schaust dir einen Film an?"),
        "introMediaTitle":
            MessageLookupByLibrary.simpleMessage("Hör deine Lieblingsmusik"),
        "introNoInterruptionsSubtitle": MessageLookupByLibrary.simpleMessage(
            "Lass dich nicht durch eingehende Benachrichtigungen ablenken."),
        "introNoInterruptionsTitle":
            MessageLookupByLibrary.simpleMessage("Keine Unterbrechungen"),
        "linkBridge": MessageLookupByLibrary.simpleMessage("Verbinde Bridge"),
        "linkBridgeInstruction": MessageLookupByLibrary.simpleMessage(
            "1. Drücke den Knopf auf der Bridge\n2. Tippe auf verbinden"),
        "linkingFailed":
            MessageLookupByLibrary.simpleMessage("Connection failed"),
        "linkingPending": MessageLookupByLibrary.simpleMessage("Connecting"),
        "linkingState": m3,
        "linkingUnknownError": MessageLookupByLibrary.simpleMessage("Unknown"),
        "longPressToAdjustDesc": MessageLookupByLibrary.simpleMessage(
            "Einige Aktionen können konfiguriert werden. Halte die Aktion dazu gedrückt."),
        "longPressToAdjustTitle":
            MessageLookupByLibrary.simpleMessage("Gedrückthalten"),
        "noLightsFound": MessageLookupByLibrary.simpleMessage(
            "Keine Philips Hue Lampen gefunden"),
        "notSupported":
            MessageLookupByLibrary.simpleMessage("Nicht vom Gerät unterstützt"),
        "notificationActionCancel":
            MessageLookupByLibrary.simpleMessage("Abbrechen"),
        "notificationActionContinue":
            MessageLookupByLibrary.simpleMessage("Fortsetzen"),
        "notificationActionPause":
            MessageLookupByLibrary.simpleMessage("Pause"),
        "notificationActionRestart":
            MessageLookupByLibrary.simpleMessage("Neustarten"),
        "notificationNoActionsExecuted": MessageLookupByLibrary.simpleMessage(
            "Keine Aktionen zur Ausführung ausgewählt."),
        "notificationStatusActive":
            MessageLookupByLibrary.simpleMessage("Sleep Timer läuft"),
        "notificationStatusElapsed":
            MessageLookupByLibrary.simpleMessage("Sleep Timer abgelaufen"),
        "notificationStatusPausing":
            MessageLookupByLibrary.simpleMessage("Sleep Timer pausiert"),
        "notificationTimeLeft":
            MessageLookupByLibrary.simpleMessage("Verbleibende Zeit: %s"),
        "notificationsRequired": MessageLookupByLibrary.simpleMessage(
            "Timer-Benachrichtigungen erforderlich"),
        "numberOfMinutesLong": m4,
        "numberOfMinutesShort": m5,
        "otherSectionTitle": MessageLookupByLibrary.simpleMessage("Sonstige"),
        "prefsDeviceAdmin":
            MessageLookupByLibrary.simpleMessage("Erweiterte Gerätefunktionen"),
        "prefsDeviceAdminDescription": MessageLookupByLibrary.simpleMessage(
            "Ermöglicht das Ausschalten des Displays. Vorm Deinstallieren deaktivieren."),
        "prefsExtendTimeOnShake":
            MessageLookupByLibrary.simpleMessage("Schütteln zum Verlängern"),
        "prefsHintEnableAccessToNotificationSettings":
            MessageLookupByLibrary.simpleMessage(
                "Aktiviere den Zugriff auf Benachrichtigungs­einstellungen"),
        "prefsHintEnableDeviceAdmin": MessageLookupByLibrary.simpleMessage(
            "Aktiviere die erweiterten Gerätefunktionen"),
        "prefsNotificationSettingsAccess":
            MessageLookupByLibrary.simpleMessage("Zugriff auf Nicht stören"),
        "prefsNotificationSettingsAccessDescription":
            MessageLookupByLibrary.simpleMessage(
                "Wird für die \"Nicht stören\"-Aktion benötigt"),
        "purchasesSectionTitle":
            MessageLookupByLibrary.simpleMessage("In-App-Käufe"),
        "quickLaunchTitle":
            MessageLookupByLibrary.simpleMessage("Schnellstart"),
        "rateAppPrice": MessageLookupByLibrary.simpleMessage("Gratis"),
        "rateAppSubtitle": MessageLookupByLibrary.simpleMessage(
            "Ich bin für jede Bewertung dankbar"),
        "rateAppTitle":
            MessageLookupByLibrary.simpleMessage("Gefällt dir die App?"),
        "setToValue": m6,
        "setVolumeTitle":
            MessageLookupByLibrary.simpleMessage("Lautstärke ändern"),
        "settings": MessageLookupByLibrary.simpleMessage("Einstellungen"),
        "showTimerGlow":
            MessageLookupByLibrary.simpleMessage("Timer Leuchteffekt"),
        "showTimerGlowDescription": MessageLookupByLibrary.simpleMessage(
            "Zeigt einen Leuchteffekt rund um den Fortschrittsbalken des Timers"),
        "sleepTimer": MessageLookupByLibrary.simpleMessage("Sleep Timer"),
        "stateSearching": MessageLookupByLibrary.simpleMessage("Suche ..."),
        "tapToConnect":
            MessageLookupByLibrary.simpleMessage("Tippe zum\nVerbinden"),
        "tapToToggleDesc": MessageLookupByLibrary.simpleMessage(
            "Startaktionen werden beim nächsten Start des Timers ausgeführt."),
        "tapToToggleTitle":
            MessageLookupByLibrary.simpleMessage("Antippen zum Aktivieren"),
        "timerEndsActionsTitle":
            MessageLookupByLibrary.simpleMessage("Nach Ablauf der Zeit"),
        "timerNoAlarm": MessageLookupByLibrary.simpleMessage(
            "Timer löst manchmal nicht aus"),
        "timerNoAlarmDescription": MessageLookupByLibrary.simpleMessage(
            "Stell bitte sicher, dass die Batterieoptimierung für die App deaktiviert ist. Einige Gerätehersteller wie Samsung oder Huawei benötigen zusätzliche Einstellungen, damit die App im Hintergrund ausgeführt werden kann."),
        "timerNoBluetooth":
            MessageLookupByLibrary.simpleMessage("Bluetooth Aktion fehlt"),
        "timerNoBluetoothDescription": MessageLookupByLibrary.simpleMessage(
            "Seit Android 13 ist es nicht mehr möglich, Bluetooth über eine App an- oder auszuschalten.."),
        "timerNoWifi":
            MessageLookupByLibrary.simpleMessage("WLAN Aktion fehlt"),
        "timerNoWifiDescription": MessageLookupByLibrary.simpleMessage(
            "Seit Android 10 ist es nicht mehr möglich, WLAN über eine App an- oder auszuschalten."),
        "timerSettingsSectionTitle":
            MessageLookupByLibrary.simpleMessage("Timer"),
        "timerStartsActionsTitle":
            MessageLookupByLibrary.simpleMessage("Beim Starten des Timers"),
        "titleLightGroups":
            MessageLookupByLibrary.simpleMessage("Lampen ausschalten"),
        "unlinkBridgeName": m7
      };
}
