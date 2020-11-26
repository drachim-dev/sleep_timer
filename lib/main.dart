import 'dart:ui';

import 'package:device_functions/messages_generated.dart';
import 'package:device_functions/platform_impl.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:in_app_purchase/in_app_purchase.dart';
import 'package:logger/logger.dart';
import 'package:shake/shake.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:sleep_timer/app/auto_router.gr.dart';
import 'package:sleep_timer/app/logger.util.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/messages_generated.dart';
import 'package:sleep_timer/platform_impl.dart';
import 'package:sleep_timer/platform_interface.dart';
import 'package:sleep_timer/services/device_service.dart';
import 'package:stacked/stacked.dart';
import 'package:stacked_services/stacked_services.dart';

import 'app/locator.dart';
import 'common/timer_service_manager.dart';
import 'generated/l10n.dart';
import 'services/theme_service.dart';

Future<void> main() async {
  await Application.init();

  ErrorWidget.builder = (FlutterErrorDetails details) {
    var inDebug = false;
    assert(() {
      inDebug = true;
      return true;
    }());

    return inDebug
        ? ErrorWidget(details.stack)
        : SingleChildScrollView(child: ErrorWidget(details.stack));
  };
  runApp(MyApp());
}

final mainScaffoldMessengerKey = GlobalKey<ScaffoldMessengerState>();

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return ViewModelBuilder<MyAppViewModel>.reactive(
        viewModelBuilder: () => MyAppViewModel(),
        onModelReady: (model) {
          final systemUiOverlayStyle =
              model.theme.brightness == Brightness.light
                  ? SystemUiOverlayStyle.dark
                  : SystemUiOverlayStyle.light;

          SystemChrome.setSystemUIOverlayStyle(systemUiOverlayStyle.copyWith(
              statusBarColor: Colors.transparent));

          ShakeDetector.autoStart(onPhoneShake: () {
            print('SHAKE IT BABY');
          });
        },
        builder: (context, model, child) {
          return MaterialApp(
            theme: model.theme,
            title: 'Sleep timer',
            scaffoldMessengerKey: mainScaffoldMessengerKey,
            navigatorKey: locator<NavigationService>().navigatorKey,
            onGenerateRoute: AutoRouter(),
            initialRoute:
                model.firstLaunch ? Routes.introView : Routes.homeView,
            localizationsDelegates: [
              S.delegate,
              GlobalMaterialLocalizations.delegate,
              GlobalWidgetsLocalizations.delegate,
              GlobalCupertinoLocalizations.delegate,
            ],
            supportedLocales: [
              const Locale.fromSubtags(languageCode: 'en'),
              const Locale.fromSubtags(languageCode: 'de'),
              const Locale.fromSubtags(languageCode: 'es'),
            ],
            debugShowCheckedModeBanner: false,
          );
        });
  }
}

class MyAppViewModel extends ReactiveViewModel {
  final _prefsService = locator<SharedPreferences>();
  final _themeService = locator<ThemeService>();

  bool get firstLaunch => _prefsService.getBool(kPrefKeyFirstLaunch) ?? true;

  ThemeData get theme => _themeService.myTheme.theme;

  MyAppViewModel() {
    var savedTheme = _prefsService.getString(kPrefKeyTheme);
    if (savedTheme != null) _themeService.updateTheme(savedTheme);

    var savedGlow = _prefsService.getBool(kPrefKeyGlow);
    if (savedGlow != null) _themeService.showGlow = savedGlow;
  }

  @override
  List<ReactiveServiceMixin> get reactiveServices => [_themeService];
}

class Application {
  final Logger log = getLogger();

  static void init({Function onCallBack}) async {
    WidgetsFlutterBinding.ensureInitialized();

    // init Logger
    Logger.level = Level.debug;

    // init In-App purchases
    InAppPurchaseConnection.enablePendingPurchases();

    // ignore: await_only_futures
    await setupLocator();

    // initialize PlatformChannel for callback
    final sleepTimerCallback =
        PluginUtilities.getCallbackHandle(onNativeSideCallback);
    await SleepTimerPlatform.getInstance()
        .init(sleepTimerCallback.toRawHandle());

    // setup callback even when activity is destroyed
    FlutterTimerApi.setup(FlutterApiHandler(
        callback: onNativeSideCallback, alarmCallback: onAlarmCallback));

    FlutterDeviceFunctionsApi.setup(DeviceFunctionsApiHandler(
        onDeviceAdminCallback: onDeviceAdminCallback,
        onNotificationAccessCallback: onNotificationAccessCallback));
  }
}

void onNativeSideCallback() async {
  final log = getLogger();
  log.d(
      '################################## onNativeSideCallback ##################################');

  WidgetsFlutterBinding.ensureInitialized();
}

void onAlarmCallback(final String timerId) async {
  final log = getLogger();
  log.d('onAlarmCallback for $timerId');

  WidgetsFlutterBinding.ensureInitialized();

  final _timerService =
      TimerServiceManager.getInstance().getTimerService(timerId);
  await _timerService.handleEndedActions();
}

void onNativeSideDeviceFunctionsCallback() async {
  final log = getLogger();
  log.d('onNativeSideDeviceFunctionsCallback');

  WidgetsFlutterBinding.ensureInitialized();
}

void onDeviceAdminCallback(final bool granted) async {
  final log = getLogger();
  log.d('onDeviceAdminGrantedCallback');

  WidgetsFlutterBinding.ensureInitialized();

  final _deviceService = locator<DeviceService>();
  _deviceService.setDeviceAdmin(granted);
}

void onNotificationAccessCallback(final bool granted) async {
  final log = getLogger();
  log.d('onNotificationAccessGrantedCallback');

  WidgetsFlutterBinding.ensureInitialized();

  final _deviceService = locator<DeviceService>();
  _deviceService.setNotificationAccess(granted);
}
