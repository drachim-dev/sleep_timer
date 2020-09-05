import 'package:auto_route/auto_route_annotations.dart';
import 'package:sleep_timer/ui/views/alarm_detail_view.dart';
import 'package:sleep_timer/ui/views/home_view.dart';
import 'package:sleep_timer/ui/views/settings_view.dart';
import 'package:sleep_timer/ui/views/timer_detail_view.dart';

@MaterialAutoRouter(
  routes: <AutoRoute>[
    MaterialRoute(page: HomeView, initial: true),
    MaterialRoute(page: SettingsView),
    MaterialRoute(page: TimerDetailView),
    MaterialRoute(page: AlarmDetailView),
  ],
)
class $AutoRouter {}
