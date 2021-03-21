import 'dart:async';
import 'dart:ui';

import 'package:device_functions/messages_generated.dart';
import 'package:device_functions/platform_impl.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_crashlytics/firebase_crashlytics.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:google_mobile_ads/google_mobile_ads.dart';
import 'package:in_app_purchase/in_app_purchase.dart';
import 'package:logger/logger.dart';
import 'package:material_design_icons_flutter/material_design_icons_flutter.dart';
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

  ErrorWidget.builder = _buildErrorWidget;

  runZonedGuarded(() {
    runApp(MyApp());
  }, FirebaseCrashlytics.instance.recordError);
}

Builder _buildErrorWidget(FlutterErrorDetails details) {
  var message = 'Error occured!\n\n' + details.exception.toString() + '\n\n';
  var stackTrace = details.stack.toString().split('\n');

  return Builder(builder: (context) {
    final theme = Theme.of(context);
    return kDebugMode
        ? SingleChildScrollView(child: ErrorWidget('$message $stackTrace'))
        : Container(
            color: Colors.transparent,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                Icon(MdiIcons.googleDownasaur, size: 64),
                Text(
                  "Oops, this shouldn't have happened.\nPlease try again!",
                  style: theme.textTheme.headline6,
                  textAlign: TextAlign.center,
                ),
              ],
            ),
          );
  });
}

final mainScaffoldMessengerKey = GlobalKey<ScaffoldMessengerState>();

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return ViewModelBuilder<MyAppViewModel>.reactive(
        viewModelBuilder: () => MyAppViewModel(),
        builder: (context, model, child) {
          return MaterialApp(
            theme: model.theme,
            title: kAppTitle,
            scaffoldMessengerKey: mainScaffoldMessengerKey,
            navigatorKey: StackedService.navigatorKey,
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
    final savedTheme = _prefsService.getString(kPrefKeyTheme);
    if (savedTheme != null) _themeService.setTheme(savedTheme);

    final savedGlow = _prefsService.getBool(kPrefKeyGlow);
    if (savedGlow != null) _themeService.setShowGlow(savedGlow);
  }

  @override
  List<ReactiveServiceMixin> get reactiveServices => [_themeService];
}

class Application {
  final Logger log = getLogger();

  static Future<void> init({Function onCallBack}) async {
    WidgetsFlutterBinding.ensureInitialized();

    // init Logger
    Logger.level = Level.debug;

    // init Firebase Core
    await Firebase.initializeApp();

    // Enable crashlytics only in release mode
    await FirebaseCrashlytics.instance
        .setCrashlyticsCollectionEnabled(!kDebugMode);

    // Pass all uncaught errors to Crashlytics.
    Function originalOnError = FlutterError.onError;
    FlutterError.onError = (FlutterErrorDetails errorDetails) async {
      await FirebaseCrashlytics.instance.recordFlutterError(errorDetails);
      // Forward to original handler.
      originalOnError(errorDetails);
    };

    // init In-App purchases
    InAppPurchaseConnection.enablePendingPurchases();

    // init Google Mobile Ads
    // ignore: unawaited_futures
    MobileAds.instance.initialize();

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
