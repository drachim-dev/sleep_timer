// GENERATED CODE - DO NOT MODIFY BY HAND

// **************************************************************************
// InjectableConfigGenerator
// **************************************************************************

import 'package:get_it/get_it.dart' as _i1;
import 'package:injectable/injectable.dart' as _i2;
import 'package:shared_preferences/shared_preferences.dart' as _i7;
import 'package:stacked_services/stacked_services.dart' as _i4;

import '../model/timer_model.dart' as _i9;
import '../services/ad_service.dart' as _i10;
import '../services/device_service.dart' as _i11;
import '../services/light_service.dart' as _i3;
import '../services/purchase_service.dart' as _i5;
import '../services/review_service.dart' as _i6;
import '../services/theme_service.dart' as _i12;
import '../services/third_party_services_module.dart' as _i13;
import '../services/timer_service.dart' as _i8;

const String _prod = 'prod';
// ignore_for_file: unnecessary_lambdas
// ignore_for_file: lines_longer_than_80_chars
/// initializes the registration of provided dependencies inside of [GetIt]
Future<_i1.GetIt> $initGetIt(_i1.GetIt get,
    {String? environment, _i2.EnvironmentFilter? environmentFilter}) async {
  final gh = _i2.GetItHelper(get, environment, environmentFilter);
  final thirdPartyServicesModule = _$ThirdPartyServicesModule();
  gh.lazySingleton<_i3.LightService>(() => _i3.LightService());
  gh.lazySingleton<_i4.NavigationService>(
      () => thirdPartyServicesModule.navigationService);
  await gh.factoryAsync<_i5.PurchaseService>(
      () => thirdPartyServicesModule.purchaseService,
      registerFor: {_prod},
      preResolve: true);
  gh.lazySingleton<_i6.ReviewService>(() => _i6.ReviewService());
  await gh.factoryAsync<_i7.SharedPreferences>(
      () => thirdPartyServicesModule.prefsService,
      preResolve: true);
  gh.factoryParam<_i8.TimerService, _i9.TimerModel?, dynamic>(
      (timerModel, _) => _i8.TimerService(timerModel));
  gh.singletonAsync<_i10.AdService>(() => _i10.AdService.create());
  gh.singletonAsync<_i11.DeviceService>(() => _i11.DeviceService.create(),
      registerFor: {_prod});
  gh.singleton<_i12.ThemeService>(_i12.ThemeService());
  return get;
}

class _$ThirdPartyServicesModule extends _i13.ThirdPartyServicesModule {
  @override
  _i4.NavigationService get navigationService => _i4.NavigationService();
}
