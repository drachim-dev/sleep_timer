import 'dart:ui';

import 'package:device_functions/device_functions_old.dart';
import 'package:device_functions/device_functions_platform_interface.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:in_app_purchase/in_app_purchase.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/app/auto_router.gr.dart';
import 'package:sleep_timer/messages_generated.dart';
import 'package:sleep_timer/platform_impl.dart';
import 'package:sleep_timer/platform_interface.dart';
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

    // initialize DeviceFunctionsPlugin for callback
    final CallbackHandle deviceFunctionsCallback =
        PluginUtilities.getCallbackHandle(onDeviceFunctionsCallback);
    DeviceFunctionsPlatform.getInstance().init(deviceFunctionsCallback.toRawHandle());

    // initialize PlatformChannel for callback
    final CallbackHandle sleepTimerCallback =
    PluginUtilities.getCallbackHandle(onNativeSideCallback);
    SleepTimerPlatform.getInstance().init(sleepTimerCallback.toRawHandle());

    // setup callback even when activity is destroyed
    FlutterTimerApi.setup(FlutterApiHandler(onNativeSideCallback));
  }
}

void onNativeSideCallback() async {
  WidgetsFlutterBinding.ensureInitialized();

  print('onNativeSideCallback');
}

void onDeviceFunctionsCallback() async {
  WidgetsFlutterBinding.ensureInitialized();

  print('onDeviceFunctionsCallback');

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