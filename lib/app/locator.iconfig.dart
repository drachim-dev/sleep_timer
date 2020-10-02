// GENERATED CODE - DO NOT MODIFY BY HAND

// **************************************************************************
// InjectableConfigGenerator
// **************************************************************************

import 'package:sleep_timer/services/device_service.dart';
import 'package:sleep_timer/services/third_party_services_module.dart';
import 'package:stacked_services/stacked_services.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:sleep_timer/services/theme_service.dart';
import 'package:sleep_timer/services/timer_service.dart';
import 'package:sleep_timer/model/timer_model.dart';
import 'package:get_it/get_it.dart';

Future<void> $initGetIt(GetIt g, {String environment}) async {
  final thirdPartyServicesModule = _$ThirdPartyServicesModule();
  g.registerLazySingleton<DeviceService>(() => DeviceService());
  g.registerLazySingleton<DialogService>(
      () => thirdPartyServicesModule.dialogService);
  g.registerLazySingleton<NavigationService>(
      () => thirdPartyServicesModule.navigationService);
  final sharedPreferences = await thirdPartyServicesModule.prefsService;
  g.registerFactory<SharedPreferences>(() => sharedPreferences);
  g.registerLazySingleton<ThemeService>(() => ThemeService());
  g.registerFactoryParam<TimerService, TimerModel, dynamic>(
      (timerModel, _) => TimerService(timerModel));
}

class _$ThirdPartyServicesModule extends ThirdPartyServicesModule {
  @override
  DialogService get dialogService => DialogService();
  @override
  NavigationService get navigationService => NavigationService();
}
