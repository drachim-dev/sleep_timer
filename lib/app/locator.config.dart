// GENERATED CODE - DO NOT MODIFY BY HAND

// **************************************************************************
// InjectableConfigGenerator
// **************************************************************************

import 'package:get_it/get_it.dart';
import 'package:injectable/injectable.dart';
import 'package:in_app_purchase/in_app_purchase.dart';
import 'package:stacked_services/stacked_services.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../services/device_service.dart';
import '../services/light_service.dart';
import '../services/purchase_service.dart';
import '../services/theme_service.dart';
import '../services/third_party_services_module.dart';
import '../model/timer_model.dart';
import '../services/timer_service.dart';

/// adds generated dependencies
/// to the provided [GetIt] instance

Future<GetIt> $initGetIt(
  GetIt get, {
  String environment,
  EnvironmentFilter environmentFilter,
}) async {
  final gh = GetItHelper(get, environment, environmentFilter);
  final thirdPartyServicesModule = _$ThirdPartyServicesModule();
  gh.lazySingleton<DeviceService>(() => DeviceService());
  gh.lazySingleton<InAppPurchaseConnection>(
      () => thirdPartyServicesModule.iapService);
  gh.lazySingleton<LightService>(() => LightService());
  gh.lazySingleton<NavigationService>(
      () => thirdPartyServicesModule.navigationService);
  final resolvedSharedPreferences = await thirdPartyServicesModule.prefsService;
  gh.factory<SharedPreferences>(() => resolvedSharedPreferences);
  gh.lazySingleton<ThemeService>(() => ThemeService());
  gh.factoryParam<TimerService, TimerModel, dynamic>(
      (timerModel, _) => TimerService(timerModel));

  // Eager singletons must be registered in the right order
  gh.singletonAsync<PurchaseService>(() => PurchaseService.create());
  return get;
}

class _$ThirdPartyServicesModule extends ThirdPartyServicesModule {
  @override
  NavigationService get navigationService => NavigationService();
}
