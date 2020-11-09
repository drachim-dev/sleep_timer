import 'package:flutter/material.dart';
import 'package:logger/logger.dart';
import 'package:preferences/preference_page.dart';
import 'package:preferences/preferences.dart';
import 'package:sleep_timer/app/logger.util.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/common/theme.dart';
import 'package:sleep_timer/model/product.dart';
import 'package:sleep_timer/ui/widgets/section_header.dart';
import 'package:stacked/stacked.dart';

import 'settings_viewmodel.dart';

class SettingsView extends StatefulWidget {
  final bool deviceAdminFocused, notificationSettingsAccessFocused;

  const SettingsView(
      {deviceAdminFocused = false, notificationSettingsAccessFocused = false})
      : this.deviceAdminFocused = deviceAdminFocused ?? false,
        this.notificationSettingsAccessFocused =
            notificationSettingsAccessFocused ?? false;

  @override
  _SettingsViewState createState() => _SettingsViewState();
}

class _SettingsViewState extends State<SettingsView>
    with SingleTickerProviderStateMixin {
  final Logger log = getLogger();
  final _scaffoldKey = GlobalKey<ScaffoldState>();

  SettingsViewModel model;

  AnimationController _controller;
  Animatable<Color> blinkingFocus;
  Duration snackBarDelay = const Duration(seconds: 2);

  @override
  void initState() {
    super.initState();

    _controller = AnimationController(
      duration: const Duration(milliseconds: 500),
      reverseDuration: const Duration(milliseconds: 100),
      vsync: this,
    );

    if (widget.deviceAdminFocused || widget.notificationSettingsAccessFocused) {
      final String message = widget.deviceAdminFocused
          ? "Please enable device admin"
          : "Please enable access to notification settings";

      WidgetsBinding.instance.addPostFrameCallback(
          (_) => Future.delayed(snackBarDelay).then((value) {
                _scaffoldKey.currentState.showSnackBar(SnackBar(
                    shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(12)),
                    content: Text(message)));
              }));
    }
  }

  void initAnimations(final ThemeData theme) async {
    blinkingFocus = ColorTween(
      begin: Colors.transparent,
      end: theme.focusColor,
    );

    await _controller.forward();
    await _controller.reverse();
    await _controller.forward();
    await _controller.reverse();
    await _controller.forward();
  }

  @override
  Widget build(BuildContext context) {
    final ThemeData theme = Theme.of(context);

    return ViewModelBuilder<SettingsViewModel>.reactive(
        viewModelBuilder: () => SettingsViewModel(),
        onModelReady: (model) {
          this.model = model;
          if (widget.deviceAdminFocused ||
              widget.notificationSettingsAccessFocused) {
            initAnimations(theme);
          }
        },
        builder: (context, model, child) {
          return Scaffold(
            key: _scaffoldKey,
            appBar: _buildAppBar(theme),
            body: _buildBody(theme),
          );
        });
  }

  AppBar _buildAppBar(final ThemeData theme) {
    return AppBar(title: Text("Settings"));
  }

  PreferencePage _buildBody(final ThemeData theme) {
    log.d("Products for InApp-Purchase found: ${model.products.length}");

    return PreferencePage(
      [
        SectionHeader("Appearance",
            dense: true, leftPadding: kPreferenceTitleLeftPadding),
        for (var option in _buildAppearance(theme)) option,
        SectionHeader("Support me",
            dense: true, leftPadding: kPreferenceTitleLeftPadding),
        for (var product in model.products) _buildProduct(theme, product),
        SectionHeader("Advanced",
            dense: true, leftPadding: kPreferenceTitleLeftPadding),
        for (var option in _buildAdvanced(theme)) option,
        SectionHeader("Other",
            dense: true, leftPadding: kPreferenceTitleLeftPadding),
        ListTile(title: Text("FAQ"), onTap: () => model.navigateToFAQ()),
        ListTile(
            title: Text("Credits"), onTap: () => model.navigateToCredits()),
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
              children: themeList
                  .map((myTheme) {
                    return RadioListTile(
                      title: Text(myTheme.title),
                      groupValue: model.currentTheme.id,
                      value: myTheme.id,
                      onChanged: model.updateTheme);
                  })
                  .toList()),
        );
      },
    );
  }

  List<Widget> _buildAppearance(final ThemeData theme) {
    return [
      ListTile(
        title: Text('Choose theme'),
        subtitle: Text(model.currentTheme.title),
        leading: Icon(Icons.color_lens_outlined),
        onTap: showThemeDialog,
      ),
      SwitchListTile(
        title: Text('Show timer glow'),
        subtitle: Text("Enables shadow effect around the timers progress bar"),
        isThreeLine: true,
        secondary: Icon(Icons.blur_on_outlined),
        value: model.glow,
        onChanged: model.onChangeGlow,
      ),
    ];
  }

  List<Widget> _buildAdvanced(final ThemeData theme) {
    return [
      AnimatedBuilder(
          animation: _controller,
          builder: (context, child) {
            return Container(
              color: widget.deviceAdminFocused
                  ? blinkingFocus
                      .evaluate(AlwaysStoppedAnimation(_controller.value))
                  : Colors.transparent,
              child: SwitchListTile(
                  secondary: Icon(Icons.security_outlined),
                  title: Text("Device admin"),
                  subtitle: Text(
                      "Allow app to manage device functions. Enables screen off action."),
                  isThreeLine: true,
                  value: model.deviceAdmin,
                  onChanged: model.onChangeDeviceAdmin),
            );
          }),
      AnimatedBuilder(
          animation: _controller,
          builder: (context, child) {
            return Container(
              color: widget.notificationSettingsAccessFocused
                  ? blinkingFocus
                      .evaluate(AlwaysStoppedAnimation(_controller.value))
                  : Colors.transparent,
              child: SwitchListTile(
                  secondary: Icon(Icons.do_not_disturb_on),
                  title: Text("Notification Settings Access"),
                  subtitle: Text(
                      "Allow access to notification settings. Enables do not disturb action."),
                  isThreeLine: true,
                  value: model.notificationSettingsAccess,
                  onChanged: model.onChangeNotificationSettingsAccess),
            );
          }),
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
