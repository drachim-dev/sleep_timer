import 'package:injectable/injectable.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:sleep_timer/services/purchase_service.dart';
import 'package:stacked_services/stacked_services.dart';

@module
abstract class ThirdPartyServicesModule {
  @lazySingleton
  NavigationService get navigationService;
  @preResolve
  Future<SharedPreferences> get prefsService => SharedPreferences.getInstance();
  @prod
  @preResolve
  Future<PurchaseService> get purchaseService => PurchaseService.create();
}
