import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:preferences/preference_page.dart';
import 'package:preferences/preference_title.dart';
import 'package:preferences/preferences.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:stacked/stacked.dart';
import 'settings_viewmodel.dart';

class SettingsView extends StatefulWidget {
  @override
  _SettingsViewState createState() => _SettingsViewState();
}

class _SettingsViewState extends State<SettingsView> {
  bool _deviceAdmin = false;

  SettingsViewModel model;

  @override
  void initState() {
    super.initState();
    _loadPreferences();
  }

  void _loadPreferences() async {
    _deviceAdmin = await kMethodChannel.invokeMethod("isDeviceAdminActive");
  }

  @override
  Widget build(BuildContext context) {
    final ThemeData theme = Theme.of(context);

    return ViewModelBuilder<SettingsViewModel>.reactive(
        viewModelBuilder: () => SettingsViewModel(),
        onModelReady: (model) => this.model = model,
        builder: (context, model, child) {
          return Scaffold(
            appBar: _buildAppBar(theme),
            body: _buildBody(theme),
          );
        });
  }

  AppBar _buildAppBar(final ThemeData theme) {
    return AppBar(title: Text("Settings"));
  }

  PreferencePage _buildBody(final ThemeData theme) {
    return PreferencePage(
      [
        PreferenceTitle("Appereance"),
        ListTile(
            title: Text('Choose theme'),
            subtitle: Text(model.currentTheme),
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
                bool success = await kMethodChannel
                    .invokeMethod("toggleDeviceAdmin", {"enabled": value});
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
                groupValue: model.currentTheme,
                value: 'Light',
                onChanged: setTheme,
              ),
              RadioListTile(
                title: Text('Dark'),
                groupValue: model.currentTheme,
                value: 'Dark',
                onChanged: setTheme,
              ),
              RadioListTile(
                title: Text('Black'),
                groupValue: model.currentTheme,
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
    model.updateTheme(value);
  }
}
