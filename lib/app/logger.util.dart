import 'package:logger/logger.dart';

Logger getLogger() {
  return Logger(
    printer: PrettyPrinter(
      lineLength: 120,
      colors: true,
      methodCount: 1,
      errorMethodCount: 5,
      printEmojis: false,
      dateTimeFormat: DateTimeFormat.onlyTimeAndSinceStart,
    ),
    output: null,
  );
}
