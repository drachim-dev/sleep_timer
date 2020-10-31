// GENERATED CODE - DO NOT MODIFY BY HAND

// **************************************************************************
// AutoRouteGenerator
// **************************************************************************

// ignore_for_file: public_member_api_docs

import 'package:auto_route/auto_route.dart';
import 'package:flutter/material.dart';

import '../model/timer_model.dart';
import '../ui/views/alarm_detail_view.dart';
import '../ui/views/credits_view.dart';
import '../ui/views/faq_view.dart';
import '../ui/views/home_view.dart';
import '../ui/views/settings_view.dart';
import '../ui/views/timer_detail_view.dart';

class Routes {
  static const String homeView = '/home-view';
  static const String settingsView = '/settings-view';
  static const String timerDetailView = '/timer-detail-view';
  static const String alarmDetailView = '/alarm-detail-view';
  static const String fAQView = '/f-aq-view';
  static const String creditsView = '/credits-view';
  static const all = <String>{
    homeView,
    settingsView,
    timerDetailView,
    alarmDetailView,
    fAQView,
    creditsView,
  };
}

class AutoRouter extends RouterBase {
  @override
  List<RouteDef> get routes => _routes;
  final _routes = <RouteDef>[
    RouteDef(Routes.homeView, page: HomeView),
    RouteDef(Routes.settingsView, page: SettingsView),
    RouteDef(Routes.timerDetailView, page: TimerDetailView),
    RouteDef(Routes.alarmDetailView, page: AlarmDetailView),
    RouteDef(Routes.fAQView, page: FAQView),
    RouteDef(Routes.creditsView, page: CreditsView),
  ];
  @override
  Map<Type, AutoRouteFactory> get pagesMap => _pagesMap;
  final _pagesMap = <Type, AutoRouteFactory>{
    HomeView: (data) {
      return MaterialPageRoute<dynamic>(
        builder: (context) => HomeView(),
        settings: data,
      );
    },
    SettingsView: (data) {
      return MaterialPageRoute<dynamic>(
        builder: (context) => SettingsView(),
        settings: data,
      );
    },
    TimerDetailView: (data) {
      final args = data.getArgs<TimerDetailViewArguments>(nullOk: false);
      return MaterialPageRoute<dynamic>(
        builder: (context) => TimerDetailView(
          key: args.key,
          timerModel: args.timerModel,
        ),
        settings: data,
      );
    },
    AlarmDetailView: (data) {
      return MaterialPageRoute<dynamic>(
        builder: (context) => AlarmDetailView(),
        settings: data,
      );
    },
    FAQView: (data) {
      return MaterialPageRoute<dynamic>(
        builder: (context) => FAQView(),
        settings: data,
      );
    },
    CreditsView: (data) {
      return MaterialPageRoute<dynamic>(
        builder: (context) => CreditsView(),
        settings: data,
      );
    },
  };
}

/// ************************************************************************
/// Arguments holder classes
/// *************************************************************************

/// TimerDetailView arguments holder class
class TimerDetailViewArguments {
  final Key key;
  final TimerModel timerModel;
  TimerDetailViewArguments({this.key, @required this.timerModel});
}
