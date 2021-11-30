import 'dart:async';

import 'package:get_it/get_it.dart';
import 'package:in_app_purchase/in_app_purchase.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:sleep_timer/app/app.router.dart';
import 'package:sleep_timer/app/locator.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/common/theme.dart';
import 'package:sleep_timer/model/product.dart';
import 'package:sleep_timer/services/device_service.dart';
import 'package:sleep_timer/services/purchase_service.dart';
import 'package:sleep_timer/services/review_service.dart';
import 'package:sleep_timer/services/theme_service.dart';
import 'package:stacked/stacked.dart';
import 'package:stacked_services/stacked_services.dart';

class SettingsViewModel extends ReactiveViewModel implements Initialisable {
  final DeviceService _deviceService = locator<DeviceService>();
  final NavigationService _navigationService = locator<NavigationService>();
  final SharedPreferences _prefsService = locator<SharedPreferences>();
  final PurchaseService _purchaseService = locator.get<PurchaseService>();
  final ReviewService _reviewService = locator<ReviewService>();
  final ThemeService _themeService = locator<ThemeService>();

  MyTheme get currentTheme => _themeService.myTheme;
  bool get glow => _themeService.showGlow;

  bool get extendByShake =>
      _prefsService.getBool(kPrefKeyExtendByShake) ?? kDefaultExtendByShake;

  int get extendTimeByShake =>
      _prefsService.getInt(kPrefKeyDefaultExtendTimeByShake) ??
      kDefaultExtendTimeByShake;

  bool get hasAccelerometer => _deviceService.hasAccelerometer;
  bool get deviceAdmin => _deviceService.deviceAdmin;
  bool get notificationSettingsAccess =>
      _deviceService.notificationSettingsAccess;

  Stream<List<PurchaseDetails>> get stream =>
      _purchaseService.purchaseUpdatedStream;

  List<Product> get products => _purchaseService.products;

  @override
  List<ReactiveServiceMixin> get reactiveServices =>
      [_themeService, _deviceService];

  @override
  Future initialise() async {
    await GetIt.I.isReady<DeviceService>();
    await GetIt.I.isReady<PurchaseService>();

    stream.listen((data) async {
      setBusy(true);
      await runBusyFuture(_purchaseService.updateProducts());
      notifyListeners();
    });
  }

  void setTheme(final String? theme) {
    _prefsService.setString(kPrefKeyTheme, theme!);
    _themeService.setTheme(theme);
  }

  void onChangeGlow(final bool value) {
    _prefsService.setBool(kPrefKeyGlow, value);
    _themeService.setShowGlow(value);
  }

  void onChangeExtendByShake(final bool value) async {
    await _prefsService.setBool(kPrefKeyExtendByShake, value);
    _deviceService.toggleExtendByShake(value);
    notifyListeners();
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

  Future<void> buyProduct(final Product product) async {
    await _purchaseService.buyProduct(product);
  }

  Future<void>? navigateToCredits() =>
      _navigationService.navigateTo(Routes.creditsView);

  Future<void>? navigateToFAQ() =>
      _navigationService.navigateTo(Routes.fAQView);

  Future<void> openStoreListing() => _reviewService.openStoreListing();
}
