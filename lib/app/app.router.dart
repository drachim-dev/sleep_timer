// GENERATED CODE - DO NOT MODIFY BY HAND

// **************************************************************************
// StackedRouterGenerator
// **************************************************************************

// ignore_for_file: no_leading_underscores_for_library_prefixes
import 'package:flutter/material.dart' as _i10;
import 'package:flutter/material.dart';
import 'package:sleep_timer/model/timer_model.dart' as _i11;
import 'package:sleep_timer/ui/views/bridge_link_view.dart' as _i8;
import 'package:sleep_timer/ui/views/credits_view.dart' as _i7;
import 'package:sleep_timer/ui/views/faq_view.dart' as _i6;
import 'package:sleep_timer/ui/views/home_view.dart' as _i3;
import 'package:sleep_timer/ui/views/intro_view.dart' as _i2;
import 'package:sleep_timer/ui/views/light_group_view.dart' as _i9;
import 'package:sleep_timer/ui/views/settings_view.dart' as _i5;
import 'package:sleep_timer/ui/views/timer_view.dart' as _i4;
import 'package:stacked/stacked.dart' as _i1;
import 'package:stacked_services/stacked_services.dart' as _i12;

class Routes {
  static const introView = '/intro-view';

  static const homeView = '/home-view';

  static const timerView = '/timer-view';

  static const settingsView = '/settings-view';

  static const fAQView = '/f-aq-view';

  static const creditsView = '/credits-view';

  static const bridgeLinkView = '/bridge-link-view';

  static const lightGroupView = '/light-group-view';

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

class StackedRouter extends _i1.RouterBase {
  final _routes = <_i1.RouteDef>[
    _i1.RouteDef(
      Routes.introView,
      page: _i2.IntroView,
    ),
    _i1.RouteDef(
      Routes.homeView,
      page: _i3.HomeView,
    ),
    _i1.RouteDef(
      Routes.timerView,
      page: _i4.TimerView,
    ),
    _i1.RouteDef(
      Routes.settingsView,
      page: _i5.SettingsView,
    ),
    _i1.RouteDef(
      Routes.fAQView,
      page: _i6.FAQView,
    ),
    _i1.RouteDef(
      Routes.creditsView,
      page: _i7.CreditsView,
    ),
    _i1.RouteDef(
      Routes.bridgeLinkView,
      page: _i8.BridgeLinkView,
    ),
    _i1.RouteDef(
      Routes.lightGroupView,
      page: _i9.LightGroupView,
    ),
  ];

  final _pagesMap = <Type, _i1.StackedRouteFactory>{
    _i2.IntroView: (data) {
      return MaterialPageRoute<dynamic>(
        builder: (context) => _i2.IntroView(),
        settings: data,
      );
    },
    _i3.HomeView: (data) {
      final args = data.getArgs<HomeViewArguments>(
        orElse: () => const HomeViewArguments(),
      );
      return MaterialPageRoute<dynamic>(
        builder: (context) => _i3.HomeView(timerId: args.timerId),
        settings: data,
      );
    },
    _i4.TimerView: (data) {
      final args = data.getArgs<TimerViewArguments>(nullOk: false);
      return MaterialPageRoute<dynamic>(
        builder: (context) =>
            _i4.TimerView(key: args.key, timerModel: args.timerModel),
        settings: data,
      );
    },
    _i5.SettingsView: (data) {
      final args = data.getArgs<SettingsViewArguments>(
        orElse: () => const SettingsViewArguments(),
      );
      return MaterialPageRoute<dynamic>(
        builder: (context) => _i5.SettingsView(
            deviceAdminFocused: args.deviceAdminFocused,
            notificationSettingsAccessFocused:
                args.notificationSettingsAccessFocused),
        settings: data,
      );
    },
    _i6.FAQView: (data) {
      return MaterialPageRoute<dynamic>(
        builder: (context) => _i6.FAQView(),
        settings: data,
      );
    },
    _i7.CreditsView: (data) {
      return MaterialPageRoute<dynamic>(
        builder: (context) => _i7.CreditsView(),
        settings: data,
      );
    },
    _i8.BridgeLinkView: (data) {
      return MaterialPageRoute<dynamic>(
        builder: (context) => _i8.BridgeLinkView(),
        settings: data,
      );
    },
    _i9.LightGroupView: (data) {
      return MaterialPageRoute<dynamic>(
        builder: (context) => _i9.LightGroupView(),
        settings: data,
      );
    },
  };

  @override
  List<_i1.RouteDef> get routes => _routes;
  @override
  Map<Type, _i1.StackedRouteFactory> get pagesMap => _pagesMap;
}

class HomeViewArguments {
  const HomeViewArguments({this.timerId});

