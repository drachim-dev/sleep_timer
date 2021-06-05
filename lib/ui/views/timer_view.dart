import 'dart:async';
import 'dart:convert';
import 'dart:ui';

import 'package:device_functions/messages_generated.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:material_design_icons_flutter/material_design_icons_flutter.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/common/utils.dart';
import 'package:sleep_timer/generated/l10n.dart';
import 'package:sleep_timer/model/app.dart';
import 'package:sleep_timer/model/timer_model.dart';
import 'package:sleep_timer/services/timer_service.dart';
import 'package:sleep_timer/ui/widgets/rounded_rect_button.dart';
import 'package:sleep_timer/ui/widgets/sabt.dart';
import 'package:sleep_timer/ui/widgets/section_header.dart';
import 'package:sleep_timer/ui/widgets/slider_dialog.dart';
import 'package:sleep_timer/ui/widgets/timer_slider.dart';
import 'package:sleep_timer/ui/widgets/toggle_button.dart';
import 'package:stacked/stacked.dart';

import 'timer_viewmodel.dart';

class TimerView extends StatefulWidget {
  final TimerModel? timerModel;

  const TimerView({Key? key, required this.timerModel});

  @override
  _TimerViewState createState() => _TimerViewState();
}

class _TimerViewState extends State<TimerView> with TickerProviderStateMixin {
  final ScrollController _scrollController = ScrollController();
  late AnimationController _hideFabAnimController;
  late AnimationController _fabAnimController;
  late Animation<Color?> _colorAnimation;

  late TimerViewModel viewModel;

  late Future<List<App>> alarmAppsFuture;
  late Future<List<App>> playerAppsFuture;

  Map<String, MemoryImage>? cachedAppIcons;

  @override
  void initState() {
    super.initState();

    initAnimations();
  }

  void initAnimations() {
    _hideFabAnimController = AnimationController(
        vsync: this, duration: kThemeAnimationDuration, value: 1);
    _fabAnimController = AnimationController(
        vsync: this, duration: kThemeAnimationDuration, value: 1);

    _colorAnimation = ColorTween(
      begin: Colors.redAccent,
      end: Colors.orangeAccent,
    ).animate(_fabAnimController)
      ..addListener(() => setState(() {}));
  }

  @override
  void dispose() {
    _scrollController.dispose();
    _hideFabAnimController.dispose();
    _fabAnimController.dispose();

    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return ViewModelBuilder<TimerViewModel>.reactive(
        viewModelBuilder: () => TimerViewModel(widget.timerModel!),
        onModelReady: (viewModel) {
          this.viewModel = viewModel;

          alarmAppsFuture = viewModel.alarmApps;
          playerAppsFuture = viewModel.playerApps;

          alarmAppsFuture.then((apps) {
            cachedAppIcons?.addAll({
              for (var e in apps)
                e.packageName!: MemoryImage(base64Decode(e.icon!))
            });
          });
          playerAppsFuture.then((apps) {
            cachedAppIcons?.addAll({
              for (var e in apps)
                e.packageName!: MemoryImage(base64Decode(e.icon!))
            });
          });
        },
        builder: (context, viewModel, _) {
          return WillPopScope(
            onWillPop: () async {
              await viewModel.navigateBack();
              return false;
            },
            child: NotificationListener(
              onNotification: _onScrollNotification,
              child: Scaffold(
                body: _buildBody(theme),
                floatingActionButton: _buildFAB(theme),
                floatingActionButtonLocation:
                    FloatingActionButtonLocation.centerFloat,
              ),
            ),
          );
        });
  }

