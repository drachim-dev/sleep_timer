import 'package:get_it/get_it.dart';
import 'package:injectable/injectable.dart';

import 'app.locator.config.dart';

final locator = GetIt.instance;

@injectableInit
Future<void> setupLocator(String environment) async {
  await locator.init(environment: environment);
}
