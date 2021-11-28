// GENERATED CODE - DO NOT MODIFY BY HAND

// **************************************************************************
// InjectableConfigGenerator
// **************************************************************************

import 'package:get_it/get_it.dart' as _i1;
import 'package:injectable/injectable.dart' as _i2;
import 'package:shared_preferences/shared_preferences.dart' as _i8;
import 'package:stacked_services/stacked_services.dart' as _i5;

import '../model/timer_model.dart' as _i11;
import '../services/ad_service.dart' as _i12;
import '../services/device_service.dart' as _i3;
import '../services/light_service.dart' as _i4;
import '../services/purchase_service.dart' as _i6;
import '../services/review_service.dart' as _i7;
import '../services/theme_service.dart' as _i9;
import '../services/third_party_services_module.dart' as _i13;
import '../services/timer_service.dart' as _i10;

const String _prod = 'prod';
// ignore_for_file: unnecessary_lambdas
// ignore_for_file: lines_longer_than_80_chars
/// initializes the registration of provided dependencies inside of [GetIt]
Future<_i1.GetIt> $initGetIt(_i1.GetIt get,
    {String? environment, _i2.EnvironmentFilter? environmentFilter}) async {
  final gh = _i2.GetItHelper(get, environment, environmentFilter);
  final thirdPartyServicesModule = _$ThirdPartyServicesModule();
  gh.singletonAsync<_i3.DeviceService>(() => _i3.DeviceService.create(),
      registerFor: {_prod});
  gh.lazySingleton<_i4.LightService>(() => _i4.LightService());
  gh.lazySingleton<_i5.NavigationService>(
      () => thirdPartyServicesModule.navigationService);
  await gh.factoryAsync<_i6.PurchaseService>(
      () => thirdPartyServicesModule.purchaseService,
      registerFor: {_prod},
      preResolve: true);
  gh.lazySingleton<_i7.ReviewService>(() => _i7.ReviewService());
  await gh.factoryAsync<_i8.SharedPreferences>(
      () => thirdPartyServicesModule.prefsService,
      preResolve: true);
  gh.singleton<_i9.ThemeService>(_i9.ThemeService());
  gh.factoryParam<_i10.TimerService, _i11.TimerModel?, dynamic>(
      (timerModel, _) => _i10.TimerService(timerModel));
  gh.singleton<_i12.AdService>(
      _i12.AdService.create(get<_i8.SharedPreferences>()));
  return get;
}

class _$ThirdPartyServicesModule extends _i13.ThirdPartyServicesModule {
  @override
  _i5.NavigationService get navigationService => _i5.NavigationService();
}
