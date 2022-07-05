import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:in_app_purchase/in_app_purchase.dart';
import 'package:injectable/injectable.dart';
import 'package:logger/logger.dart';
import 'package:sleep_timer/app/logger.util.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/model/product.dart';
import 'package:stacked/stacked.dart';
import 'package:in_app_purchase_android/in_app_purchase_android.dart';

@prod
@lazySingleton
class PurchaseService with ReactiveServiceMixin {
  final Logger log = getLogger();
  final InAppPurchase _inAppPurchase;

  PurchaseService(this._inAppPurchase) {
    _listenToPurchaseUpdated();
  }

  @factoryMethod
  static Future<PurchaseService> create() async {
    final log = getLogger();

    final iap = InAppPurchase.instance;
    var instance = PurchaseService(iap);

    var isAvailable = await instance._inAppPurchase.isAvailable();
    if (!isAvailable) {
      log.e(isAvailable.toString());
      instance._products = [];
      return instance;
    }

    await instance._restorePurchases();
    await instance.updateProducts();

    return instance;
  }

  void _listenToPurchaseUpdated() {
    _inAppPurchase.purchaseStream
        .listen((List<PurchaseDetails> purchaseDetails) {
      _purchases = purchaseDetails;

      // only for internal testing
      // if (kDebugMode) _resetPurchases(_purchases);

      for (var detail in purchaseDetails) {
        log.d(detail.status.toString());
        if (detail.pendingCompletePurchase) {
          log.d('pendingCompletePurchase');
          _inAppPurchase.completePurchase(detail);
        }
      }

      log.d('Number of products: ${_products.length}');
    });
  }

  Stream<List<PurchaseDetails>> get purchaseStream =>
      _inAppPurchase.purchaseStream;

  List<Product> _products = [];
  List<Product> get products => _products;

  List<PurchaseDetails> _purchases = <PurchaseDetails>[];

  bool get adFree => products.any((element) =>
      element.productDetails.id == kProductRemoveAds && element.purchased);

  Future<void> updateProducts() async {
    final response = await _inAppPurchase.queryProductDetails(kProducts);

    for (var element in response.notFoundIDs) {
      log.d(element);
    }

    if (response.error != null) {
      log.e('error code: ${response.error!.code}');
      log.e('error message: ${response.error!.message}');
      log.e(response.error!.details);
    }

    _products = response.productDetails.map((product) {
      bool consumable;
      switch (product.id) {
        case kProductDonation:
          consumable = true;
          break;
        default:
          consumable = false;
      }

      final hasPurchased = _purchases.any((purchase) =>
          purchase.productID == product.id &&
          [
            PurchaseStatus.purchased,
            PurchaseStatus.restored,
            PurchaseStatus.pending
          ].contains(purchase.status));

      log.d('Found product: ${product.id}, purchased: $hasPurchased');

      return Product(
          productDetails: product,
          consumable: consumable,
          purchased: hasPurchased);
    }).toList();

    log.d('Number of products: ${_products.length}');
  }

  Future<void> _restorePurchases() async {
    log.d('Get purchases');
    await _inAppPurchase.restorePurchases();
  }

  Future<bool> buyProduct(final Product product) async {
    final purchaseParam = PurchaseParam(productDetails: product.productDetails);

    final success = product.consumable
        ? await _inAppPurchase.buyConsumable(purchaseParam: purchaseParam)
        : await _inAppPurchase.buyNonConsumable(purchaseParam: purchaseParam);

    return success;
  }

  @Deprecated('Only for internal testing. Must not be used in production code.')
  // ignore: unused_element
  Future<void> _resetPurchases(List<PurchaseDetails> purchases) async {
    final androidAddition = InAppPurchase.instance
        .getPlatformAddition<InAppPurchaseAndroidPlatformAddition>();

    log.d('Reset purchases');
    for (var purchase in purchases) {
      await androidAddition.consumePurchase(purchase);
    }
  }
}
