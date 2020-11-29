import 'package:auto_route/auto_route_annotations.dart';
import 'package:sleep_timer/ui/views/credits_view.dart';
import 'package:sleep_timer/ui/views/faq_view.dart';
import 'package:sleep_timer/ui/views/home_view.dart';
import 'package:sleep_timer/ui/views/intro_view.dart';
import 'package:sleep_timer/ui/views/settings_view.dart';
import 'package:sleep_timer/ui/views/timer_view.dart';

@MaterialAutoRouter(
  routes: <AutoRoute>[
    MaterialRoute(page: IntroView),
    MaterialRoute(page: HomeView),
    MaterialRoute(page: TimerView),
    MaterialRoute(page: SettingsView),
    MaterialRoute(page: FAQView),
    MaterialRoute(page: CreditsView),
  ],
)
class $AutoRouter {}
