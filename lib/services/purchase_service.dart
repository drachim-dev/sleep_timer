import 'package:in_app_purchase/in_app_purchase.dart';
import 'package:injectable/injectable.dart';
import 'package:observable_ish/list/list.dart';
import 'package:stacked/stacked.dart';

@lazySingleton
class PurchaseService with ReactiveServiceMixin {
  PurchaseService() {
    listenToReactiveValues([_products, _purchases]);
  }

  final RxList<ProductDetails> _products = RxList<ProductDetails>();
  List<ProductDetails> get products => _products;

  final RxList<PurchaseDetails> _purchases = RxList<PurchaseDetails>();
  List<PurchaseDetails> get purchases => _purchases;
}
