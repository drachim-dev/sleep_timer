// GENERATED CODE - DO NOT MODIFY BY HAND

// **************************************************************************
// InjectableConfigGenerator
// **************************************************************************

// ignore_for_file: type=lint
// coverage:ignore-file

// ignore_for_file: no_leading_underscores_for_library_prefixes
import 'package:get_it/get_it.dart' as _i174;
import 'package:injectable/injectable.dart' as _i526;
import 'package:shared_preferences/shared_preferences.dart' as _i460;
import 'package:sleep_timer/model/timer_model.dart' as _i190;
import 'package:sleep_timer/services/ad_service.dart' as _i861;
import 'package:sleep_timer/services/device_service.dart' as _i270;
import 'package:sleep_timer/services/light_service.dart' as _i338;
import 'package:sleep_timer/services/purchase_service.dart' as _i997;
import 'package:sleep_timer/services/review_service.dart' as _i445;
import 'package:sleep_timer/services/theme_service.dart' as _i548;
import 'package:sleep_timer/services/third_party_services_module.dart' as _i719;
import 'package:sleep_timer/services/timer_service.dart' as _i477;
import 'package:stacked_services/stacked_services.dart' as _i1055;

const String _prod = 'prod';

extension GetItInjectableX on _i174.GetIt {
// initializes the registration of main-scope dependencies inside of GetIt
  Future<_i174.GetIt> init({
    String? environment,
    _i526.EnvironmentFilter? environmentFilter,
  }) async {
    final gh = _i526.GetItHelper(
      this,
      environment,
      environmentFilter,
    );
    final thirdPartyServicesModule = _$ThirdPartyServicesModule();
    await gh.factoryAsync<_i460.SharedPreferences>(
      () => thirdPartyServicesModule.prefsService,
      preResolve: true,
    );
    gh.singleton<_i548.ThemeService>(() => _i548.ThemeService());
    gh.lazySingleton<_i338.LightService>(() => _i338.LightService());
    gh.lazySingleton<_i1055.NavigationService>(
        () => thirdPartyServicesModule.navigationService);
    gh.lazySingleton<_i1055.SnackbarService>(
        () => thirdPartyServicesModule.snackBarService);
    gh.lazySingleton<_i445.ReviewService>(() => _i445.ReviewService());
    gh.lazySingletonAsync<_i861.AdService>(
        () => _i861.AdService.create(gh<_i460.SharedPreferences>()));
    gh.singletonAsync<_i270.DeviceService>(
      () => _i270.DeviceService.create(),
      registerFor: {_prod},
    );
    gh.lazySingletonAsync<_i997.PurchaseService>(
      () => _i997.PurchaseService.create(),
      registerFor: {_prod},
    );
    gh.factoryParam<_i477.TimerService, _i190.TimerModel, dynamic>((
      timerModel,
      _,
    ) =>
        _i477.TimerService(timerModel));
    return this;
  }
}

class _$ThirdPartyServicesModule extends _i719.ThirdPartyServicesModule {
  @override
  _i1055.NavigationService get navigationService => _i1055.NavigationService();

  @override
  _i1055.SnackbarService get snackBarService => _i1055.SnackbarService();
}