  SliverAppBar _buildAppBar(final ThemeData theme) {
    final titleStyle = theme.textTheme.headline2!.copyWith(
        fontSize: 46, shadows: [Shadow(blurRadius: 5.0, color: Colors.white)]);

    return SliverAppBar(
      backwardsCompatibility: false,
      backgroundColor: theme.scaffoldBackgroundColor,
      primary: true,
      pinned: true,
      expandedHeight: 272,
      leading: IconButton(
        icon: Icon(Icons.arrow_back),
        onPressed: () => viewModel.navigateBack(),
      ),
      automaticallyImplyLeading: false,
      actions: [
        IconButton(
            icon: Icon(Icons.clear), onPressed: () => viewModel.cancelTimer()),
      ],
      centerTitle: true,
      title: SABT(
          child: Text(
              Utils.secondsToString(viewModel.remainingTime.round(),
                  spacing: true),
              style: theme.textTheme.headline2!
                  .copyWith(fontSize: 40, color: Colors.white))),
      elevation: 0,
      flexibleSpace: FlexibleSpaceBar(
        background: Padding(
          padding: const EdgeInsets.only(top: kVerticalPadding, bottom: 80),
          child: SafeArea(
            child: TimerSlider(
                initialValue: viewModel.remainingTime,
                maxValue: viewModel.maxTime,
                hasHandle: false,
                showGlow: viewModel.showGlow,
                labelStyle: titleStyle,
                size: 180,
                onUpdateLabel: (value) {
                  return Utils.secondsToString(value.round(), spacing: true);
                }),
          ),
        ),
      ),
      bottom: PreferredSize(
        preferredSize: Size.fromHeight(80),
        child: _buildExtendTimeRow(),
      ),
    );
  }

  Widget _buildBody(final ThemeData theme) {
    return CustomScrollView(
      slivers: [
        _buildAppBar(theme),
        SliverPadding(
          padding: const EdgeInsets.only(bottom: kFloatingActionButtonHeight),
          sliver: SliverList(
            delegate: SliverChildListDelegate.fixed([
              Divider(height: 1),
              SectionHeader(S.of(context).quickLaunchTitle,
                  leftPadding: kHorizontalPadding),
              _buildQuickLaunch(theme),
              SectionHeader(S.of(context).timerStartsActionsTitle,
                  leftPadding: kHorizontalPadding),
              if (viewModel.showHints)
                Stack(
                  children: [
                    if (viewModel.showLongPressHint)
                      _buildHint(
                        title: S.of(context).longPressToAdjustTitle,
                        subtitle: S.of(context).longPressToAdjustDesc,
                        onPressed: viewModel.dismissLongPressHint,
                      ),
                    if (viewModel.showTapHint)
                      _buildHint(
                        title: S.of(context).tapToToggleTitle,
                        subtitle: S.of(context).tapToToggleDesc,
                        onPressed: viewModel.dismissTapHint,
                      ),
                  ],
                ),
              _buildCompactStartedActions(theme),
              // if (!viewModel.isAdFree) NativeAdWidget(),
              SectionHeader(S.of(context).timerEndsActionsTitle,
                  leftPadding: kHorizontalPadding),
              _buildEndedActions(theme),
            ]),
          ),
        ),
      ],
    );
  }

  Card _buildHint(
      {required String title,
      required String subtitle,
      required void Function() onPressed}) {
    return Card(
      margin: EdgeInsets.symmetric(
          horizontal: kHorizontalPadding, vertical: kVerticalPaddingSmall),
      elevation: 0,
      child: ListTile(
        leading: Icon(Icons.info),
        title: Text(title, maxLines: 1),
        subtitle: Text(
          subtitle,
          maxLines: 4,
          overflow: TextOverflow.ellipsis,
        ),
        isThreeLine: true,
        trailing: IconButton(
          alignment: Alignment.topRight,
          padding: EdgeInsets.all(2),
          icon: Icon(Icons.clear),
          onPressed: onPressed,
        ),
      ),
    );
  }

  SizedBox _buildQuickLaunch(final ThemeData theme) {
    final isLight = theme.brightness == Brightness.light;

    return SizedBox(
      height: 56,
      child: ListView(
        padding: const EdgeInsets.symmetric(
            horizontal: kHorizontalPadding, vertical: kVerticalPaddingSmall),
        shrinkWrap: true,
        scrollDirection: Axis.horizontal,
        children: [
          OutlinedButton.icon(
            style: OutlinedButton.styleFrom(
              primary: isLight ? Colors.black87 : Colors.white,
            ),
            icon: Icon(Icons.play_arrow_outlined),
            label: Text(S.of(context).buttonShowPlayerApps),
            onPressed: () => _showAppSheet(viewModel.playerApps),
          ),
          SizedBox(width: 12),
          OutlinedButton.icon(
            style: OutlinedButton.styleFrom(
              primary: isLight ? Colors.black87 : Colors.white,
            ),
            icon: Icon(Icons.alarm),
            label: Text(S.of(context).buttonShowAlarmApps),
            onPressed: () => _showAppSheet(viewModel.alarmApps),
          ),
        ],
      ),
    );
  }

