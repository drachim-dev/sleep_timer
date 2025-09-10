import 'package:sleep_timer/services/ad_service.dart';
import 'package:sleep_timer/services/light_service.dart';
import 'package:sleep_timer/services/purchase_service.dart';
import 'package:sleep_timer/services/review_service.dart';
import 'package:sleep_timer/ui/views/bridge_link_view.dart';
import 'package:sleep_timer/ui/views/credits_view.dart';
import 'package:sleep_timer/ui/views/faq_view.dart';
import 'package:sleep_timer/ui/views/home_view.dart';
import 'package:sleep_timer/ui/views/intro_view.dart';
import 'package:sleep_timer/ui/views/light_group_view.dart';
import 'package:sleep_timer/ui/views/settings_view.dart';
import 'package:sleep_timer/ui/views/timer_view.dart';
import 'package:stacked/stacked_annotations.dart';
import 'package:stacked_services/stacked_services.dart';

@StackedApp(
  locatorName: 'fakeLocator',
  locatorSetupName: 'setupFakeLocator',
  routes: [
    MaterialRoute(page: IntroView),
    MaterialRoute(page: HomeView),
    MaterialRoute(page: TimerView),
    MaterialRoute(page: SettingsView),
    MaterialRoute(page: FAQView),
    MaterialRoute(page: CreditsView),
    MaterialRoute(page: BridgeLinkView),
    MaterialRoute(page: LightGroupView),
  ],
  dependencies: [
    LazySingleton(
      classType: PurchaseService,
      resolveUsing: PurchaseService.create,
    ),
    LazySingleton(classType: AdService, resolveUsing: AdService.create),
    LazySingleton(classType: LightService),
    LazySingleton(classType: ReviewService),
    LazySingleton(classType: NavigationService),
    LazySingleton(classType: SnackbarService),
  ],
)
class AppSetup {}
