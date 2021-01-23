// GENERATED CODE - DO NOT MODIFY BY HAND

// **************************************************************************
// AutoRouteGenerator
// **************************************************************************

// ignore_for_file: public_member_api_docs

import 'package:auto_route/auto_route.dart';
import 'package:flutter/material.dart';

import '../model/timer_model.dart';
import '../ui/views/bridge_link_view.dart';
import '../ui/views/credits_view.dart';
import '../ui/views/faq_view.dart';
import '../ui/views/home_view.dart';
import '../ui/views/intro_view.dart';
import '../ui/views/light_group_view.dart';
import '../ui/views/settings_view.dart';
import '../ui/views/timer_view.dart';

class Routes {
  static const String introView = '/intro-view';
  static const String homeView = '/home-view';
  static const String timerView = '/timer-view';
  static const String settingsView = '/settings-view';
  static const String fAQView = '/f-aq-view';
  static const String creditsView = '/credits-view';
  static const String bridgeLinkView = '/bridge-link-view';
  static const String lightGroupView = '/light-group-view';
  static const all = <String>{
    introView,
    homeView,
    timerView,
    settingsView,
    fAQView,
    creditsView,
    bridgeLinkView,
    lightGroupView,
  };
}

class AutoRouter extends RouterBase {
  @override
  List<RouteDef> get routes => _routes;
  final _routes = <RouteDef>[
    RouteDef(Routes.introView, page: IntroView),
    RouteDef(Routes.homeView, page: HomeView),
    RouteDef(Routes.timerView, page: TimerView),
    RouteDef(Routes.settingsView, page: SettingsView),
    RouteDef(Routes.fAQView, page: FAQView),
    RouteDef(Routes.creditsView, page: CreditsView),
    RouteDef(Routes.bridgeLinkView, page: BridgeLinkView),
    RouteDef(Routes.lightGroupView, page: LightGroupView),
  ];
  @override
  Map<Type, AutoRouteFactory> get pagesMap => _pagesMap;
  final _pagesMap = <Type, AutoRouteFactory>{
    IntroView: (data) {
      return MaterialPageRoute<dynamic>(
        builder: (context) => IntroView(),
        settings: data,
      );
    },
    HomeView: (data) {
      final args = data.getArgs<HomeViewArguments>(
        orElse: () => HomeViewArguments(),
      );
      return MaterialPageRoute<dynamic>(
        builder: (context) => HomeView(timerId: args.timerId),
        settings: data,
      );
    },
    TimerView: (data) {
      final args = data.getArgs<TimerViewArguments>(nullOk: false);
      return MaterialPageRoute<dynamic>(
        builder: (context) => TimerView(
          key: args.key,
          timerModel: args.timerModel,
        ),
        settings: data,
      );
    },
    SettingsView: (data) {
      final args = data.getArgs<SettingsViewArguments>(
        orElse: () => SettingsViewArguments(),
      );
      return MaterialPageRoute<dynamic>(
        builder: (context) => SettingsView(
          deviceAdminFocused: args.deviceAdminFocused,
          notificationSettingsAccessFocused:
              args.notificationSettingsAccessFocused,
        ),
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
    BridgeLinkView: (data) {
      return MaterialPageRoute<dynamic>(
        builder: (context) => BridgeLinkView(),
        settings: data,
      );
    },
    LightGroupView: (data) {
      return MaterialPageRoute<dynamic>(
        builder: (context) => LightGroupView(),
        settings: data,
      );
    },
  };
}

/// ************************************************************************
/// Arguments holder classes
/// *************************************************************************

/// HomeView arguments holder class
class HomeViewArguments {
  final String timerId;
  HomeViewArguments({this.timerId});
}

/// TimerView arguments holder class
class TimerViewArguments {
  final Key key;
  final TimerModel timerModel;
  TimerViewArguments({this.key, @required this.timerModel});
}

/// SettingsView arguments holder class
class SettingsViewArguments {
  final dynamic deviceAdminFocused;
  final dynamic notificationSettingsAccessFocused;
  SettingsViewArguments(
      {this.deviceAdminFocused = false,
      this.notificationSettingsAccessFocused = false});
}
