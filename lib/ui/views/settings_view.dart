import 'package:flutter/material.dart';
import 'package:flutter/scheduler.dart';
import 'package:logger/logger.dart';
import 'package:sleep_timer/app/logger.util.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/common/theme.dart';
import 'package:sleep_timer/generated/l10n.dart';
import 'package:sleep_timer/main.dart';
import 'package:sleep_timer/model/product.dart';
import 'package:sleep_timer/ui/widgets/section_header.dart';
import 'package:stacked/stacked.dart';

import 'settings_viewmodel.dart';

class SettingsView extends StatefulWidget {
  final bool deviceAdminFocused, notificationSettingsAccessFocused;

  const SettingsView(
      {deviceAdminFocused = false, notificationSettingsAccessFocused = false})
      : deviceAdminFocused = deviceAdminFocused ?? false,
        notificationSettingsAccessFocused =
            notificationSettingsAccessFocused ?? false;

  @override
  _SettingsViewState createState() => _SettingsViewState();
}

class _SettingsViewState extends State<SettingsView>
    with SingleTickerProviderStateMixin {
  final Logger log = getLogger();

  SettingsViewModel model;

  final ScrollController _scrollController = ScrollController();
  AnimationController _controller;
  Animatable<Color> blinkingFocus;
  Duration snackBarDelay = const Duration(milliseconds: 200);

  @override
  void initState() {
    super.initState();

    _controller = AnimationController(
      duration: const Duration(milliseconds: 500),
      reverseDuration: const Duration(milliseconds: 100),
      vsync: this,
    );

    if (widget.deviceAdminFocused || widget.notificationSettingsAccessFocused) {
      var message = '';

      if (widget.deviceAdminFocused) {
        message = S.current.prefsHintEnableDeviceAdmin;
      }

      if (widget.notificationSettingsAccessFocused) {
        message = S.current.prefsHintEnableAccessToNotificationSettings;
      }

      SchedulerBinding.instance.addPostFrameCallback((_) {
        Future.delayed(snackBarDelay).then((value) {
          mainScaffoldMessengerKey.currentState.showSnackBar(SnackBar(
              shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(12)),
              content: Text(message)));
        });
      });

      SchedulerBinding.instance.addPostFrameCallback((_) {
        Future.delayed(Duration(milliseconds: 500)).then((value) => {
              _scrollController.animateTo(
                  _scrollController.position.maxScrollExtent,
                  duration: kThemeAnimationDuration,
                  curve: Curves.easeOut)
            });
      });
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
    final theme = Theme.of(context);

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
            appBar: _buildAppBar(theme),
            body: _buildBody(theme),
          );
        });
  }

  AppBar _buildAppBar(final ThemeData theme) {
    return AppBar(title: Text(S.of(context).settings));
  }

  Widget _buildBody(final ThemeData theme) {
    return ListView(
      controller: _scrollController,
      children: [
        SectionHeader(S.of(context).appearanceSectionTitle,
            dense: true, leftPadding: kPreferenceTitleLeftPadding),
        for (var option in _buildAppearance(theme)) option,
        SectionHeader(S.of(context).timerSettingsSectionTitle,
            dense: true, leftPadding: kPreferenceTitleLeftPadding),
        for (var option in _buildTimerSettings(theme)) option,
        SectionHeader(S.of(context).purchasesSectionTitle,
            dense: true, leftPadding: kPreferenceTitleLeftPadding),
        for (var product in model.products) _buildProduct(theme, product),
        SectionHeader(S.of(context).advancedSectionTitle,
            dense: true, leftPadding: kPreferenceTitleLeftPadding),
        for (var option in _buildAdvanced(theme)) option,
        SectionHeader(S.of(context).otherSectionTitle,
            dense: true, leftPadding: kPreferenceTitleLeftPadding),
        ListTile(
            title: Text(S.of(context).faqShort),
            onTap: () => model.navigateToFAQ()),
        ListTile(
            title: Text(S.of(context).creditsAppTitle),
            onTap: () => model.navigateToCredits()),
      ],
    );
  }

  List<Widget> _buildAppearance(final ThemeData theme) {
    return [
      ListTile(
        title: Text(S.of(context).chooseThemeTitle),
        subtitle: Text(model.currentTheme.title),
        leading: Icon(Icons.color_lens_outlined),
        onTap: _showThemeDialog,
      ),
      SwitchListTile(
        title: Text(S.of(context).showTimerGlow),
        subtitle: Text(S.of(context).showTimerGlowDescription),
        isThreeLine: true,
        secondary: Icon(Icons.blur_on_outlined),
        value: model.glow,
        onChanged: model.onChangeGlow,
      ),
    ];
  }

  Future<void> _showThemeDialog() {
    return showDialog(
      context: context,
      builder: (_) {
        return AlertDialog(
          title: Text(S.of(context).chooseThemeTitle),
          content: Column(
              mainAxisSize: MainAxisSize.min,
              children: themeList.map((myTheme) {
                return RadioListTile(
                    title: Text(myTheme.title),
                    groupValue: model.currentTheme.id,
                    value: myTheme.id,
                    onChanged: model.updateTheme);
              }).toList()),
        );
      },
    );
  }

  List<Widget> _buildTimerSettings(final ThemeData theme) {
    return [
      PopupMenuButton(
        enabled: model.hasAccelerometer,
        child: ListTile(
          enabled: model.hasAccelerometer,
          leading: Icon(Icons.timer_outlined),
          title: Text(S.of(context).prefsExtendTimeOnShake),
          subtitle: model.hasAccelerometer
              ? Text(S.of(context).numberOfMinutesLong(model.extendTimeByShake))
              : Text(S.of(context).notSupported),
        ),
        tooltip: S.of(context).extendTimeByShakeMenuToolTip,
        offset: Offset(1, 0),
        initialValue: model.extendTimeByShake,
        onSelected: model.onChangeExtendTimeByShake,
        itemBuilder: (context) => kExtendTimeByShakeOptions.map((minutes) {
          return PopupMenuItem(
            child: Text(S.of(context).numberOfMinutesShort(minutes)),
            value: minutes,
          );
        }).toList(),
      ),
    ];
  }

  ListTile _buildProduct(ThemeData theme, Product product) {
    IconData icon;
    switch (product.productDetails.id) {
      case kProductDonation:
        icon = Icons.local_cafe_outlined;
        break;
      case kProductRemoveAds:
        icon = Icons.cleaning_services_outlined;
        break;
      default:
    }

    final purchased = product.purchased;
    final priceStyle = purchased
        ? theme.textTheme.bodyText1.copyWith(color: Colors.green)
        : theme.textTheme.bodyText1;

    return ListTile(
      leading: Icon(icon),
      title: Text(product.productDetails.title.split(' (').first),
      subtitle: Text(product.productDetails.description),
      trailing: Text(
        purchased
            ? S.of(context).alreadyPurchased
            : product.productDetails.price,
        textAlign: TextAlign.center,
        style: priceStyle,
      ),
      onTap: () => purchased ? null : model.buyProduct(product),
    );
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
                  title: Text(S.of(context).prefsDeviceAdmin),
                  subtitle: Text(S.of(context).prefsDeviceAdminDescription),
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
                  title: Text(S.of(context).prefsNotificationSettingsAccess),
                  subtitle: Text(
                      S.of(context).prefsNotificationSettingsAccessDescription),
                  isThreeLine: true,
                  value: model.notificationSettingsAccess,
                  onChanged: model.onChangeNotificationSettingsAccess),
            );
          }),
    ];
  }
}
