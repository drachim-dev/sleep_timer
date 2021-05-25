// DO NOT EDIT. This is code generated via package:intl/generate_localized.dart
// This is a library that provides messages for a es locale. All the
// messages from the main program should be duplicated here with the same
// function name.

// Ignore issues from commonly used lints in this file.
// ignore_for_file:unnecessary_brace_in_string_interps, unnecessary_new
// ignore_for_file:prefer_single_quotes,comment_references, directives_ordering
// ignore_for_file:annotate_overrides,prefer_generic_function_type_aliases
// ignore_for_file:unused_import, file_names, avoid_escaping_inner_quotes

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

  static String m3(minutes) => "${minutes} minutos";

  static String m4(minutes) => "${minutes} min";

  static String m5(bridge) => "Unlink ${bridge}";

  final messages = _notInlinedMessages(_notInlinedMessages);
  static Map<String, Function> _notInlinedMessages(_) => <String, Function>{
        "IntroMediaSubtitle": MessageLookupByLibrary.simpleMessage(
            "¿Te gusta escuchar música o ver una película mientras te quedas dormido?"),
        "actionDoNotDisturbDescription":
            MessageLookupByLibrary.simpleMessage("Activar No molestar"),
        "actionDoNotDisturbTitle":
            MessageLookupByLibrary.simpleMessage("No molestar"),
        "actionPlayMusicTitle":
            MessageLookupByLibrary.simpleMessage("Toca música"),
        "actionToggleBluetoothDescription":
            MessageLookupByLibrary.simpleMessage("Desactivar el bluetooth"),
        "actionToggleBluetoothTitle":
            MessageLookupByLibrary.simpleMessage("Bluetooth"),
        "actionToggleLightDescription":
            MessageLookupByLibrary.simpleMessage("Apaga la luz de Philips Hue"),
        "actionToggleLightTitle": MessageLookupByLibrary.simpleMessage("Luces"),
        "actionToggleMediaDescription": MessageLookupByLibrary.simpleMessage(
            "Detener la reproducción de medios"),
        "actionToggleMediaTitle": MessageLookupByLibrary.simpleMessage("Media"),
        "actionToggleScreenDescription":
            MessageLookupByLibrary.simpleMessage("Apagar la pantalla"),
        "actionToggleScreenTitle":
            MessageLookupByLibrary.simpleMessage("Pantalla"),
        "actionToggleWifiDescription":
            MessageLookupByLibrary.simpleMessage("Desactivar wifi"),
        "actionToggleWifiTitle": MessageLookupByLibrary.simpleMessage("Wifi"),
        "actionVolumeTitle": MessageLookupByLibrary.simpleMessage("Volumen"),
        "adLoadFailure":
            MessageLookupByLibrary.simpleMessage("No se ha cargado el anuncio"),
        "advancedSectionTitle":
            MessageLookupByLibrary.simpleMessage("Avanzado"),
        "alreadyPurchased": MessageLookupByLibrary.simpleMessage("Ya comprado"),
        "appearanceSectionTitle":
            MessageLookupByLibrary.simpleMessage("Apariencia"),
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
        "extendTimeByShakeMenuToolTip":
            MessageLookupByLibrary.simpleMessage("Pulse para cambiar la hora"),
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
        "introMediaTitle":
            MessageLookupByLibrary.simpleMessage("Escucha tu música favorita"),
        "introNoInterruptionsSubtitle": MessageLookupByLibrary.simpleMessage(
            "No se distraiga con los mensajes o notificaciones entrantes."),
        "introNoInterruptionsTitle":
            MessageLookupByLibrary.simpleMessage("Sin interrupciones"),
        "linkBridge": MessageLookupByLibrary.simpleMessage("Link el puente"),
        "linkBridgeInstruction": MessageLookupByLibrary.simpleMessage(
            "1. Presiona el botón del puente\n2. Pulse en conectar"),
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
        "numberOfMinutesLong": m3,
        "numberOfMinutesShort": m4,
        "otherSectionTitle": MessageLookupByLibrary.simpleMessage("Otro"),
        "prefsDeviceAdmin": MessageLookupByLibrary.simpleMessage(
            "Administrador del dispositivo"),
        "prefsDeviceAdminDescription": MessageLookupByLibrary.simpleMessage(
            "Necesario para poder apagar la pantalla"),
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
        "setVolumeTitle":
            MessageLookupByLibrary.simpleMessage("Ajustar el volumen"),
        "settings": MessageLookupByLibrary.simpleMessage("Ajustes"),
        "showTimerGlow": MessageLookupByLibrary.simpleMessage(
            "Efecto de brillo del temporizador"),
        "showTimerGlowDescription": MessageLookupByLibrary.simpleMessage(
            "Muestra un efecto de brillo alrededor de la barra de progreso"),
        "sleepTimer": MessageLookupByLibrary.simpleMessage("Sleep Timer"),
        "stateSearching": MessageLookupByLibrary.simpleMessage("Buscando ..."),
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
        "timerNoWifi":
            MessageLookupByLibrary.simpleMessage("No opción de Wifi"),
        "timerNoWifiDescription": MessageLookupByLibrary.simpleMessage(
            "A partir de Android 10 ya no es posible desactivar el wifi"),
        "timerSettingsSectionTitle":
            MessageLookupByLibrary.simpleMessage("Temporizador"),
        "timerStartsActionsTitle":
            MessageLookupByLibrary.simpleMessage("Cuando se inicia"),
        "titleLightGroups":
            MessageLookupByLibrary.simpleMessage("Apagar la luz"),
        "unlinkBridgeName": m5
      };
}
