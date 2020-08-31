// GENERATED CODE - DO NOT MODIFY BY HAND

// **************************************************************************
// AutoRouteGenerator
// **************************************************************************

// ignore_for_file: public_member_api_docs

import 'package:auto_route/auto_route.dart';
import 'package:flutter/material.dart';

import 'views/alarm_detail.dart';
import 'views/navigation.dart';
import 'views/settings.dart';
import 'views/timer_detail.dart';

class Routes {
  static const String navigationPage = '/';
  static const String settingsPage = '/settings-page';
  static const String timerDetailPage = '/timer-detail-page';
  static const String alarmDetailPage = '/alarm-detail-page';
  static const all = <String>{
    navigationPage,
    settingsPage,
    timerDetailPage,
    alarmDetailPage,
  };
}

class AutoRouter extends RouterBase {
  @override
  List<RouteDef> get routes => _routes;
  final _routes = <RouteDef>[
    RouteDef(Routes.navigationPage, page: NavigationPage),
    RouteDef(Routes.settingsPage, page: SettingsPage),
    RouteDef(Routes.timerDetailPage, page: TimerDetailPage),
    RouteDef(Routes.alarmDetailPage, page: AlarmDetailPage),
  ];
  @override
  Map<Type, AutoRouteFactory> get pagesMap => _pagesMap;
  final _pagesMap = <Type, AutoRouteFactory>{
    NavigationPage: (data) {
      return MaterialPageRoute<dynamic>(
        builder: (context) => NavigationPage(),
        settings: data,
      );
    },
    SettingsPage: (data) {
      return MaterialPageRoute<dynamic>(
        builder: (context) => SettingsPage(),
        settings: data,
      );
    },
    TimerDetailPage: (data) {
      final args = data.getArgs<TimerDetailPageArguments>(nullOk: false);
      return MaterialPageRoute<dynamic>(
        builder: (context) => TimerDetailPage(
          key: args.key,
          initialTime: args.initialTime,
        ),
        settings: data,
      );
    },
    AlarmDetailPage: (data) {
      return MaterialPageRoute<dynamic>(
        builder: (context) => AlarmDetailPage(),
        settings: data,
      );
    },
  };
}

/// ************************************************************************
/// Arguments holder classes
/// *************************************************************************

/// TimerDetailPage arguments holder class
class TimerDetailPageArguments {
  final Key key;
  final int initialTime;
  TimerDetailPageArguments({this.key, @required this.initialTime});
}
