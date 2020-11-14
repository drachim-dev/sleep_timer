class App {
  String title;
  String icon;
  String packageName;

  Map<dynamic, dynamic> toMap() {
    final Map<dynamic, dynamic> pigeonMap = <dynamic, dynamic>{};
    pigeonMap['title'] = title;
    pigeonMap['icon'] = icon;
    pigeonMap['packageName'] = packageName;
    return pigeonMap;
  }

  static App fromMap(Map<dynamic, dynamic> pigeonMap) {
    final App result = App();
    result.title = pigeonMap['title'];
    result.icon = pigeonMap['icon'];
    result.packageName = pigeonMap['packageName'];
    return result;
  }
}