import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:preferences/preference_page.dart';
import 'package:preferences/preference_title.dart';
import 'package:preferences/preferences.dart';
import 'package:provider/provider.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/common/theme.dart';

class SettingsPage extends StatefulWidget {
  @override
  _SettingsPageState createState() => _SettingsPageState();
}

class _SettingsPageState extends State<SettingsPage> {
  Future<SharedPreferences> _prefsFuture;
  SharedPreferences _prefs;
  ThemeNotifier _themeNotifier;

  bool _deviceAdmin = false;
  String _selectedTheme = 'Dark';

  @override
  void initState() {
    super.initState();
    _loadPreferences();

    _themeNotifier = Provider.of<ThemeNotifier>(context, listen: false);
  }

  void _loadPreferences() async {
    _prefs = await SharedPreferences.getInstance();
    _selectedTheme = _prefs.getString(kPrefKeyTheme);

    _deviceAdmin = await kMethodChannel.invokeMethod("isDeviceAdminActive");
  }

  @override
  Widget build(BuildContext context) {
    final ThemeData theme = Theme.of(context);

    return Scaffold(
      appBar: _buildAppBar(theme),
      body: _buildBody(theme),
    );
  }

  AppBar _buildAppBar(final ThemeData theme) {
    return AppBar(title: Text("Settings"));
  }

  FutureBuilder _buildBody(final ThemeData theme) {
    return FutureBuilder<SharedPreferences>(
        future: _prefsFuture,
        builder: (context, snapshot) {
          return PreferencePage(
            [
              PreferenceTitle("Appereance"),
              ListTile(
                  title: Text('Choose theme'),
                  subtitle: Text(_selectedTheme),
                  leading: Icon(Icons.color_lens_outlined),
                  onTap: () => showThemeDialog()),
              PreferenceTitle("Experimental"),
              SwitchListTile(
                  secondary: Icon(Icons.admin_panel_settings_outlined),
                  title: Text("Device admin"),
                  subtitle: Text(
                      "Required for advanced actions such as switching off the display"),
                  isThreeLine: true,
                  value: _deviceAdmin,
                  onChanged: (value) async {
                    try {
                      bool success = await kMethodChannel.invokeMethod(
                          "toggleDeviceAdmin", {"enabled": value});
                      if (success) setState(() => _deviceAdmin = value);
                    } on PlatformException catch (e) {
                      print(e.message);
                    }
                  }),
              PreferenceTitle("Other"),
              ListTile(
                title: Text("FAQ"),
              ),
              ListTile(
                title: Text("Credits"),
              ),
            ],
          );
        });
  }

  Future<void> showThemeDialog() {
    return showDialog(
      context: context,
      builder: (_) {
        return AlertDialog(
          title: Text('Choose Theme'),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              RadioListTile(
                title: Text('Light'),
                groupValue: _selectedTheme,
                value: 'Light',
                onChanged: setTheme,
              ),
              RadioListTile(
                title: Text('Dark'),
                groupValue: _selectedTheme,
                value: 'Dark',
                onChanged: setTheme,
              ),
              RadioListTile(
                title: Text('Black'),
                groupValue: _selectedTheme,
                value: 'Black',
                onChanged: setTheme,
              ),
            ],
          ),
        );
      },
    );
  }

  void setTheme(String value) {
    _themeNotifier.setTheme(MyTheme.getThemeFromName(value));

    _prefs.setString(kPrefKeyTheme, value);
    setState(() => _selectedTheme = value);
  }
}
