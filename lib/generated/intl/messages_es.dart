// DO NOT EDIT. This is code generated via package:intl/generate_localized.dart
// This is a library that provides messages for a es locale. All the
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
  String get localeName => 'es';

  static String m0(minutes) => "por ${minutes} minutos";

  static String m1(count) =>
      "${Intl.plural(count, one: '1 dispositivo encontrado', other: '${count} dispositivos encontrados')}";

  static String m2(count) =>
      "${Intl.plural(count, one: '1 luz', other: '${count} luces')}";

  static String m3(status) => "Status: ${status}";

  static String m4(minutes) => "${minutes} minutos";

  static String m5(minutes) => "${minutes} min";

  static String m6(action, value) => "${action} al ${value}";

  static String m7(bridge) => "Unlink ${bridge}";

  final messages = _notInlinedMessages(_notInlinedMessages);
  static Map<String, Function> _notInlinedMessages(_) => <String, Function>{
        "actionDoNotDisturbDescription":
            MessageLookupByLibrary.simpleMessage("No molestar activado"),
        "actionDoNotDisturbTitle":
            MessageLookupByLibrary.simpleMessage("No molestar"),
        "actionNotSupported":
            MessageLookupByLibrary.simpleMessage("Acción no compatible"),
        "actionPlayMusicTitle":
            MessageLookupByLibrary.simpleMessage("Toca música"),
        "actionToggleBluetoothDescription":
            MessageLookupByLibrary.simpleMessage("Bluetooth apagado"),
        "actionToggleBluetoothTitle":
            MessageLookupByLibrary.simpleMessage("Bluetooth"),
        "actionToggleLightDescription":
            MessageLookupByLibrary.simpleMessage("Luz apagada"),
        "actionToggleLightTitle": MessageLookupByLibrary.simpleMessage("Luces"),
        "actionToggleMediaDescription": MessageLookupByLibrary.simpleMessage(
            "Reproducción en pausa"),
        "actionToggleMediaTitle": MessageLookupByLibrary.simpleMessage("Media"),
        "actionToggleScreenDescription":
            MessageLookupByLibrary.simpleMessage("Pantalla apagada"),
        "actionToggleScreenTitle":
            MessageLookupByLibrary.simpleMessage("Pantalla"),
        "actionToggleWifiDescription":
            MessageLookupByLibrary.simpleMessage("Wifi apagado"),
        "actionToggleWifiTitle": MessageLookupByLibrary.simpleMessage("Wifi"),
        "actionVolumeTitle": MessageLookupByLibrary.simpleMessage("Volumen"),
        "adLoadFailure":
            MessageLookupByLibrary.simpleMessage("No se ha cargado el anuncio"),
        "advancedSectionTitle":
            MessageLookupByLibrary.simpleMessage("Avanzado"),
        "alreadyPurchased": MessageLookupByLibrary.simpleMessage("Ya comprado"),
        "appearanceSectionTitle":
            MessageLookupByLibrary.simpleMessage("Apariencia"),
        "bluetoothNotSupportedExplanation":
            MessageLookupByLibrary.simpleMessage(
                "Desde Android 13 ya no es posible desactivar el bluetooth."),
        "bluetoothToggleNotSupportedTitle":
            MessageLookupByLibrary.simpleMessage("Acción bluetooth eliminado"),
        "buttonOpenSavedTimer": MessageLookupByLibrary.simpleMessage(
            "Pulse aquí para ver su temporizador"),
        "buttonSearchAgain":
            MessageLookupByLibrary.simpleMessage("Busca de nuevo"),
        "buttonShowAlarmApps": MessageLookupByLibrary.simpleMessage("Alarma"),
        "buttonShowPlayerApps": MessageLookupByLibrary.simpleMessage("Jugador"),
        "buttonTimerContinue": MessageLookupByLibrary.simpleMessage("Reanudar"),
        "buttonTimerPause": MessageLookupByLibrary.simpleMessage("Pausar"),
        "buttonTimerStart": MessageLookupByLibrary.simpleMessage("Iniciar"),
        "byNumberOfMinutesLong": m0,
        "cannotUninstallDesc": MessageLookupByLibrary.simpleMessage(
            "Para poder desinstalar la aplicación, tienes que desactivar el Administrador de dispositivo en los ajustes."),
        "cannotUninstallTitle":
            MessageLookupByLibrary.simpleMessage("Desinstalar"),
        "chooseThemeTitle": MessageLookupByLibrary.simpleMessage("Tema"),
        "commonDialogOk": MessageLookupByLibrary.simpleMessage("OK"),
        "connectionStateConnected":
            MessageLookupByLibrary.simpleMessage("Conectado"),
        "connectionStateFailed":
            MessageLookupByLibrary.simpleMessage("Fallo de\nconexión"),
        "countDevicesFound": m1,
        "countLights": m2,
        "creditsAppTitle": MessageLookupByLibrary.simpleMessage("Créditos"),
        "creditsLibraries": MessageLookupByLibrary.simpleMessage("Bibliotecas"),
        "dialogCancel": MessageLookupByLibrary.simpleMessage("Cancelar"),
        "dialogConnect": MessageLookupByLibrary.simpleMessage("CONECTAR"),
        "dialogDone": MessageLookupByLibrary.simpleMessage("Aceptar"),
        "dialogUnlink": MessageLookupByLibrary.simpleMessage("UNLINK"),
        "errorNoConnection":
            MessageLookupByLibrary.simpleMessage("Compruebe su conexión"),
        "errorNoDevices": MessageLookupByLibrary.simpleMessage(
            "No se encontraron dispositivos"),
        "extendTimeByShakeMenuToolTip": MessageLookupByLibrary.simpleMessage(
            "Seleccione el tiempo que debe prolongarse por agitación telefónica"),
        "faqShort": MessageLookupByLibrary.simpleMessage("FAQ"),
        "faqTitle":
            MessageLookupByLibrary.simpleMessage("Preguntas frecuentes"),
        "hintTurnsOffLightAction": MessageLookupByLibrary.simpleMessage(
            "This will turn off the light action."),
        "introAutomateSleepRoutineSubtitle": MessageLookupByLibrary.simpleMessage(
            "¿Estás cansado de ajustar los mismos ajustes cada noche para poder dormir bien?"),
        "introAutomateSleepRoutineTitle": MessageLookupByLibrary.simpleMessage(
            "Automatice su rutina de sueño"),
        "introButtonDone": MessageLookupByLibrary.simpleMessage("LISTO"),
        "introButtonNext": MessageLookupByLibrary.simpleMessage("Siguiente"),
        "introButtonSkip": MessageLookupByLibrary.simpleMessage("SALTAR"),
        "introGoodNightSubtitle": MessageLookupByLibrary.simpleMessage(
            "Sólo ajusta el temporizador y listo. Relájate y deja que tus sueños se hagan realidad..."),
        "introGoodNightTitle":
            MessageLookupByLibrary.simpleMessage("Que tengas una buena noche"),
        "introMediaSubtitle": MessageLookupByLibrary.simpleMessage(
            "¿Te gusta escuchar música o ver una película mientras te quedas dormido?"),
        "introMediaTitle":
            MessageLookupByLibrary.simpleMessage("Escucha tu música favorita"),
        "introNoInterruptionsSubtitle": MessageLookupByLibrary.simpleMessage(
            "No se distraiga con los mensajes o notificaciones entrantes."),
        "introNoInterruptionsTitle":
            MessageLookupByLibrary.simpleMessage("Sin interrupciones"),
        "linkBridge": MessageLookupByLibrary.simpleMessage("Link el puente"),
        "linkBridgeInstruction": MessageLookupByLibrary.simpleMessage(
            "1. Presiona el botón del puente\n2. Pulse en conectar"),
        "linkingFailed":
            MessageLookupByLibrary.simpleMessage("Error de conexión"),
        "linkingPending": MessageLookupByLibrary.simpleMessage("Conectando"),
        "linkingState": m3,
        "linkingUnknownError":
            MessageLookupByLibrary.simpleMessage("Error desconocido"),
        "longPressToAdjustDesc": MessageLookupByLibrary.simpleMessage(
            "Algunas acciones se pueden configurar. Mantenga pulsada la acción para ajustarla."),
        "longPressToAdjustTitle":
            MessageLookupByLibrary.simpleMessage("Mantenga pulsada"),
        "noLightsFound": MessageLookupByLibrary.simpleMessage(
            "No se encontraron luces de Philips Hue"),
        "notSupported": MessageLookupByLibrary.simpleMessage(
            "No es compatible con el dispositivo"),
        "notificationActionCancel":
            MessageLookupByLibrary.simpleMessage("Cancelar"),
        "notificationActionContinue":
            MessageLookupByLibrary.simpleMessage("Reanudar"),
        "notificationActionPause":
            MessageLookupByLibrary.simpleMessage("Pausar"),
        "notificationActionRestart":
            MessageLookupByLibrary.simpleMessage("Reiniciar"),
        "notificationNoActionsExecuted": MessageLookupByLibrary.simpleMessage(
            "No hay acciones seleccionadas para la ejecución"),
        "notificationStatusActive":
            MessageLookupByLibrary.simpleMessage("Sleep Timer está activo"),
        "notificationStatusElapsed":
            MessageLookupByLibrary.simpleMessage("Sleep Timer detenido"),
        "notificationStatusPausing":
            MessageLookupByLibrary.simpleMessage("Sleep Timer en pausa"),
        "notificationTimeLeft":
            MessageLookupByLibrary.simpleMessage("Tiempo restante: %s"),
        "notificationsRequired":
            MessageLookupByLibrary.simpleMessage("Se requieren notificaciones"),
        "numberOfMinutesLong": m4,
        "numberOfMinutesShort": m5,
        "otherSectionTitle": MessageLookupByLibrary.simpleMessage("Otro"),
        "prefsDeviceAdmin": MessageLookupByLibrary.simpleMessage(
            "Administrador del dispositivo"),
        "prefsDeviceAdminDescription": MessageLookupByLibrary.simpleMessage(
            "Necesario para apagar la pantalla. Desactivar antes de desinstalar."),
        "prefsExtendTimeOnShake":
            MessageLookupByLibrary.simpleMessage("Agitar para añadir tiempo"),
        "prefsHintEnableAccessToNotificationSettings":
            MessageLookupByLibrary.simpleMessage(
                "Por favor, permite el acceso a No molestar"),
        "prefsHintEnableDeviceAdmin": MessageLookupByLibrary.simpleMessage(
            "Por favor, permite el administrador del dispositivo"),
        "prefsNotificationSettingsAccess":
            MessageLookupByLibrary.simpleMessage("Acceso a No molestar"),
        "prefsNotificationSettingsAccessDescription":
            MessageLookupByLibrary.simpleMessage(
                "Necesario para la acción de no molestar"),
        "purchasesSectionTitle":
            MessageLookupByLibrary.simpleMessage("Compras"),
        "quickLaunchTitle":
            MessageLookupByLibrary.simpleMessage("Abrir rápido"),
        "rateAppPrice": MessageLookupByLibrary.simpleMessage("Gratis"),
        "rateAppSubtitle": MessageLookupByLibrary.simpleMessage(
            "Agradezco cualquier valoración"),
        "rateAppTitle":
            MessageLookupByLibrary.simpleMessage("¿Te gusta la app?"),
        "setToValue": m6,
        "setVolumeTitle":
            MessageLookupByLibrary.simpleMessage("Ajustar el volumen"),
        "settings": MessageLookupByLibrary.simpleMessage("Ajustes"),
        "showTimerGlow": MessageLookupByLibrary.simpleMessage(
            "Efecto de brillo del temporizador"),
        "showTimerGlowDescription": MessageLookupByLibrary.simpleMessage(
            "Muestra un efecto de brillo alrededor de la barra de progreso"),
        "sleepTimer": MessageLookupByLibrary.simpleMessage("Sleep Timer"),
        "stateSearching": MessageLookupByLibrary.simpleMessage("Buscando ..."),
        "tapToChangeTime":
            MessageLookupByLibrary.simpleMessage("Tap to change time"),
        "tapToConnect":
            MessageLookupByLibrary.simpleMessage("Pulse para\nconectarse"),
        "tapToToggleDesc": MessageLookupByLibrary.simpleMessage(
            "Acciones de inicio se ejecutarán en el siguiente inicio del temporizador."),
        "tapToToggleTitle":
            MessageLookupByLibrary.simpleMessage("Pulse para activar"),
        "timerEndsActionsTitle":
            MessageLookupByLibrary.simpleMessage("Cuando se detiene"),
        "timerNoAlarm": MessageLookupByLibrary.simpleMessage(
            "El temporizador no funciona a veces"),
        "timerNoAlarmDescription": MessageLookupByLibrary.simpleMessage(
            "Asegúrate de que la optimización de la batería está desactivada para la aplicación. Algunos fabricantes de dispositivos como Samsung o Huawei requieren ajustes adicionales para permitir que la aplicación funcione en segundo plano"),
        "timerNoBluetooth":
            MessageLookupByLibrary.simpleMessage("No opción de Bluetooth"),
        "timerNoBluetoothDescription": MessageLookupByLibrary.simpleMessage(
            "Desde Android 13 ya no es posible desactivar el bluetooth."),
        "timerNoWifi":
            MessageLookupByLibrary.simpleMessage("No opción de Wifi"),
        "timerNoWifiDescription": MessageLookupByLibrary.simpleMessage(
            "A partir de Android 10 ya no es posible desactivar el wifi."),
        "timerSettingsSectionTitle":
            MessageLookupByLibrary.simpleMessage("Temporizador"),
        "timerStartsActionsTitle":
            MessageLookupByLibrary.simpleMessage("Cuando se inicia"),
        "titleLightGroups":
            MessageLookupByLibrary.simpleMessage("Apagar la luz"),
        "unlinkBridgeName": m7
      };
}
