import 'package:flutter/material.dart';
import 'package:preferences/preference_page.dart';
import 'package:preferences/preference_title.dart';
import 'package:preferences/preferences.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/model/product.dart';
import 'package:stacked/stacked.dart';

import 'settings_viewmodel.dart';

class SettingsView extends StatefulWidget {
  @override
  _SettingsViewState createState() => _SettingsViewState();
}

class _SettingsViewState extends State<SettingsView> {
  SettingsViewModel model;

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
    print("Warum?? ");
    print(model.products.length);

    return PreferencePage(
      [
        PreferenceTitle("Appearance", leftPadding: kPreferenceTitleLeftPadding),
        ListTile(
            title: Text('Choose theme'),
            subtitle: Text(model.currentTheme),
            leading: Icon(Icons.color_lens_outlined),
            onTap: () => showThemeDialog()),
        PreferenceTitle("Support me", leftPadding: kPreferenceTitleLeftPadding),
        for (var product in model.products) _buildProduct(theme, product),
        ExpansionTile(
            title: Text(
              "Experiments",
              style: theme.textTheme.bodyText1.copyWith(
                  color: Theme.of(context).accentColor,
                  fontWeight: FontWeight.bold),
            ),
            initiallyExpanded: model.experimentActive,
            children: _buildExperimental(theme)),
        PreferenceTitle("Other", leftPadding: kPreferenceTitleLeftPadding),
        ListTile(title: Text("FAQ")),
        ListTile(title: Text("Credits")),
      ],
    );
  }

  ListTile _buildProduct(ThemeData theme, Product product) {
    String title;
    IconData icon;
    switch (product.productDetails.id) {
      case kProductDonation:
        title = "Donation";
        icon = Icons.local_cafe_outlined;
        break;
      case kProductRemoveAds:
        title = "Remove ads";
        icon = Icons.cleaning_services_outlined;
        break;
      default:
    }

    final bool purchased = product.purchased;
    final TextStyle priceStyle = purchased
        ? theme.textTheme.bodyText1.copyWith(color: Colors.green)
        : theme.textTheme.bodyText1;

    return ListTile(
      leading: Icon(icon),
      title: Text(title),
      subtitle: Text(product.productDetails.description),
      trailing: Text(
        purchased ? "Already\npurchased" : product.productDetails.price,
        textAlign: TextAlign.center,
        style: priceStyle,
      ),
      onTap: () => purchased ? null : model.buyProduct(product),
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

  List<Widget> _buildExperimental(final ThemeData theme) {
    return [
      SwitchListTile(
          secondary: Icon(Icons.security_outlined),
          title: Text("Device admin"),
          subtitle: Text(
              "Allow app to manage device functions. Enables screen off action."),
          isThreeLine: true,
          value: model.deviceAdmin,
          onChanged: (value) => model.onChangeDeviceAdmin(value)),
      SwitchListTile(
          secondary: Icon(Icons.do_not_disturb_on),
          title: Text("Notification Settings Access"),
          subtitle: Text(
              "Allow access to notification settings. Enables do not disturb action."),
          isThreeLine: true,
          value: model.notificationSettingsAccess,
          onChanged: (value) =>
              model.onChangeNotificationSettingsAccess(value)),
      // TODO: Enable connection to philips hue
      if (false)
        SwitchListTile(
            secondary: Icon(Icons.lightbulb_outline),
            title: Text("Philips Hue"),
            subtitle: Text(
                "Connect to your Philips hue bridge. Enables light actions."),
            isThreeLine: true,
            value: model.deviceAdmin,
            onChanged: null),
    ];
  }
}
