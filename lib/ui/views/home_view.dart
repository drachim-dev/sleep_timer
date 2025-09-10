import 'package:flutter/material.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/generated/l10n.dart';
import 'package:sleep_timer/ui/widgets/rounded_rect_button.dart';
import 'package:stacked/stacked.dart';

import '../widgets/timer_slider.dart';
import 'home_viewmodel.dart';

enum MenuOption { settings }

class HomeView extends StatefulWidget {
  final String? timerId;

  const HomeView({this.timerId});

  @override
  State<HomeView> createState() => _HomeViewState();
}

class _HomeViewState extends State<HomeView> with TickerProviderStateMixin {
  late AnimationController _animationController;
  late Animation<double> _scaleAnimation;

  late HomeViewModel viewModel;

  @override
  void initState() {
    super.initState();

    _animationController = AnimationController(
        duration: const Duration(milliseconds: 800), vsync: this);
    _scaleAnimation = CurvedAnimation(
        parent: _animationController, curve: Curves.easeInOutBack);
    _animationController.forward();

    // Request notification permission
    WidgetsBinding.instance.addPostFrameCallback((_) async {
      final permissionStatus = await Permission.notification.request();
      if (permissionStatus.isPermanentlyDenied) {
        await Future.delayed(Duration(seconds: 1, milliseconds: 500));
        ScaffoldMessenger.of(context).showSnackBar(_buildSnackBar(context));
      }
    });
  }

  SnackBar _buildSnackBar(BuildContext context) {
    final theme = Theme.of(context);

    return SnackBar(
      content: Row(
        children: [
          Icon(Icons.info_rounded, color: theme.colorScheme.primary),
          const SizedBox(width: 8),
          Flexible(
              child: Text(S.of(context).notificationsRequired,
                  style: theme.textTheme.titleSmall)),
        ],
      ),
      behavior: SnackBarBehavior.floating,
      duration: Duration(seconds: 5),
      shape: StadiumBorder(),
      action: SnackBarAction(
        label: S.of(context).settings,
        onPressed: () => openAppSettings(),
      ),
    );
  }

  @override
  void dispose() {
    _animationController.dispose();

    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return ViewModelBuilder<HomeViewModel>.reactive(
        viewModelBuilder: () => HomeViewModel(),
        onViewModelReady: (viewModel) {
          this.viewModel = viewModel;

          if (widget.timerId == null) {
            this.viewModel.mayAskForReview();
          } else {
            this.viewModel.activeTimerId = widget.timerId;
          }
        },
        builder: (context, viewModel, _) {
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
    const padding = 20.0;

    return PreferredSize(
      preferredSize: Size.fromHeight(kToolbarHeight + padding + 4),
      child: SafeArea(
        child: AppBar(
          centerTitle: true,
          flexibleSpace: Padding(
            padding: const EdgeInsets.only(top: padding),
            child: Text(
              S.of(context).sleepTimer,
              style: theme.textTheme.displayMedium!.copyWith(fontSize: 48),
              textAlign: TextAlign.center,
            ),
          ),
          actions: [
            PopupMenuButton<MenuOption>(
              onSelected: _onMenuOption,
              itemBuilder: (context) => [
                PopupMenuItem(
                  value: MenuOption.settings,
                  child: Text(S.of(context).settings),
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
        viewModel.navigateToSettings();
        break;
    }
  }

  Widget _buildBody(final ThemeData theme) {
    return LayoutBuilder(builder: (context, constraint) {
      return SingleChildScrollView(
        child: ConstrainedBox(
          constraints: BoxConstraints(minHeight: constraint.maxHeight),
          child: IntrinsicHeight(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.spaceAround,
              children: [
                viewModel.hasActiveTimer
                    ? _buildActiveTimer(theme)
                    : SizedBox(),
                TimerSlider(
                  minValue: 1,
                  maxValue: 60,
                  initialValue: viewModel.initialTime,
                  animationEnabled: false,
                  showGlow: viewModel.showGlow,
                  onChange: (hours, minutes) {
                    final minutesTotal = hours * 60 + minutes;
                    viewModel.setTimeSilent(minutesTotal.round());
                  },
                  child: _buildTimeLabel,
                ),
                Padding(
                  padding: const EdgeInsets.only(
                      bottom: kFloatingActionButtonHeight),
                  child: Container(
                    height: 86,
                    width: double.maxFinite,
                    alignment: Alignment.center,
                    child: ListView(
                        scrollDirection: Axis.horizontal,
                        shrinkWrap: true,
                        children: [15, 30, 45, 60, 90].map((value) {
                          return RoundedRectButton(
                              title: '$value',
                              onPressed: () => viewModel.setTime(value));
                        }).toList()),
                  ),
                ),
              ],
            ),
          ),
        ),
      );
    });
  }

  Widget _buildTimeLabel(int hours, int minutes) {
    final ThemeData theme = Theme.of(context);
    final TextTheme textTheme = theme.textTheme;

    final minuteStyle = textTheme.displayMedium;
    final hourStyle =
        textTheme.headlineSmall?.copyWith(color: theme.colorScheme.primary);

    final formattedMinutes = S.of(context).numberOfMinutesShort(minutes);

    return Column(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        Text(formattedMinutes, style: minuteStyle),
        if (hours > 0)
          Padding(
            padding: const EdgeInsets.only(top: kVerticalPaddingSmall),
            child: Text('+ $hours h', style: hourStyle),
          ),
      ],
    );
  }

  Widget _buildActiveTimer(ThemeData theme) {
    return OutlinedButton(
      style: OutlinedButton.styleFrom(
        padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 12),
        side: BorderSide(
          color: theme.colorScheme.primary.withAlpha(50),
          width: 3,
        ),
      ),
      onPressed: viewModel.openActiveTimer,
      child: Text(S.of(context).buttonOpenSavedTimer),
    );
  }

  Widget _buildFAB(final ThemeData theme) {
    final foregroundColor = Colors.white;

    final textStyle =
        theme.textTheme.titleLarge!.copyWith(color: foregroundColor);

    return ScaleTransition(
      scale: _scaleAnimation,
      child: AnimatedSwitcher(
        duration: kThemeAnimationDuration,
        transitionBuilder: (Widget child, Animation<double> animation) {
          return ScaleTransition(scale: animation, child: child);
        },
        child: FloatingActionButton.extended(
          onPressed: viewModel.startNewTimer,
          icon: Icon(Icons.bedtime_outlined, color: foregroundColor),
          label: Text(S.of(context).buttonTimerStart, style: textStyle),
        ),
      ),
    );
  }
}
