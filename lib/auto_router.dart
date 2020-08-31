import 'package:auto_route/auto_route_annotations.dart';
import 'package:sleep_timer/views/alarm_detail.dart';
import 'package:sleep_timer/views/navigation.dart';
import 'package:sleep_timer/views/settings.dart';
import 'package:sleep_timer/views/timer_detail.dart';

@MaterialAutoRouter(
  routes: <AutoRoute>[
    MaterialRoute(page: NavigationPage, initial: true),
    MaterialRoute(page: SettingsPage),
    MaterialRoute(page: TimerDetailPage),
    MaterialRoute(page: AlarmDetailPage),
  ],
)
class $AutoRouter {}
