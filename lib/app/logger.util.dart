import 'package:logger/logger.dart';

Logger getLogger() {
  return Logger(
    printer: PrettyPrinter(
      lineLength: 120,
      colors: true,
      methodCount: 1,
      errorMethodCount: 5,
      printEmojis: false,
      printTime: true,
    ),
    output: null,
  );
}