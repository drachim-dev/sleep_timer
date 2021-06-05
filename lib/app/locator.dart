import 'package:get_it/get_it.dart';
import 'package:injectable/injectable.dart';

import 'locator.config.dart';

final locator = GetIt.instance;

@injectableInit
Future<void> configureInjection(String environment) async {
  await $initGetIt(locator, environment: environment);
}
