import 'package:in_app_purchase/in_app_purchase.dart';
import 'package:injectable/injectable.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:stacked_services/stacked_services.dart';

@module
abstract class ThirdPartyServicesModule {
  @lazySingleton
  NavigationService get navigationService;
  @lazySingleton
  DialogService get dialogService;
  @preResolve
  Future<SharedPreferences> get prefsService => SharedPreferences.getInstance();
  @lazySingleton
  InAppPurchaseConnection get iapService => InAppPurchaseConnection.instance;
}
