// GENERATED CODE - DO NOT MODIFY BY HAND

// **************************************************************************
// InjectableConfigGenerator
// **************************************************************************

// ignore_for_file: type=lint
// coverage:ignore-file

// ignore_for_file: no_leading_underscores_for_library_prefixes
import 'package:get_it/get_it.dart' as _i1;
import 'package:injectable/injectable.dart' as _i2;
import 'package:shared_preferences/shared_preferences.dart' as _i8;
import 'package:sleep_timer/model/timer_model.dart' as _i11;
import 'package:sleep_timer/services/ad_service.dart' as _i12;
import 'package:sleep_timer/services/device_service.dart' as _i3;
import 'package:sleep_timer/services/light_service.dart' as _i4;
import 'package:sleep_timer/services/purchase_service.dart' as _i6;
import 'package:sleep_timer/services/review_service.dart' as _i7;
import 'package:sleep_timer/services/theme_service.dart' as _i9;
import 'package:sleep_timer/services/third_party_services_module.dart' as _i13;
import 'package:sleep_timer/services/timer_service.dart' as _i10;
import 'package:stacked_services/stacked_services.dart' as _i5;

const String _prod = 'prod';

extension GetItInjectableX on _i1.GetIt {
// initializes the registration of main-scope dependencies inside of GetIt
  Future<_i1.GetIt> init({
    String? environment,
    _i2.EnvironmentFilter? environmentFilter,
  }) async {
    final gh = _i2.GetItHelper(
      this,
      environment,
      environmentFilter,
    );
    final thirdPartyServicesModule = _$ThirdPartyServicesModule();
    gh.singletonAsync<_i3.DeviceService>(
      () => _i3.DeviceService.create(),
      registerFor: {_prod},
    );
    gh.lazySingleton<_i4.LightService>(() => _i4.LightService());
    gh.lazySingleton<_i5.NavigationService>(
        () => thirdPartyServicesModule.navigationService);
    gh.lazySingletonAsync<_i6.PurchaseService>(
      () => _i6.PurchaseService.create(),
      registerFor: {_prod},
    );
    gh.lazySingleton<_i7.ReviewService>(() => _i7.ReviewService());
    await gh.factoryAsync<_i8.SharedPreferences>(
      () => thirdPartyServicesModule.prefsService,
      preResolve: true,
    );
    gh.lazySingleton<_i5.SnackbarService>(
        () => thirdPartyServicesModule.snackBarService);
    gh.singleton<_i9.ThemeService>(_i9.ThemeService());
    gh.factoryParam<_i10.TimerService, _i11.TimerModel?, dynamic>((
      timerModel,
      _,
    ) =>
        _i10.TimerService(timerModel));
    gh.lazySingletonAsync<_i12.AdService>(
        () => _i12.AdService.create(gh<_i8.SharedPreferences>()));
    return this;
  }
}

class _$ThirdPartyServicesModule extends _i13.ThirdPartyServicesModule {
  @override
  _i5.NavigationService get navigationService => _i5.NavigationService();

  @override
  _i5.SnackbarService get snackBarService => _i5.SnackbarService();
}
