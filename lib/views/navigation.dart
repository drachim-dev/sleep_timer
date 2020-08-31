import 'package:auto_route/auto_route.dart';
import 'package:flutter/material.dart';
import 'package:sleep_timer/model/navigation_item.dart';
import 'package:sleep_timer/auto_router.gr.dart';
import 'package:sleep_timer/views/alarm.dart';
import 'package:sleep_timer/views/timer.dart';
import 'package:sleep_timer/widgets/fade_indexed_stack.dart';

enum MenuOption { settings }

class NavigationPage extends StatefulWidget {
  @override
  _NavigationPageState createState() => _NavigationPageState();
}

class _NavigationPageState extends State<NavigationPage>
    with TickerProviderStateMixin {
  final _timerController = NavigationItemController();
  final _alarmController = NavigationItemController();

  List<NavigationItem> _navItems;
  int _index = 0;

  AnimationController _animationController;
  Animation<double> _scaleAnimation;

  @override
  void initState() {
    _navItems = [
      NavigationItem(
        page: TimerPage(controller: _timerController),
        controller: _timerController,
        title: "Timer",
        icon: Icon(Icons.snooze_outlined),
        fabIcon: Icon(Icons.play_arrow_outlined),
      ),
      NavigationItem(
        page: AlarmPage(controller: _alarmController),
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
    final NavigationItem _currentItem = _navItems[_index];

    return Scaffold(
      appBar: _buildAppBar(theme),
      body: _buildBody(),
      bottomNavigationBar: _buildBottomNavigationBar(),
      floatingActionButton: _buildFAB(_currentItem),
      floatingActionButtonLocation: FloatingActionButtonLocation.centerDocked,
    );
  }

  AppBar _buildAppBar(ThemeData theme) {
    return AppBar(
      bottom: PreferredSize(
        preferredSize: Size(64, 64),
        child: Text(
          _navItems[_index].title,
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
        index: _index,
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
          currentIndex: _index,
          onTap: (index) => setState(() => _index = index),
          items: _navItems.map((item) {
            return BottomNavigationBarItem(icon: item.icon, label: item.title);
          }).toList()),
    );
  }

  void _onMenuOption(MenuOption option) {
    switch (option) {
      case MenuOption.settings:
        ExtendedNavigator.root.push(Routes.settingsPage);
        break;
      default:
    }
  }

  Widget _buildFAB(final NavigationItem item) {
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
