// GENERATED CODE - DO NOT MODIFY BY HAND

// **************************************************************************
// AutoRouteGenerator
// **************************************************************************

// ignore_for_file: public_member_api_docs

import 'package:auto_route/auto_route.dart';
import 'package:flutter/material.dart';

import '../model/timer_model.dart';
import '../ui/views/credits_view.dart';
import '../ui/views/faq_view.dart';
import '../ui/views/home_view.dart';
import '../ui/views/intro_view.dart';
import '../ui/views/settings_view.dart';
import '../ui/views/spotify_auth_view.dart';
import '../ui/views/timer_view.dart';

class Routes {
  static const String introView = '/intro-view';
  static const String homeView = '/home-view';
  static const String timerView = '/timer-view';
  static const String spotifyAuthView = '/spotify-auth-view';
  static const String settingsView = '/settings-view';
  static const String fAQView = '/f-aq-view';
  static const String creditsView = '/credits-view';
  static const all = <String>{
    introView,
    homeView,
    timerView,
    spotifyAuthView,
    settingsView,
    fAQView,
    creditsView,
  };
}

class AutoRouter extends RouterBase {
  @override
  List<RouteDef> get routes => _routes;
  final _routes = <RouteDef>[
    RouteDef(Routes.introView, page: IntroView),
    RouteDef(Routes.homeView, page: HomeView),
    RouteDef(Routes.timerView, page: TimerView),
    RouteDef(Routes.spotifyAuthView, page: SpotifyAuthView),
    RouteDef(Routes.settingsView, page: SettingsView),
    RouteDef(Routes.fAQView, page: FAQView),
    RouteDef(Routes.creditsView, page: CreditsView),
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
      return MaterialPageRoute<dynamic>(
        builder: (context) => HomeView(),
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
    SpotifyAuthView: (data) {
      final args = data.getArgs<SpotifyAuthViewArguments>(nullOk: false);
      return MaterialPageRoute<dynamic>(
        builder: (context) => SpotifyAuthView(
          url: args.url,
          redirectUrl: args.redirectUrl,
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
  };
}

/// ************************************************************************
/// Arguments holder classes
/// *************************************************************************

/// TimerView arguments holder class
class TimerViewArguments {
  final Key key;
  final TimerModel timerModel;
  TimerViewArguments({this.key, @required this.timerModel});
}

/// SpotifyAuthView arguments holder class
class SpotifyAuthViewArguments {
  final String url;
  final String redirectUrl;
  SpotifyAuthViewArguments({@required this.url, @required this.redirectUrl});
}

/// SettingsView arguments holder class
class SettingsViewArguments {
  final dynamic deviceAdminFocused;
  final dynamic notificationSettingsAccessFocused;
  SettingsViewArguments(
      {this.deviceAdminFocused = false,
      this.notificationSettingsAccessFocused = false});
}
