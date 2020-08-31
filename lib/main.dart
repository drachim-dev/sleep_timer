import 'package:auto_route/auto_route.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:provider/provider.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/common/theme.dart';
import 'package:sleep_timer/auto_router.gr.dart';
import 'package:sleep_timer/views/navigation.dart';

SharedPreferences prefs;
Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  MethodChannelCall.initMethodChannel();

  prefs = await SharedPreferences.getInstance();
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    final ThemeData theme =
        MyTheme.getThemeFromName(prefs.getString(kPrefKeyTheme));

    return ChangeNotifierProvider<ThemeNotifier>(
        create: (_) => ThemeNotifier(theme),
        builder: (context, _) {
          return MaterialApp(
            builder: ExtendedNavigator<AutoRouter>(router: AutoRouter()),
            theme: Provider.of<ThemeNotifier>(context).theme,
            title: 'Sleep timer',
            home: NavigationPage(),
            debugShowCheckedModeBanner: false,
          );
        });
  }
}

class MethodChannelCall {
  static initMethodChannel({Function onCallBack}) async {
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