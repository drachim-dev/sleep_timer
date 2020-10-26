import 'package:in_app_purchase/in_app_purchase.dart';
import 'package:injectable/injectable.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/model/product.dart';
import 'package:stacked/stacked.dart';

@singleton
class PurchaseService with ReactiveServiceMixin {
  final _iap = InAppPurchaseConnection.instance;

  PurchaseService() {
    _iap.purchaseUpdatedStream.listen((details) {
      details.forEach((detail) {
        if (detail.status == PurchaseStatus.pending) {
          print('pending');
        } else {
          if (detail.status == PurchaseStatus.error) {
            print('error');
          } else if (detail.status == PurchaseStatus.purchased) {
            print('purchased');
          }
          if (detail.pendingCompletePurchase) {
            print('complete');
            _iap.completePurchase(detail);
          }
        }
      });
    });
  }

  @factoryMethod
  static Future<PurchaseService> create() async {
    var instance = PurchaseService();
    instance._products = await instance._getProducts();
    return instance;
  }

  Stream<List<PurchaseDetails>> get purchaseUpdatedStream =>
      _iap.purchaseUpdatedStream;

  List<Product> _products = [];
  List<Product> get products => _products;

  Future<void> updateProducts() async {
    _products = await _getProducts();
  }

  Future<List<Product>> _getProducts() async {
    List<PurchaseDetails> purchases = await _getPurchases();
    //resetPurchases(purchases);

    final ProductDetailsResponse response =
        await _iap.queryProductDetails(kProducts);

    return response.productDetails.map((e) {
      bool consumable;
      switch (e.id) {
        case kProductDonation:
          consumable = true;
          break;
        default:
          consumable = false;
      }

      final bool hasPurchased = purchases.firstWhere(
              (element) => element.productID == e.id,
              orElse: () => null) !=
          null;

      return Product(
          productDetails: e, consumable: consumable, purchased: hasPurchased);
    }).toList();
  }

  Future<List<PurchaseDetails>> _getPurchases() async {
    final QueryPurchaseDetailsResponse response =
        await _iap.queryPastPurchases();

    return response.pastPurchases;
  }

  Future<bool> buyProduct(final Product product) async {
    final PurchaseParam purchaseParam =
        PurchaseParam(productDetails: product.productDetails);

    final bool success = product.consumable
        ? await _iap.buyConsumable(purchaseParam: purchaseParam)
        : await _iap.buyNonConsumable(purchaseParam: purchaseParam);

    return success;
  }

  // TODO: only for internal testing
  void resetPurchases(List<PurchaseDetails> purchases) {
    purchases.forEach((element) {
      print(element.productID);
      _iap.consumePurchase(
          PurchaseDetails.fromPurchase(element.billingClientPurchase));
    });
  }
}
