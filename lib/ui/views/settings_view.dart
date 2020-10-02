import 'package:flutter/material.dart';
import 'package:preferences/preference_page.dart';
import 'package:preferences/preference_title.dart';
import 'package:preferences/preferences.dart';
import 'package:stacked/stacked.dart';
import 'settings_viewmodel.dart';

class SettingsView extends StatefulWidget {
  @override
  _SettingsViewState createState() => _SettingsViewState();
}

class _SettingsViewState extends State<SettingsView> {
  SettingsViewModel model;

  @override
  initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    final ThemeData theme = Theme.of(context);

    return ViewModelBuilder<SettingsViewModel>.reactive(
        viewModelBuilder: () => SettingsViewModel(),
        onModelReady: (model) {
          this.model = model..init();
        },
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
            leading: Icon(Icons.color_lens),
            onTap: () => showThemeDialog()),
        PreferenceTitle("Experimental"),
        SwitchListTile(
            secondary: Icon(Icons.security),
            title: Text("Device admin"),
            subtitle: Text(
                "Required for advanced actions such as switching off the display"),
            isThreeLine: true,
            value: model.deviceAdmin,
            onChanged: (value) => model.onChangeDeviceAdmin(value)),
        SwitchListTile(
            secondary: Icon(Icons.do_not_disturb_on),
            title: Text("Notification Settings Access"),
            subtitle: Text("Required for accessing do not disturb mode"),
            isThreeLine: true,
            value: model.notificationSettingsAccess,
            onChanged: (value) =>
                model.onChangeNotificationSettingsAccess(value)),
        PreferenceTitle("Support"),
        for (var product in model.products)
          ListTile(
            title: Text(product.title),
            subtitle: Text(product.description),
            trailing: Text(product.price),
            enabled: model.hasPurchased(product),
          ),
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
                onChanged: model.updateTheme,
              ),
              RadioListTile(
                title: Text('Dark'),
                groupValue: model.currentTheme,
                value: 'Dark',
                onChanged: model.updateTheme,
              ),
              RadioListTile(
                title: Text('Black'),
                groupValue: model.currentTheme,
                value: 'Black',
                onChanged: model.updateTheme,
              ),
            ],
          ),
        );
      },
    );
  }
}
