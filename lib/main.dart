import 'dart:ui';

import 'package:device_functions/platform_impl.dart';
import 'package:device_functions/messages_generated.dart';
import 'package:flutter/material.dart';
import 'package:in_app_purchase/in_app_purchase.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/app/auto_router.gr.dart';
import 'package:sleep_timer/messages_generated.dart';
import 'package:sleep_timer/platform_impl.dart';
import 'package:sleep_timer/platform_interface.dart';
import 'package:sleep_timer/services/device_service.dart';
import 'package:sleep_timer/services/timer_service.dart';
import 'package:stacked/stacked.dart';
import 'package:stacked_services/stacked_services.dart';

import 'app/locator.dart';
import 'common/timer_service_manager.dart';
import 'services/theme_service.dart';

Future<void> main() async {
  await Application.init();

  ErrorWidget.builder = (FlutterErrorDetails details) {
    bool inDebug = false;
    assert(() {
      inDebug = true;
      return true;
    }());

    return inDebug
        ? ErrorWidget(details.stack)
        : SingleChildScrollView(child: ErrorWidget(details.stack));
  };
  runApp(MyApp(initialRoute: Routes.homeView));
}

class MyApp extends StatelessWidget {
  final String initialRoute;

  MyApp({this.initialRoute});

  @override
  Widget build(BuildContext context) {
    print(this.initialRoute);

    return ViewModelBuilder<MyAppViewModel>.reactive(
        viewModelBuilder: () => MyAppViewModel(),
        builder: (context, model, child) {
          return MaterialApp(
            theme: model.themeData,
            title: 'Sleep timer',
            navigatorKey: locator<NavigationService>().navigatorKey,
            onGenerateRoute: AutoRouter(),
            initialRoute: initialRoute ?? Routes.homeView,
            debugShowCheckedModeBanner: false,
          );
        });
  }
}

class MyAppViewModel extends ReactiveViewModel {
  final _prefsService = locator<SharedPreferences>();
  final _themeService = locator<ThemeService>();

  ThemeData get themeData => _themeService.myTheme.theme;

  MyAppViewModel() {
    var savedTheme = _prefsService.getString(kPrefKeyTheme);
    if (savedTheme != null) _themeService.updateTheme(savedTheme);
  }

  @override
  List<ReactiveServiceMixin> get reactiveServices => [_themeService];
}

class Application {
  static init({Function onCallBack}) async {
    WidgetsFlutterBinding.ensureInitialized();

    // init In-App purchases
    InAppPurchaseConnection.enablePendingPurchases();

    // ignore: await_only_futures
    await setupLocator();

    // initialize PlatformChannel for callback
    final CallbackHandle sleepTimerCallback =
        PluginUtilities.getCallbackHandle(onNativeSideCallback);
    SleepTimerPlatform.getInstance().init(sleepTimerCallback.toRawHandle());

    // setup callback even when activity is destroyed
    FlutterTimerApi.setup(FlutterApiHandler(callback: onNativeSideCallback, alarmCallback: onAlarmCallback));

    FlutterDeviceFunctionsApi.setup(DeviceFunctionsApiHandler(
        onDeviceAdminCallback: onDeviceAdminCallback,
        onNotificationAccessCallback: onNotificationAccessCallback));
  }
}

void onNativeSideCallback() async {
  WidgetsFlutterBinding.ensureInitialized();

  print('onNativeSideCallback');
}

void onAlarmCallback(final String timerId) async {
  WidgetsFlutterBinding.ensureInitialized();

  print('onAlarmCallback for $timerId');

  final _timerService = TimerServiceManager.getInstance().getTimerService(timerId);
  _timerService.handleAlarm();
}

void onNativeSideDeviceFunctionsCallback() async {
  WidgetsFlutterBinding.ensureInitialized();

  print('onNativeSideDeviceFunctionsCallback');
}

void onDeviceAdminCallback(final bool granted) async {
  WidgetsFlutterBinding.ensureInitialized();

  print('onDeviceAdminGrantedCallback');
  final _deviceService = locator<DeviceService>();
  _deviceService.setDeviceAdmin(granted);
}

void onNotificationAccessCallback(final bool granted) async {
  WidgetsFlutterBinding.ensureInitialized();

  print('onNotificationAccessGrantedCallback');
  final _deviceService = locator<DeviceService>();
  _deviceService.setNotificationAccess(granted);
}
