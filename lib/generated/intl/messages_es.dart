// DO NOT EDIT. This is code generated via package:intl/generate_localized.dart
// This is a library that provides messages for a es locale. All the
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
  String get localeName => 'es';

  static m0(count) => "${Intl.plural(count, one: '1 dispositivo encontrado', other: '${count} dispositivos encontrados')}";

  static m1(count) => "${count} luces";

  static m2(time) => "${time} expirado. ";

  static m3(timeLeft) => "Tiempo restante: ${timeLeft}";

  static m4(time) => "Temporizador ajustado a ${time} minutos";

  static m5(minutes) => "${minutes} minutos";

  static m6(minutes) => "${minutes} min";

  static m7(durationString) => "${durationString} minutos";

  static m8(bridge) => "Unlink ${bridge}";

  final messages = _notInlinedMessages(_notInlinedMessages);
  static _notInlinedMessages(_) => <String, Function> {
    "IntroMediaSubtitle" : MessageLookupByLibrary.simpleMessage("¿Te gusta escuchar música o ver una película mientras te quedas dormido?"),
    "actionDoNotDisturbDescription" : MessageLookupByLibrary.simpleMessage("Activar No molestar"),
    "actionDoNotDisturbTitle" : MessageLookupByLibrary.simpleMessage("No molestar"),
    "actionPlayMusicTitle" : MessageLookupByLibrary.simpleMessage("Toca música"),
    "actionToggleBluetoothDescription" : MessageLookupByLibrary.simpleMessage("Desactivar el bluetooth"),
    "actionToggleBluetoothTitle" : MessageLookupByLibrary.simpleMessage("Bluetooth"),
    "actionToggleLightDescription" : MessageLookupByLibrary.simpleMessage("Apaga la luz"),
    "actionToggleLightTitle" : MessageLookupByLibrary.simpleMessage("Luz"),
    "actionToggleMediaDescription" : MessageLookupByLibrary.simpleMessage("Detener la reproducción de medios"),
    "actionToggleMediaTitle" : MessageLookupByLibrary.simpleMessage("Media"),
    "actionToggleScreenDescription" : MessageLookupByLibrary.simpleMessage("Apagar la pantalla"),
    "actionToggleScreenTitle" : MessageLookupByLibrary.simpleMessage("Pantalla"),
    "actionToggleWifiDescription" : MessageLookupByLibrary.simpleMessage("Desactivar wifi"),
    "actionToggleWifiTitle" : MessageLookupByLibrary.simpleMessage("Wifi"),
    "actionVolumeTitle" : MessageLookupByLibrary.simpleMessage("Volumen"),
    "adLoadFailure" : MessageLookupByLibrary.simpleMessage("No se ha cargado el anuncio"),
    "advancedSectionTitle" : MessageLookupByLibrary.simpleMessage("Avanzado"),
    "alreadyPurchased" : MessageLookupByLibrary.simpleMessage("Ya comprado"),
    "appearanceSectionTitle" : MessageLookupByLibrary.simpleMessage("Apariencia"),
    "buttonOpenSavedTimer" : MessageLookupByLibrary.simpleMessage("Toque aquí para ver su temporizador"),
    "buttonSearchAgain" : MessageLookupByLibrary.simpleMessage("Busca de nuevo"),
    "buttonSetupTimer" : MessageLookupByLibrary.simpleMessage("Establezca su rutina"),
    "buttonShowAlarmApps" : MessageLookupByLibrary.simpleMessage("Alarma"),
    "buttonShowPlayerApps" : MessageLookupByLibrary.simpleMessage("Jugador"),
    "buttonTimerContinue" : MessageLookupByLibrary.simpleMessage("Continúa"),
    "buttonTimerPause" : MessageLookupByLibrary.simpleMessage("Pausa"),
    "buttonTimerStart" : MessageLookupByLibrary.simpleMessage("Iniciar"),
    "chooseThemeTitle" : MessageLookupByLibrary.simpleMessage("Tema"),
    "connectionStateConnected" : MessageLookupByLibrary.simpleMessage("Conectado"),
    "connectionStateFailed" : MessageLookupByLibrary.simpleMessage("Fallo de\nconexión"),
    "countDevicesFound" : m0,
    "countLights" : m1,
    "creditsAppTitle" : MessageLookupByLibrary.simpleMessage("Créditos"),
    "creditsLibraries" : MessageLookupByLibrary.simpleMessage("Bibliotecas"),
    "dialogCancel" : MessageLookupByLibrary.simpleMessage("CANCELAR"),
    "dialogConnect" : MessageLookupByLibrary.simpleMessage("CONECTAR"),
    "dialogDone" : MessageLookupByLibrary.simpleMessage("HECHO"),
    "dialogUnlink" : MessageLookupByLibrary.simpleMessage("UNLINK"),
    "errorNoConnection" : MessageLookupByLibrary.simpleMessage("Compruebe su conexión"),
    "errorNoDevices" : MessageLookupByLibrary.simpleMessage("No se encontraron dispositivos"),
    "extendTimeByShakeMenuToolTip" : MessageLookupByLibrary.simpleMessage("Toque para cambiar el tiempo"),
    "faqShort" : MessageLookupByLibrary.simpleMessage("FAQ"),
    "faqTitle" : MessageLookupByLibrary.simpleMessage("Preguntas frecuentes"),
    "hintTurnsOffLightAction" : MessageLookupByLibrary.simpleMessage("This will turn off the light action."),
    "introAutomateSleepRoutineSubtitle" : MessageLookupByLibrary.simpleMessage("¿Estás cansado de ajustar los mismos ajustes cada noche para dormir bien?"),
    "introAutomateSleepRoutineTitle" : MessageLookupByLibrary.simpleMessage("Automatice su rutina diaria de sueño"),
    "introButtonDone" : MessageLookupByLibrary.simpleMessage("HECHO"),
    "introButtonNext" : MessageLookupByLibrary.simpleMessage("Siguiente"),
    "introButtonSkip" : MessageLookupByLibrary.simpleMessage("SÁLTATE"),
    "introGoodNightSubtitle" : MessageLookupByLibrary.simpleMessage("Sólo ajusta el temporizador y listo. Relájate y deja que tus sueños se hagan realidad..."),
    "introGoodNightTitle" : MessageLookupByLibrary.simpleMessage("Que tengas una buena noche"),
    "introMediaTitle" : MessageLookupByLibrary.simpleMessage("Escucha tu música favorita"),
    "introNoInterruptionsSubtitle" : MessageLookupByLibrary.simpleMessage("No se distraiga con los mensajes o notificaciones entrantes"),
    "introNoInterruptionsTitle" : MessageLookupByLibrary.simpleMessage("Sin interrupciones"),
    "linkBridge" : MessageLookupByLibrary.simpleMessage("Link el puente"),
    "linkBridgeInstruction" : MessageLookupByLibrary.simpleMessage("1. Presiona el botón del puente\n2. Pulse en conectar"),
    "longPressToAdjust" : MessageLookupByLibrary.simpleMessage("Long press to adjust settings"),
    "noLightsFound" : MessageLookupByLibrary.simpleMessage("No se encontraron luces"),
    "notSupported" : MessageLookupByLibrary.simpleMessage("No es compatible con el dispositivo"),
    "notificationActionCancel" : MessageLookupByLibrary.simpleMessage("Cancelar"),
    "notificationActionContinue" : MessageLookupByLibrary.simpleMessage("Continúa"),
    "notificationActionPause" : MessageLookupByLibrary.simpleMessage("Pausa"),
    "notificationActionRestart" : MessageLookupByLibrary.simpleMessage("Reiniciar"),
    "notificationNoActionsExecuted" : MessageLookupByLibrary.simpleMessage("No hay acciones seleccionadas para la ejecución"),
    "notificationStatusElapsed" : MessageLookupByLibrary.simpleMessage("Temporizador de sueño transcurrido"),
    "notificationStatusPausing" : MessageLookupByLibrary.simpleMessage("Temporizador de sueño en pausa"),
    "notificationStatusRunning" : MessageLookupByLibrary.simpleMessage("Temporizador de sueño funcionando"),
    "notificationTimeExpired" : m2,
    "notificationTimeLeft" : m3,
    "notificationTimerSet" : m4,
    "numberOfMinutesLong" : m5,
    "numberOfMinutesShort" : m6,
    "otherSectionTitle" : MessageLookupByLibrary.simpleMessage("Otro"),
    "prefsDeviceAdmin" : MessageLookupByLibrary.simpleMessage("Administrador del dispositivo"),
    "prefsDeviceAdminDescription" : MessageLookupByLibrary.simpleMessage("Permite a la aplicación gestionar las funciones del dispositivo\nPermite de apagar la pantalla"),
    "prefsExtendTimeOnShake" : MessageLookupByLibrary.simpleMessage("Extiende el tiempo agitando"),
    "prefsHintEnableAccessToNotificationSettings" : MessageLookupByLibrary.simpleMessage("Por favor, permite el acceso a No molestar"),
    "prefsHintEnableDeviceAdmin" : MessageLookupByLibrary.simpleMessage("Por favor, permite el administrador del dispositivo"),
    "prefsNotificationSettingsAccess" : MessageLookupByLibrary.simpleMessage("Acceso a No molestar"),
    "prefsNotificationSettingsAccessDescription" : MessageLookupByLibrary.simpleMessage("Permite el acceso a No molestar"),
    "purchasesSectionTitle" : MessageLookupByLibrary.simpleMessage("Compras"),
    "quickLaunchTitle" : MessageLookupByLibrary.simpleMessage("Abrir rápido"),
    "setVolumeTitle" : MessageLookupByLibrary.simpleMessage("Ajustar el volumen"),
    "settings" : MessageLookupByLibrary.simpleMessage("Ajustes"),
    "showTimerGlow" : MessageLookupByLibrary.simpleMessage("Efecto de brillo del temporizador"),
    "showTimerGlowDescription" : MessageLookupByLibrary.simpleMessage("Muestra un efecto de brillo alrededor de la barra de progreso"),
    "sleepTimer" : MessageLookupByLibrary.simpleMessage("Sleep Timer"),
    "stateSearching" : MessageLookupByLibrary.simpleMessage("Buscando ..."),
    "tapToConnect" : MessageLookupByLibrary.simpleMessage("Pulse para\nconectarse"),
    "timerEndsActionsTitle" : MessageLookupByLibrary.simpleMessage("Cuando se acabe"),
    "timerNoAlarm" : MessageLookupByLibrary.simpleMessage("El temporizador no funciona a veces"),
    "timerNoAlarmDescription" : MessageLookupByLibrary.simpleMessage("Asegúrate de que la optimización de la batería está desactivada para la aplicación. Algunos fabricantes de dispositivos como Samsung o Huawei requieren ajustes adicionales para permitir que la aplicación funcione en segundo plano"),
    "timerNoWifi" : MessageLookupByLibrary.simpleMessage("Falta la opción de Wifi"),
    "timerNoWifiDescription" : MessageLookupByLibrary.simpleMessage("A partir de Android 10 ya no es posible desactivar el wifi"),
    "timerSettingsSectionTitle" : MessageLookupByLibrary.simpleMessage("Temporizador"),
    "timerStartsActionsTitle" : MessageLookupByLibrary.simpleMessage("Cuando comienza"),
    "titleLightGroups" : MessageLookupByLibrary.simpleMessage("Grupos de luces"),
    "unitMinute" : m7,
    "unlinkBridgeName" : m8
  };
}
