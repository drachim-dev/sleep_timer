import 'dart:ui';

import 'package:android_alarm_manager/android_alarm_manager.dart';
import 'package:device_functions/device_functions.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_config/flutter_config.dart';
import 'package:in_app_purchase/in_app_purchase.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/app/auto_router.gr.dart';
import 'package:sleep_timer/ui/views/home_view.dart';
import 'package:stacked/stacked.dart';
import 'package:stacked_services/stacked_services.dart';

import 'app/locator.dart';
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

  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return ViewModelBuilder<MyAppViewModel>.reactive(
        viewModelBuilder: () => MyAppViewModel(),
        builder: (context, model, child) {
          return MaterialApp(
            theme: model.themeData,
            title: 'Sleep timer',
            navigatorKey: locator<NavigationService>().navigatorKey,
            onGenerateRoute: AutoRouter(),
            home: HomeView(),
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

    // load environment variables
    await FlutterConfig.loadEnvVariables();

    // init inapp purchases
    InAppPurchaseConnection.enablePendingPurchases();

    // ignore: await_only_futures
    await setupLocator();

    AndroidAlarmManager.initialize();
    _initMethodChannel();
    DeviceFunctions.init(onDeviceFunctionsCallback);
  }

  static _initMethodChannel() async {
    final CallbackHandle callback =
        PluginUtilities.getCallbackHandle(onNativeSideCallback);
    kMethodChannel.invokeMethod(
        'initMainActivityEntry', callback.toRawHandle());
  }
}

void onNativeSideCallback() {
  WidgetsFlutterBinding.ensureInitialized();

  kMethodChannel.setMethodCallHandler((MethodCall call) async {
    print('On Dart side: ${call.method}');

    switch (call.method) {
      case "updateWidget":
        final result = 7.0;
        final id = call.arguments;

        return {
          // Pass back the id of the widget so we can update it later
          'id': id,
          'value': result,
        };
      default:
        throw MissingPluginException('notImplemented');
    }
  });
}

void onDeviceFunctionsCallback() async {
  WidgetsFlutterBinding.ensureInitialized();

  DeviceFunctions.channel.setMethodCallHandler((MethodCall call) async {
    print('On Dart side2: ${call.method}');

    switch (call.method) {
      case "NOTIF_ACTION_RESTART":
        final id = call.arguments;
        print("Restart timer");
        return {
          'id': id,
        };
      default:
        throw MissingPluginException('notImplemented');
    }
  });
}
