// GENERATED CODE - DO NOT MODIFY BY HAND

// **************************************************************************
// InjectableConfigGenerator
// **************************************************************************

import 'package:get_it/get_it.dart' as _i1;
import 'package:in_app_purchase/in_app_purchase.dart' as _i3;
import 'package:injectable/injectable.dart' as _i2;
import 'package:shared_preferences/shared_preferences.dart' as _i7;
import 'package:stacked_services/stacked_services.dart' as _i5;

import '../model/timer_model.dart' as _i10;
import '../services/device_service.dart' as _i11;
import '../services/light_service.dart' as _i4;
import '../services/purchase_service.dart' as _i12;
import '../services/review_service.dart' as _i6;
import '../services/theme_service.dart' as _i8;
import '../services/third_party_services_module.dart' as _i13;
import '../services/timer_service.dart' as _i9;

const String _prod = 'prod';
// ignore_for_file: unnecessary_lambdas
// ignore_for_file: lines_longer_than_80_chars
/// initializes the registration of provided dependencies inside of [GetIt]
Future<_i1.GetIt> $initGetIt(_i1.GetIt get,
    {String? environment, _i2.EnvironmentFilter? environmentFilter}) async {
  final gh = _i2.GetItHelper(get, environment, environmentFilter);
  final thirdPartyServicesModule = _$ThirdPartyServicesModule();
  gh.lazySingleton<_i3.InAppPurchaseConnection>(
      () => thirdPartyServicesModule.iapService);
  gh.lazySingleton<_i4.LightService>(() => _i4.LightService());
  gh.lazySingleton<_i5.NavigationService>(
      () => thirdPartyServicesModule.navigationService);
  gh.lazySingleton<_i6.ReviewService>(() => _i6.ReviewService());
  await gh.factoryAsync<_i7.SharedPreferences>(
      () => thirdPartyServicesModule.prefsService,
      preResolve: true);
  gh.lazySingleton<_i8.ThemeService>(() => _i8.ThemeService());
  gh.factoryParam<_i9.TimerService, _i10.TimerModel?, dynamic>(
      (timerModel, _) => _i9.TimerService(timerModel));
  gh.singletonAsync<_i11.DeviceService>(() => _i11.DeviceService.create());
  gh.singletonAsync<_i12.PurchaseService>(() => _i12.PurchaseService.create(),
      registerFor: {_prod});
  return get;
}

class _$ThirdPartyServicesModule extends _i13.ThirdPartyServicesModule {
  @override
  _i5.NavigationService get navigationService => _i5.NavigationService();
}