  void _showAppSheet(final Future<List<App>> apps) {
    const appSize = 40.0;
    const gridSize = 3;
    const labelMargin = 12.0;

    showModalBottomSheet(
        context: context,
        builder: (BuildContext context) {
          return FutureBuilder<List<App>>(
              future: apps,
              builder: (context, snapshot) {
                if (snapshot.hasData) {
                  final apps = snapshot.data!;

                  return Column(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      IconButton(icon: Icon(Icons.drag_handle), onPressed: null),
                      Expanded(
                        child: GridView.builder(
                            padding: const EdgeInsets.all(kBottomSheetPadding),
                            shrinkWrap: true,
                            gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                                crossAxisCount: gridSize),
                            itemBuilder: (_, index) {
                              final app = snapshot.data![index];

                              return Column(
                                crossAxisAlignment: CrossAxisAlignment.center,
                                mainAxisSize: MainAxisSize.min,
                                children: [
                                  Ink.image(
                                    width: appSize,
                                    height: appSize,
                                    image: cachedAppIcons?[app.packageName] ??
                                        MemoryImage(base64Decode(app.icon!)),
                                    child: InkWell(
                                      onTap: () =>
                                          viewModel.openPackage(app.packageName!),
                                      customBorder: CircleBorder(),
                                    ),
                                  ),
                                  SizedBox(height: labelMargin),
                                  Text(app.title!,
                                      overflow: TextOverflow.ellipsis, maxLines: 1),
                                ],
                              );
                            },
                            itemCount: apps.length),
                      ),
                    ],
                  );
                } else {
                  return Center(child: CircularProgressIndicator());
                }
              });
        });
  }

  bool _onScrollNotification(ScrollNotification notification) {
    if (notification.depth == 0 && notification is UserScrollNotification) {
      switch (notification.direction) {
        case ScrollDirection.forward:
          if (notification.metrics.maxScrollExtent !=
                  notification.metrics.minScrollExtent ||
              _hideFabAnimController.value == 0.0) {
            _hideFabAnimController.forward();
          }
          break;
        case ScrollDirection.reverse:
          if (notification.metrics.maxScrollExtent !=
              notification.metrics.minScrollExtent) {
            _hideFabAnimController.reverse();
          }
          break;
        case ScrollDirection.idle:
          break;
      }
    }
    return false;
  }

  Row _buildExtendTimeRow() {
    return Row(
        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
        children: [1, 5, 20].map((minutes) {
          return RoundedRectButton(
              title: '+ $minutes',
              onPressed: () => viewModel.onExtendTime(minutes));
        }).toList());
  }

  Widget _buildCompactStartedActions(final ThemeData theme) {
    final size = 36.0;

    return Padding(
      padding: const EdgeInsets.symmetric(vertical: kVerticalPaddingSmall),
      child: Wrap(
        alignment: WrapAlignment.spaceEvenly,
        runSpacing: kVerticalPadding,
        direction: Axis.horizontal,
        children: [
          ToggleButton(
            label: '${viewModel.timerModel.volumeAction.description}',
            activeIcon: Icons.volume_down_outlined,
            disabledIcon: Icons.volume_mute_outlined,
            onChanged: (value) async {
              // setup the volumeLevel if no value exists
              if (value && viewModel.timerModel.volumeAction.value == null) {
                await _showVolumeLevelPicker();
              }
              viewModel.onChangeVolume(value);
            },
            onLongPress: _showVolumeLevelPicker,
            value: viewModel.timerModel.volumeAction.enabled,
            size: size,
          ),
          ToggleButton(
            label: viewModel.timerModel.lightAction.title,
            activeIcon: MdiIcons.lightbulbOffOutline,
            disabledIcon: MdiIcons.lightbulbOutline,
            onChanged: viewModel.onChangeLight,
            onLongPress: viewModel.navigateToLightsGroups,
            value: viewModel.timerModel.lightAction.enabled,
            size: size,
          ),
          ToggleButton(
            label: viewModel.timerModel.doNotDisturbAction.title,
            activeIcon: MdiIcons.doNotDisturb,
            disabledIcon: MdiIcons.doNotDisturbOff,
            onChanged: viewModel.onChangeDoNotDisturb,
            onLongPress: () => viewModel.navigateToSettings(
                notificationSettingsAccessFocused: true),
            value: viewModel.timerModel.doNotDisturbAction.enabled &&
                viewModel.hasNotificationSettingsAccess,
            size: size,
          ),
        ],
      ),
    );
  }

  Widget _buildEndedActions(final ThemeData theme) {
    final size = 36.0;

    return Padding(
      padding: const EdgeInsets.symmetric(vertical: kVerticalPaddingSmall),
      child: Wrap(
        alignment: WrapAlignment.spaceEvenly,
        runSpacing: kVerticalPadding,
        direction: Axis.horizontal,
        children: [
          ToggleButton(
            label: viewModel.timerModel.mediaAction.title,
            activeIcon: Icons.music_off_outlined,
            disabledIcon: Icons.music_note_outlined,
            onChanged: viewModel.onChangeMedia,
            value: viewModel.timerModel.mediaAction.enabled,
            size: size,
          ),
          if (viewModel.platformVersion < 29)
            ToggleButton(
              label: viewModel.timerModel.wifiAction.title,
              activeIcon: Icons.wifi_off_outlined,
              disabledIcon: Icons.wifi_outlined,
              onChanged: viewModel.onChangeWifi,
              value: viewModel.timerModel.wifiAction.enabled,
              size: size,
            ),
          ToggleButton(
            label: viewModel.timerModel.bluetoothAction.title,
            activeIcon: Icons.bluetooth_disabled_outlined,
            disabledIcon: Icons.bluetooth_outlined,
            onChanged: viewModel.onChangeBluetooth,
            value: viewModel.timerModel.bluetoothAction.enabled,
            size: size,
          ),
          ToggleButton(
            label: viewModel.timerModel.screenAction.title,
            activeIcon: Icons.tv_off_outlined,
            disabledIcon: Icons.tv_outlined,
            onChanged: viewModel.onChangeScreen,
            value: viewModel.timerModel.screenAction.enabled &&
                viewModel.isDeviceAdmin,
            size: size,
          ),
        ],
      ),
    );
  }

  Future<void> _showVolumeLevelPicker() async {
    final volumeLevel = await showDialog<double>(
        context: context,
        builder: (context) => FutureBuilder<VolumeResponse>(
            future: viewModel.volume,
            builder: (context, snapshot) {
              if (snapshot.hasData) {
                var currentLevel = snapshot.data?.currentLevel?.toDouble() ?? 3;
                var maxVolume = snapshot.data?.maxSystemIndex?.toDouble() ?? 10;

                return SliderDialog(
                  title: S.of(context).setVolumeTitle,
                  initialValue:
                      viewModel.timerModel.volumeAction.value ?? currentLevel,
                  maxValue: maxVolume,
                  onChangeEnd: (value) =>
                      viewModel.handleVolumeAction(value.round()),
                );
              } else {
                return CircularProgressIndicator();
              }
            }));

    if (volumeLevel != null) {
      await viewModel.onChangeVolumeLevel(volumeLevel);
    }
  }

  Widget _buildFAB(ThemeData theme) {
    final foregroundColor = Colors.white;
    final textStyle =
        theme.accentTextTheme.headline6!.copyWith(color: foregroundColor);

    if (viewModel.timerStatus == TimerStatus.RUNNING) {
      _fabAnimController.reverse();
    } else {
      _fabAnimController.forward();
    }

    return ScaleTransition(
      scale: _hideFabAnimController,
      child: FloatingActionButton.extended(
          backgroundColor: _colorAnimation.value,
          icon: AnimatedIcon(
            icon: AnimatedIcons.pause_play,
            progress: _fabAnimController,
            color: foregroundColor,
          ),
          label: Text(
            viewModel.timerStatus == TimerStatus.RUNNING ||
                    viewModel.timerStatus == TimerStatus.INITIAL
                ? S.of(context).buttonTimerPause
                : viewModel.timerStatus == TimerStatus.ELAPSED
                    ? S.of(context).buttonTimerStart
                    : S.of(context).buttonTimerContinue,
            style: textStyle,
          ),
          onPressed: () {
            if (viewModel.timerStatus == TimerStatus.RUNNING) {
              viewModel.pauseTimer();
            } else {
              viewModel.startTimer();
            }
          }),
    );
  }
}
