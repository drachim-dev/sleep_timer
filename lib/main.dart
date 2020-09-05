import 'dart:math';
import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/app/auto_router.gr.dart';
import 'package:sleep_timer/ui/views/home_view.dart';
import 'package:stacked/stacked.dart';
import 'package:stacked_services/stacked_services.dart';

import 'app/locator.dart';
import 'services/theme_service.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  MethodChannel.init();

  // await dependencies to be registered even though it's void and not a future
  await setupLocator();

  ErrorWidget.builder = (FlutterErrorDetails details) {
    bool inDebug = false;
    assert(() {
      inDebug = true;
      return true;
    }());
    // In debug mode, use the normal error widget which shows
    // the error message:
    if (inDebug) return ErrorWidget(details.stack);
    // In release builds, show a yellow-on-blue message instead:
    return SingleChildScrollView(child: ErrorWidget(details.stack));
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
    _themeService.updateTheme(_prefsService.getString(kPrefKeyTheme));
  }

  @override
  List<ReactiveServiceMixin> get reactiveServices => [_themeService];
}

class MethodChannel {
  static init({Function onCallBack}) async {
    final CallbackHandle callback =
        PluginUtilities.getCallbackHandle(onWidgetUpdate);
    final handle = callback.toRawHandle();

    kMethodChannel.invokeMethod('initialize', handle);
  }

  static void onWidgetUpdate() {
    WidgetsFlutterBinding.ensureInitialized();

    kMethodChannel.setMethodCallHandler((MethodCall call) async {
      print(call.method);
      switch (call.method) {
        case "foo":
          return "bar";
        default:
          throw MissingPluginException('notImplemented');
      }
    });
  }
}