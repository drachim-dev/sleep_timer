import 'package:flutter/foundation.dart';
import 'package:in_app_purchase/in_app_purchase.dart';

class Product {
  final ProductDetails productDetails;
  final bool consumable;
  final bool purchased;

  Product(
      {@required this.productDetails,
      @required this.consumable,
      this.purchased = false});
}
