import 'package:in_app_purchase/in_app_purchase.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:sleep_timer/app/locator.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/services/device_service.dart';
import 'package:sleep_timer/services/theme_service.dart';
import 'package:stacked/stacked.dart';

class SettingsViewModel extends ReactiveViewModel {
  final _prefsService = locator<SharedPreferences>();
  final _themeService = locator<ThemeService>();
  final _deviceService = locator<DeviceService>();
  final _iap = InAppPurchaseConnection.instance;

  bool get deviceAdmin => _deviceService.deviceAdmin ?? false;
  bool get notificationSettingsAccess =>
      _deviceService.notificationSettingsAccess ?? false;
  String get currentTheme => _prefsService.getString(kPrefKeyTheme) ?? 'Dark';

  List<ProductDetails> _products = [];
  List<PurchaseDetails> _purchases = [];

  List<ProductDetails> get products => _products;
  List<PurchaseDetails> get purchases => _purchases;

  @override
  List<ReactiveServiceMixin> get reactiveServices =>
      [_themeService, _deviceService];

  void init() async {
    await _deviceService.init();
    notifyListeners();
  }

  void onChangeDeviceAdmin(bool value) async {
    await _deviceService.toggleDeviceAdmin(value);
    notifyListeners();
  }

  void onChangeNotificationSettingsAccess(bool value) async {
    await _deviceService.toggleNotificationSettingsAccess(value);
    notifyListeners();
  }

  void updateTheme(String theme) {
    _prefsService.setString(kPrefKeyTheme, theme);
    _themeService.updateTheme(theme);

    notifyListeners();
  }

  bool hasPurchased(ProductDetails product) {
    PurchaseDetails purchase = _purchases.singleWhere(
        (purchase) => purchase.productID == product.id,
        orElse: () => null);

    return purchase != null;
  }
}
