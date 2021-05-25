class App {
  String? title, icon, packageName;

  Map<dynamic, dynamic> toMap() {
    final pigeonMap = <dynamic, dynamic>{
      'title': title,
      'icon': icon,
      'packageName': packageName,
    };
    return pigeonMap;
  }

  static App fromMap(Map<dynamic, dynamic> pigeonMap) {
    final result = App()
      ..title = pigeonMap['title']
      ..icon = pigeonMap['icon']
      ..packageName = pigeonMap['packageName'];

    return result;
  }
}
