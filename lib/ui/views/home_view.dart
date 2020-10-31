import 'package:flutter/material.dart';
import 'package:sleep_timer/model/navigation_item_model.dart';
import 'package:sleep_timer/ui/views/alarm_view.dart';
import 'package:sleep_timer/ui/views/timer_view.dart';
import 'package:sleep_timer/ui/widgets/fade_indexed_stack.dart';
import 'package:stacked/stacked.dart';

import 'home_viewmodel.dart';

enum MenuOption { settings }

class HomeView extends StatefulWidget {
  @override
  _HomeViewState createState() => _HomeViewState();
}

class _HomeViewState extends State<HomeView> with TickerProviderStateMixin {
  final _timerController = NavigationItemController();
  final _alarmController = NavigationItemController();

  List<NavigationModel> _navItems;

  AnimationController _animationController;
  Animation<double> _scaleAnimation;

  HomeViewModel model;

  @override
  void initState() {
    _navItems = [
      NavigationModel(
        page: TimerView(controller: _timerController),
        controller: _timerController,
        title: "Timer",
        icon: Icon(Icons.snooze_outlined),
        fabIcon: Icon(Icons.play_arrow_outlined),
      ),
      NavigationModel(
        page: AlarmView(controller: _alarmController),
        controller: _alarmController,
        title: "Alarm",
        icon: Icon(Icons.alarm_outlined),
        fabIcon: Icon(Icons.add_outlined),
      ),
    ];

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
            body: _buildBody(),
            // bottomNavigationBar: _buildBottomNavigationBar(),
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
              _navItems[model.currentIndex].title,
              style: theme.textTheme.headline2,
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

  FadeIndexedStack _buildBody() {
    return FadeIndexedStack(
        index: model.currentIndex,
        children: _navItems.map((item) {
          return item.page;
        }).toList());
  }

  BottomAppBar _buildBottomNavigationBar() {
    return BottomAppBar(
      shape: CircularNotchedRectangle(),
      clipBehavior: Clip.antiAlias,
      child: BottomNavigationBar(
          type: BottomNavigationBarType.fixed,
          unselectedFontSize: 14,
          selectedFontSize: 14,
          currentIndex: model.currentIndex,
          onTap: model.setIndex,
          items: _navItems.map((item) {
            return BottomNavigationBarItem(icon: item.icon, label: item.title);
          }).toList()),
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

  Widget _buildFAB(final ThemeData theme) {
    final NavigationModel item = _navItems[model.currentIndex];
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
          key: ValueKey(item.title),
          onPressed: () => item.controller.onClickFAB(),
          icon: Icon(Icons.bedtime_outlined, color: foregroundColor),
          label: Text("Go to sleep", style: textStyle),
          // child: item.fabIcon,
        ),
      ),
    );
  }
}
