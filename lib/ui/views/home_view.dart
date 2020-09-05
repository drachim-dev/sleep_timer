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
            bottomNavigationBar: _buildBottomNavigationBar(),
            floatingActionButton: _buildFAB(),
            floatingActionButtonLocation:
                FloatingActionButtonLocation.centerDocked,
          );
        });
  }

  AppBar _buildAppBar(ThemeData theme) {
    return AppBar(
      bottom: PreferredSize(
        preferredSize: Size(64, 64),
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
    );
  }

  FadeIndexedStack _buildBody() {
    return FadeIndexedStack(
        index: model.currentIndex,
        children: _navItems.map((item) {
          return Padding(
            padding: const EdgeInsets.symmetric(vertical: 32.0, horizontal: 8),
            child: item.page,
          );
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

  Widget _buildFAB() {
    final NavigationModel item = _navItems[model.currentIndex];

    return ScaleTransition(
      scale: _scaleAnimation,
      child: AnimatedSwitcher(
        duration: kThemeAnimationDuration,
        transitionBuilder: (Widget child, Animation<double> animation) {
          return ScaleTransition(child: child, scale: animation);
        },
        child: FloatingActionButton(
          key: ValueKey(item.title),
          onPressed: () => item.controller.onClickFAB(),
          child: item.fabIcon,
        ),
      ),
    );
  }
}
