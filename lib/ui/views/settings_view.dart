import 'package:flutter/material.dart';
import 'package:flutter/scheduler.dart';
import 'package:logger/logger.dart';
import 'package:sleep_timer/app/logger.util.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/common/theme.dart';
import 'package:sleep_timer/generated/l10n.dart';
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
  State<SettingsView> createState() => _SettingsViewState();
}

class _SettingsViewState extends State<SettingsView>
    with SingleTickerProviderStateMixin {
  final Logger log = getLogger();

  late SettingsViewModel viewModel;

  final ScrollController _scrollController = ScrollController();
  late AnimationController _controller;
  late Animatable<Color?> blinkingFocus;

  @override
  void initState() {
    super.initState();

    _controller = AnimationController(
      duration: const Duration(milliseconds: 500),
      reverseDuration: const Duration(milliseconds: 100),
      vsync: this,
    );

    if (widget.deviceAdminFocused || widget.notificationSettingsAccessFocused) {
      SchedulerBinding.instance.addPostFrameCallback((_) {
        Future.delayed(Duration(milliseconds: 750)).then((value) => {
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
  void dispose() {
    _controller.dispose();

    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return ViewModelBuilder<SettingsViewModel>.reactive(
        viewModelBuilder: () => SettingsViewModel(),
        onViewModelReady: (viewModel) {
          this.viewModel = viewModel;
          if (widget.deviceAdminFocused ||
              widget.notificationSettingsAccessFocused) {
            initAnimations(theme);
          }
        },
        builder: (context, viewModel, _) {
          return Scaffold(
            appBar: _buildAppBar(theme),
            body: _buildBody(theme),
          );
        });
  }

  AppBar _buildAppBar(final ThemeData theme) {
    return AppBar(
      title: Text(S.of(context).settings),
    );
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
        ListTile(
          leading: Icon(Icons.star_outline_outlined),
          title: Text(S.of(context).rateAppTitle),
          subtitle: Text(S.of(context).rateAppSubtitle),
          trailing: Text(S.of(context).rateAppPrice),
          onTap: viewModel.openStoreListing,
        ),
        for (var product in viewModel.products) _buildProduct(theme, product),
        SectionHeader(S.of(context).advancedSectionTitle,
            dense: true, leftPadding: kPreferenceTitleLeftPadding),
        for (var option in _buildAdvanced(theme)) option,
        SectionHeader(S.of(context).otherSectionTitle,
            dense: true, leftPadding: kPreferenceTitleLeftPadding),
        ListTile(
          title: Text(S.of(context).faqShort),
          onTap: viewModel.navigateToFAQ,
        ),
        ListTile(
          title: Text(S.of(context).creditsAppTitle),
          onTap: viewModel.navigateToCredits,
        ),
      ],
    );
  }

  List<Widget> _buildAppearance(final ThemeData theme) {
    return [
      ListTile(
        title: Text(S.of(context).chooseThemeTitle),
        subtitle: Text(viewModel.currentTheme.title),
        leading: Icon(Icons.color_lens_outlined),
        onTap: _showThemeDialog,
      ),
      SwitchListTile(
        title: Text(S.of(context).showTimerGlow),
        subtitle: Text(S.of(context).showTimerGlowDescription),
        isThreeLine: true,
        secondary: Icon(Icons.blur_on_outlined),
        value: viewModel.glow,
        onChanged: viewModel.onChangeGlow,
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
                    groupValue: viewModel.currentTheme.id,
                    value: myTheme.id,
                    onChanged: viewModel.setTheme);
              }).toList()),
        );
      },
    );
  }

  List<Widget> _buildTimerSettings(final ThemeData theme) {
    final unselectedColor = viewModel.hasAccelerometer
        ? theme.textTheme.subtitle1!.color
        : theme.unselectedWidgetColor;

    final selectedColor = theme.textTheme.subtitle1!.color;

    return [
      Theme(
        data: Theme.of(context).copyWith(
          textTheme: theme.textTheme.apply(bodyColor: unselectedColor),
          unselectedWidgetColor: unselectedColor,
          dividerColor: Colors.transparent,
        ),
        child: IgnorePointer(
          ignoring: !viewModel.hasAccelerometer,
          child: ExpansionTile(
            textColor: selectedColor,
            iconColor: selectedColor,
            leading: Icon(Icons.vibration_outlined),
            onExpansionChanged: viewModel.onChangeExtendByShake,
            initiallyExpanded:
                viewModel.hasAccelerometer && viewModel.extendByShake,
            title: Text(S.of(context).prefsExtendTimeOnShake),
            subtitle: !viewModel.hasAccelerometer
                ? Text(S.of(context).notSupported)
                : null,
            trailing: IgnorePointer(
              child: Switch(
                value: viewModel.hasAccelerometer && viewModel.extendByShake,
                onChanged: (_) {},
              ),
            ),
            children: [
              PopupMenuButton(
                enabled: viewModel.hasAccelerometer,
                tooltip: S.of(context).extendTimeByShakeMenuToolTip,
                offset: Offset(1, 0),
                initialValue: viewModel.extendTimeByShake,
                onSelected: viewModel.onChangeExtendTimeByShake,
                itemBuilder: (context) =>
                    kExtendTimeByShakeOptions.map((minutes) {
                  return PopupMenuItem(
                    value: minutes,
                    child: Text(S.of(context).numberOfMinutesShort(minutes)),
                  );
                }).toList(),
                child: ListTile(
                  leading: SizedBox(),
                  enabled: viewModel.hasAccelerometer,
                  title: Text(S
                      .of(context)
                      .byNumberOfMinutesLong(viewModel.extendTimeByShake)),
                  subtitle: Text(S.of(context).extendTimeByShakeMenuToolTip),
                ),
              ),
            ],
          ),
        ),
      ),
    ];
  }

  ListTile _buildProduct(ThemeData theme, Product product) {
    IconData? icon;
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
        ? theme.textTheme.bodyText1!.copyWith(color: Colors.green)
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
      onTap: () => purchased ? null : viewModel.buyProduct(product),
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
                  value: viewModel.deviceAdmin,
                  onChanged: viewModel.onChangeDeviceAdmin),
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
                  value: viewModel.notificationSettingsAccess,
                  onChanged: viewModel.onChangeNotificationSettingsAccess),
            );
          }),
    ];
  }
}
