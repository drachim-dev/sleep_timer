import 'package:in_app_purchase/in_app_purchase.dart';
import 'package:injectable/injectable.dart';
import 'package:logger/logger.dart';
import 'package:sleep_timer/app/logger.util.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/model/product.dart';
import 'package:stacked/stacked.dart';

class PurchaseService with ReactiveServiceMixin {
  final Logger log = getLogger();
  final InAppPurchaseConnection _iap;

  PurchaseService(this._iap) {
    listenToPurchases();
  }

  @factoryMethod
  static Future<PurchaseService> create() async {
    final log = getLogger();

    final iap = InAppPurchaseConnection.instance;
    var instance = PurchaseService(iap);

    var isAvailable = await instance._iap.isAvailable();
    if (!isAvailable) {
      log.e(isAvailable.toString());
      instance._products = [];
      return instance;
    }

    await instance.updateProducts();
    return instance;
  }

  void listenToPurchases() {
    _iap.purchaseUpdatedStream.listen((details) {
      for (var detail in details) {
        if (detail.status == PurchaseStatus.pending) {
          log.d('pending');
        } else {
          if (detail.status == PurchaseStatus.error) {
            log.d('error');
          } else if (detail.status == PurchaseStatus.purchased) {
            log.d('purchased');
          }
          if (detail.pendingCompletePurchase) {
            log.d('complete');
            _iap.completePurchase(detail);
          }
        }
      }
    });
  }

  Stream<List<PurchaseDetails>> get purchaseUpdatedStream =>
      _iap.purchaseUpdatedStream;

  List<Product> _products = [];
  List<Product> get products => _products;

  bool get adFree => products.any((element) =>
      element.productDetails.id == kProductRemoveAds && element.purchased);

  Future<void> updateProducts() async {
    _products = await _getProducts();
    log.d('Number of products: ${_products.length}');
  }

  Future<List<Product>> _getProducts() async {
    var purchases = await _getPurchases();
    // if (kDebugMode) resetPurchases(purchases);

    final response = await _iap.queryProductDetails(kProducts);

    for (var element in response.notFoundIDs) {
      log.d(element);
    }

    if (response.error != null) {
      log.e('error code: ${response.error!.code}');
      log.e('error message: ${response.error!.message}');
      log.e(response.error!.details);
    }

    return response.productDetails.map((e) {
      bool consumable;
      switch (e.id) {
        case kProductDonation:
          consumable = true;
          break;
        default:
          consumable = false;
      }

      final hasPurchased =
          purchases.any((element) => element.productID == e.id);

      log.d('Found product: ${e.id}, purchased: $hasPurchased');

      return Product(
          productDetails: e, consumable: consumable, purchased: hasPurchased);
    }).toList();
  }

  Future<List<PurchaseDetails>> _getPurchases() async {
    log.d('Get purchases');
    final response = await _iap.queryPastPurchases();

    return response.pastPurchases;
  }

  Future<bool> buyProduct(final Product product) async {
    final purchaseParam = PurchaseParam(productDetails: product.productDetails);

    final success = product.consumable
        ? await _iap.buyConsumable(purchaseParam: purchaseParam)
        : await _iap.buyNonConsumable(purchaseParam: purchaseParam);

    return success;
  }

  // only for internal testing
  void resetPurchases(List<PurchaseDetails> purchases) {
    log.d('Reset purchases');
    for (var element in purchases) {
      log.d(element.productID);
      _iap.consumePurchase(
          PurchaseDetails.fromPurchase(element.billingClientPurchase!));
    }
  }
}
