import 'dart:async';

import 'package:in_app_purchase/in_app_purchase.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:sleep_timer/app/auto_router.gr.dart';
import 'package:sleep_timer/app/locator.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/common/theme.dart';
import 'package:sleep_timer/model/product.dart';
import 'package:sleep_timer/services/device_service.dart';
import 'package:sleep_timer/services/purchase_service.dart';
import 'package:sleep_timer/services/theme_service.dart';
import 'package:stacked/stacked.dart';
import 'package:stacked_services/stacked_services.dart';

class SettingsViewModel extends ReactiveViewModel implements Initialisable {
  final _navigationService = locator<NavigationService>();
  final _prefsService = locator<SharedPreferences>();
  final _themeService = locator<ThemeService>();
  final _deviceService = locator<DeviceService>();
  final _purchaseService = locator<PurchaseService>();

  MyTheme get currentTheme => _themeService.myTheme;
  bool get glow => _themeService.showGlow;

  int get extendTimeByShake =>
      _prefsService.getInt(kPrefKeyDefaultExtendTimeByShake) ??
      kDefaultExtendTimeByShake;

  bool get deviceAdmin => _deviceService.deviceAdmin ?? false;
  bool get notificationSettingsAccess =>
      _deviceService.notificationSettingsAccess ?? false;

  Stream<List<PurchaseDetails>> get stream =>
      _purchaseService.purchaseUpdatedStream;

  List<Product> get products => _purchaseService.products;

  @override
  List<ReactiveServiceMixin> get reactiveServices =>
      [_themeService, _deviceService];

  @override
  Future initialise() async {
    await _deviceService.init();

    stream.listen((data) async {
      setBusy(true);
      await runBusyFuture(_purchaseService.updateProducts());
      notifyListeners();
    });
  }

  void onChangeExtendTimeByShake(final int value) async {
    await _prefsService.setInt(kPrefKeyDefaultExtendTimeByShake, value);
    notifyListeners();
  }

  void onChangeDeviceAdmin(final bool value) async {
    await _deviceService.toggleDeviceAdmin(value);
    notifyListeners();
  }

  void onChangeNotificationSettingsAccess(final bool value) async {
    await _deviceService.toggleNotificationSettingsAccess(value);
    notifyListeners();
  }

  void updateTheme(final String theme) {
    _prefsService.setString(kPrefKeyTheme, theme);
    _themeService.updateTheme(theme);
  }

  void onChangeGlow(bool value) {
    _prefsService.setBool(kPrefKeyGlow, value);
    _themeService.showGlow = value;
  }

  Future<void> buyProduct(final Product product) async {
    await _purchaseService.buyProduct(product);
  }

  Future<void> navigateToCredits() async =>
      _navigationService.navigateTo(Routes.creditsView);

  Future<void> navigateToFAQ() async =>
      _navigationService.navigateTo(Routes.fAQView);
}
