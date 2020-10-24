import 'dart:async';

import 'package:in_app_purchase/in_app_purchase.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:sleep_timer/app/locator.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/model/product.dart';
import 'package:sleep_timer/services/device_service.dart';
import 'package:sleep_timer/services/purchase_service.dart';
import 'package:sleep_timer/services/theme_service.dart';
import 'package:stacked/stacked.dart';

class SettingsViewModel extends ReactiveViewModel implements Initialisable {
  final _prefsService = locator<SharedPreferences>();
  final _themeService = locator<ThemeService>();
  final _deviceService = locator<DeviceService>();
  final _purchaseService = locator<PurchaseService>();

  bool get deviceAdmin => _deviceService.deviceAdmin ?? false;
  bool get notificationSettingsAccess =>
      _deviceService.notificationSettingsAccess ?? false;
  bool get experimentActive => deviceAdmin || notificationSettingsAccess;
  String get currentTheme => _prefsService.getString(kPrefKeyTheme) ?? 'Dark';

  StreamSubscription _streamSubscription;
  StreamSubscription get streamSubscription => _streamSubscription;
  Stream<List<PurchaseDetails>> get stream =>
      _purchaseService.purchaseUpdatedStream;

  List<Product> _products = [];
  List<Product> get products => _products;

  @override
  List<ReactiveServiceMixin> get reactiveServices =>
      [_themeService, _deviceService];

  @override
  Future initialise() async {
    setError(null);
    // We set busy manually as well because when notify listeners is called to clear error messages the
    // ui is rebuilt and if you expect busy to be true it's not.
    setBusy(true);
    notifyListeners();

    await _deviceService.init();
    _products = await runBusyFuture(_purchaseService.products);

    _streamSubscription = stream.listen((data) async {
      setBusy(true);
      _products = await runBusyFuture(_purchaseService.products);
      notifyListeners();
    });
  }

  @override
  void dispose() {
    _streamSubscription.cancel();
    super.dispose();
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

    notifyListeners();
  }

  Future<void> buyProduct(final Product product) async {
    await _purchaseService.buyProduct(product);
  }
}