  final String? timerId;
}

class TimerViewArguments {
  const TimerViewArguments({
    this.key,
    required this.timerModel,
  });

  final _i10.Key? key;

  final _i11.TimerModel? timerModel;
}

class SettingsViewArguments {
  const SettingsViewArguments({
    this.deviceAdminFocused = false,
    this.notificationSettingsAccessFocused = false,
  });

  final dynamic deviceAdminFocused;

  final dynamic notificationSettingsAccessFocused;
}

extension NavigatorStateExtension on _i12.NavigationService {
  Future<dynamic> navigateToIntroView([
    int? routerId,
    bool preventDuplicates = true,
    Map<String, String>? parameters,
    Widget Function(BuildContext, Animation<double>, Animation<double>, Widget)?
        transition,
  ]) async {
    return navigateTo<dynamic>(Routes.introView,
        id: routerId,
        preventDuplicates: preventDuplicates,
        parameters: parameters,
        transition: transition);
  }

  Future<dynamic> navigateToHomeView({
    String? timerId,
    int? routerId,
    bool preventDuplicates = true,
    Map<String, String>? parameters,
    Widget Function(BuildContext, Animation<double>, Animation<double>, Widget)?
        transition,
  }) async {
    return navigateTo<dynamic>(Routes.homeView,
        arguments: HomeViewArguments(timerId: timerId),
        id: routerId,
        preventDuplicates: preventDuplicates,
        parameters: parameters,
        transition: transition);
  }

  Future<dynamic> navigateToTimerView({
    _i10.Key? key,
    required _i11.TimerModel? timerModel,
    int? routerId,
    bool preventDuplicates = true,
    Map<String, String>? parameters,
    Widget Function(BuildContext, Animation<double>, Animation<double>, Widget)?
        transition,
  }) async {
    return navigateTo<dynamic>(Routes.timerView,
        arguments: TimerViewArguments(key: key, timerModel: timerModel),
        id: routerId,
        preventDuplicates: preventDuplicates,
        parameters: parameters,
        transition: transition);
  }

  Future<dynamic> navigateToSettingsView({
    dynamic deviceAdminFocused = false,
    dynamic notificationSettingsAccessFocused = false,
    int? routerId,
    bool preventDuplicates = true,
    Map<String, String>? parameters,
    Widget Function(BuildContext, Animation<double>, Animation<double>, Widget)?
        transition,
  }) async {
    return navigateTo<dynamic>(Routes.settingsView,
        arguments: SettingsViewArguments(
            deviceAdminFocused: deviceAdminFocused,
            notificationSettingsAccessFocused:
                notificationSettingsAccessFocused),
        id: routerId,
        preventDuplicates: preventDuplicates,
        parameters: parameters,
        transition: transition);
  }

  Future<dynamic> navigateToFAQView([
    int? routerId,
    bool preventDuplicates = true,
    Map<String, String>? parameters,
    Widget Function(BuildContext, Animation<double>, Animation<double>, Widget)?
        transition,
  ]) async {
    return navigateTo<dynamic>(Routes.fAQView,
        id: routerId,
        preventDuplicates: preventDuplicates,
        parameters: parameters,
        transition: transition);
  }

  Future<dynamic> navigateToCreditsView([
    int? routerId,
    bool preventDuplicates = true,
    Map<String, String>? parameters,
    Widget Function(BuildContext, Animation<double>, Animation<double>, Widget)?
        transition,
  ]) async {
    return navigateTo<dynamic>(Routes.creditsView,
        id: routerId,
        preventDuplicates: preventDuplicates,
        parameters: parameters,
        transition: transition);
  }

  Future<dynamic> navigateToBridgeLinkView([
    int? routerId,
    bool preventDuplicates = true,
    Map<String, String>? parameters,
    Widget Function(BuildContext, Animation<double>, Animation<double>, Widget)?
        transition,
  ]) async {
    return navigateTo<dynamic>(Routes.bridgeLinkView,
        id: routerId,
        preventDuplicates: preventDuplicates,
        parameters: parameters,
        transition: transition);
  }

  Future<dynamic> navigateToLightGroupView([
    int? routerId,
    bool preventDuplicates = true,
    Map<String, String>? parameters,
    Widget Function(BuildContext, Animation<double>, Animation<double>, Widget)?
        transition,
  ]) async {
    return navigateTo<dynamic>(Routes.lightGroupView,
        id: routerId,
        preventDuplicates: preventDuplicates,
        parameters: parameters,
        transition: transition);
  }
}
