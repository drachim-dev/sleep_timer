// GENERATED CODE - DO NOT MODIFY BY HAND

// **************************************************************************
// InjectableConfigGenerator
// **************************************************************************

import 'package:get_it/get_it.dart';
import 'package:injectable/injectable.dart';
import 'package:stacked_services/stacked_services.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../services/device_service.dart';
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
  gh.lazySingleton<DialogService>(() => thirdPartyServicesModule.dialogService);
  gh.lazySingleton<NavigationService>(
      () => thirdPartyServicesModule.navigationService);
  gh.lazySingleton<PurchaseService>(() => PurchaseService());
  final sharedPreferences = await thirdPartyServicesModule.prefsService;
  gh.factory<SharedPreferences>(() => sharedPreferences);
  gh.lazySingleton<ThemeService>(() => ThemeService());
  gh.factoryParam<TimerService, TimerModel, dynamic>(
      (timerModel, _) => TimerService(timerModel));
  return get;
}

class _$ThirdPartyServicesModule extends ThirdPartyServicesModule {
  @override
  DialogService get dialogService => DialogService();
  @override
  NavigationService get navigationService => NavigationService();
}