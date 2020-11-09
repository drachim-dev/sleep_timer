import 'package:flutter/material.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/ui/widgets/rounded_rect_button.dart';
import 'package:sleep_timer/ui/widgets/timer_slider.dart';
import 'package:stacked/stacked.dart';

import 'home_viewmodel.dart';

enum MenuOption { settings }

class HomeView extends StatefulWidget {
  @override
  _HomeViewState createState() => _HomeViewState();
}

class _HomeViewState extends State<HomeView> with TickerProviderStateMixin {
  AnimationController _animationController;
  Animation<double> _scaleAnimation;

  HomeViewModel model;

  @override
  void initState() {
    _animationController = AnimationController(
        duration: const Duration(milliseconds: 800), vsync: this);
    _scaleAnimation = CurvedAnimation(
        parent: _animationController, curve: Curves.easeInOutBack);
    _animationController.forward();

    super.initState();
  }

  @override
  void dispose() {
    super.dispose();

    _animationController.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final ThemeData theme = Theme.of(context);

    return ViewModelBuilder<HomeViewModel>.reactive(
        viewModelBuilder: () => HomeViewModel(),
        onModelReady: (model) => this.model = model,
        builder: (context, model, child) {
          return Scaffold(
            appBar: _buildAppBar(theme),
            body: _buildBody(theme),
            floatingActionButton: _buildFAB(theme),
            floatingActionButtonLocation:
                FloatingActionButtonLocation.centerFloat,
          );
        });
  }

  PreferredSize _buildAppBar(ThemeData theme) {
    const double padding = 20;

    return PreferredSize(
      preferredSize: Size.fromHeight(kToolbarHeight + padding + 4),
      child: SafeArea(
        child: AppBar(
          centerTitle: true,
          flexibleSpace: Padding(
            padding: const EdgeInsets.only(top: padding),
            child: Text(
              "Sleep Timer",
              style: theme.textTheme.headline2.copyWith(fontSize: 48),
              textAlign: TextAlign.center,
            ),
          ),
          actions: [
            PopupMenuButton<MenuOption>(
              onSelected: _onMenuOption,
              itemBuilder: (context) => [
                PopupMenuItem(
                  child: Text("Settings"),
                  value: MenuOption.settings,
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  void _onMenuOption(MenuOption option) {
    switch (option) {
      case MenuOption.settings:
        model.navigateToSettings();
        break;
      default:
    }
  }

  Widget _buildBody(final ThemeData theme) {
    return Padding(
      padding: const EdgeInsets.symmetric(
          horizontal: kHorizontalPadding, vertical: kVerticalPaddingBig),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.spaceAround,
        children: [
          model.hasActiveTimer ? _buildActiveTimers(theme) : SizedBox(),
          TimerSlider(
            minValue: 1,
            initialValue: model.initialTime,
            onUpdateLabel: (value) => "$value Min",
            onChange: (value) => model.setTime(value.round()),
            showGlow: model.showGlow,
          ),
          Padding(
            padding: const EdgeInsets.only(bottom: kVerticalPaddingBig),
            child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: [10, 20, 30, 60].map((value) {
                  return Expanded(
                    child: RoundedRectButton(
                        title: "$value",
                        onPressed: () => model.updateTime(value)),
                  );
                }).toList()),
          ),
        ],
      ),
    );
  }

  Widget _buildActiveTimers(ThemeData theme) {
    return FlatButton(
      child: Text("Tap here to see your timer"),
      padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 12),
      shape: StadiumBorder(
          side: BorderSide(
        color: theme.colorScheme.primary.withAlpha(50),
        width: 3,
      )),
      onPressed: () => model.openActiveTimer(),
    );
  }

  Widget _buildFAB(final ThemeData theme) {
    final Color foregroundColor = Colors.white;

    final TextStyle textStyle =
        theme.accentTextTheme.headline6.copyWith(color: foregroundColor);

    return ScaleTransition(
      scale: _scaleAnimation,
      child: AnimatedSwitcher(
        duration: kThemeAnimationDuration,
        transitionBuilder: (Widget child, Animation<double> animation) {
          return ScaleTransition(child: child, scale: animation);
        },
        child: FloatingActionButton.extended(
          onPressed: () => model.startNewTimer(),
          icon: Icon(Icons.bedtime_outlined, color: foregroundColor),
          label: Text("Go to sleep", style: textStyle),
        ),
      ),
    );
  }
}
