import 'package:logger/logger.dart';

Logger getLogger() {
  return Logger(
    // filter: MyLogFilter(),
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

class MyLogFilter extends LogFilter {
  @override
  bool shouldLog(LogEvent event) {
    return true;
  }
}
