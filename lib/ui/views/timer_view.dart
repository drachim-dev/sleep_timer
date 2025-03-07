import 'dart:async';
import 'dart:convert';
import 'dart:math';

import 'package:device_functions/messages_generated.dart';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:material_design_icons_flutter/material_design_icons_flutter.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/common/utils.dart';
import 'package:sleep_timer/generated/l10n.dart';
import 'package:sleep_timer/messages_generated.dart';
import 'package:sleep_timer/model/action_model.dart';
import 'package:sleep_timer/model/timer_model.dart';
import 'package:sleep_timer/services/timer_service.dart';
import 'package:sleep_timer/ui/widgets/rounded_rect_button.dart';
import 'package:sleep_timer/ui/widgets/sabt.dart';
import 'package:sleep_timer/ui/widgets/section_header.dart';
import 'package:sleep_timer/ui/widgets/slider_dialog.dart';
import 'package:sleep_timer/ui/widgets/toggle_button.dart';
import 'package:stacked/stacked.dart';

import '../widgets/timer_slider.dart';
import 'timer_viewmodel.dart';

class TimerView extends StatefulWidget {
  final TimerModel? timerModel;

  const TimerView({Key? key, required this.timerModel});

  @override
  State<TimerView> createState() => _TimerViewState();
}

class _TimerViewState extends State<TimerView> with TickerProviderStateMixin {
  final ScrollController _scrollController = ScrollController();
  late AnimationController _hideFabAnimController;
  late AnimationController _fabAnimController;
  late Animation<Color?> _colorAnimation;

  late TimerViewModel viewModel;

  late Future<List<Package>> alarmAppsFuture;
  late Future<List<Package>> playerAppsFuture;

  Map<String, Image> cachedAppIcons = {};

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
        onViewModelReady: (viewModel) {
          this.viewModel = viewModel;

          alarmAppsFuture = viewModel.alarmApps;
          playerAppsFuture = viewModel.playerApps;

          alarmAppsFuture.then((apps) {
            cachedAppIcons.addAll({
              for (var e in apps)
                e.packageName!: Image.memory(
                  base64Decode(e.icon!),
                  width: kAppImageSize,
                  height: kAppImageSize,
                )
            });
          });
          playerAppsFuture.then((apps) {
            cachedAppIcons.addAll({
              for (var e in apps)
                e.packageName!: Image.memory(
                  base64Decode(e.icon!),
                  width: kAppImageSize,
                  height: kAppImageSize,
                )
            });
          });
        },
        builder: (context, viewModel, _) {
          return PopScope(
            canPop: false,
            onPopInvokedWithResult: (bool didPop, _) {
              if (didPop) return;
              viewModel.navigateBack();
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
    final titleStyle = theme.textTheme.displayMedium!.copyWith(
        fontSize: 46, shadows: [Shadow(blurRadius: 5.0, color: Colors.white)]);

    return SliverAppBar(
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
              Utils.secondsToString(viewModel.remainingTime, spacing: true),
              style: theme.textTheme.displayMedium!
                  .copyWith(fontSize: 40, color: Colors.white))),
      elevation: 0,
      flexibleSpace: FlexibleSpaceBar(
        background: Padding(
          padding: const EdgeInsets.only(top: kVerticalPadding, bottom: 80),
          child: SafeArea(
            child: TimerSlider(
              initialValue: viewModel.remainingTime,
              maxValue: max(viewModel.maxTime, viewModel.initialTime),
              hasHandle: false,
              showGlow: viewModel.showGlow,
              labelStyle: titleStyle,
              size: 180,
              child: (_, __) => _buildTimeLabel(),
            ),
          ),
        ),
      ),
      bottom: PreferredSize(
        preferredSize: Size.fromHeight(80),
        child: _buildExtendTimeRow(),
      ),
    );
  }

  Widget _buildTimeLabel() {
    final ThemeData theme = Theme.of(context);
    final TextTheme textTheme = theme.textTheme;

    final formattedSeconds =
        Utils.secondsToString(viewModel.remainingTime, spacing: true);

    return Center(
      child: Text(
        formattedSeconds,
        style: textTheme.headlineLarge,
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
              if (viewModel.showStartActionHints)
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
              _buildStartActions(theme),
              // if (!viewModel.isAdFree) NativeAdWidget(),
              SectionHeader(S.of(context).timerEndsActionsTitle,
                  leftPadding: kHorizontalPadding),
              if (viewModel.showEndActionHints)
                Stack(
                  children: [
                    if (viewModel.showBluetoothNotSupportedHint)
                      _buildHint(
                        title: S.of(context).bluetoothToggleNotSupportedTitle,
                        subtitle:
                            S.of(context).bluetoothNotSupportedExplanation,
                        onPressed: viewModel.dismissBluetoothHint,
                      ),
                  ],
                ),
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
      color: Theme.of(context).cardColor,
      margin: EdgeInsets.symmetric(
        horizontal: kHorizontalPadding,
        vertical: kVerticalPaddingSmall,
      ),
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
          padding: EdgeInsets.zero,
          icon: Icon(Icons.clear),
          onPressed: onPressed,
        ),
      ),
    );
  }

  SizedBox _buildQuickLaunch(final ThemeData theme) {
    return SizedBox(
      height: 56,
      child: ListView(
        padding: const EdgeInsets.symmetric(
            horizontal: kHorizontalPadding, vertical: kVerticalPaddingSmall),
        shrinkWrap: true,
        scrollDirection: Axis.horizontal,
        children: [
          OutlinedButton.icon(
            icon: Icon(Icons.play_arrow_outlined),
            label: Text(S.of(context).buttonShowPlayerApps),
            onPressed: () => _showAppSheet(theme, viewModel.playerApps),
          ),
          SizedBox(width: 12),
          OutlinedButton.icon(
            icon: Icon(Icons.alarm),
            label: Text(S.of(context).buttonShowAlarmApps),
            onPressed: () => _showAppSheet(theme, viewModel.alarmApps),
          ),
        ],
      ),
    );
  }

  void _showAppSheet(final ThemeData theme, final Future<List<Package>> apps) {
    const gridSize = 3;
    const labelMargin = 12.0;

    final labelStyle = theme.textTheme.bodyMedium;

    showModalBottomSheet(
        context: context,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.vertical(
            top: Radius.circular(16),
          ),
        ),
        isScrollControlled: true,
        builder: (context) => DraggableScrollableSheet(
            expand: false,
            builder: (context, scrollController) {
              return FutureBuilder<List<Package>>(
                  future: apps,
                  builder: (context, snapshot) {
                    if (snapshot.hasData) {
                      final apps = snapshot.data!;

                      return Column(
                        children: [
                          Container(
                            height: 5,
                            width: 38,
                            margin: const EdgeInsets.symmetric(vertical: 16),
                            decoration: BoxDecoration(
                                borderRadius: BorderRadius.circular(16),
                                color: Colors.grey),
                          ),
                          Expanded(
                            child: GridView.builder(
                                padding:
                                    const EdgeInsets.all(kBottomSheetPadding),
                                controller: scrollController,
                                gridDelegate:
                                    SliverGridDelegateWithFixedCrossAxisCount(
                                        crossAxisCount: gridSize),
                                itemBuilder: (_, index) {
                                  final app = snapshot.data![index];

                                  return TextButton(
                                    onPressed: () =>
                                        viewModel.openPackage(app.packageName!),
                                    child: Column(
                                      mainAxisSize: MainAxisSize.min,
                                      crossAxisAlignment:
                                          CrossAxisAlignment.center,
                                      children: [
                                        cachedAppIcons[app.packageName] ??
                                            Image.memory(
                                              base64Decode(app.icon!),
                                              width: kAppImageSize,
                                              height: kAppImageSize,
                                            ),
                                        SizedBox(height: labelMargin),
                                        Text(app.title!,
                                            overflow: TextOverflow.ellipsis,
                                            textAlign: TextAlign.center,
                                            style: labelStyle,
                                            maxLines: 1),
                                      ],
                                    ),
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
            }));
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

  Widget _buildStartActions(final ThemeData theme) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: kVerticalPaddingSmall),
      child: Wrap(
        alignment: WrapAlignment.spaceEvenly,
        runSpacing: kVerticalPadding,
        direction: Axis.horizontal,
        children: [
          _buildVolumeToggle(
            actionModel: viewModel.timerModel.volumeStartAction,
            onToggle: viewModel.onChangeVolumeStartAction,
            onChangeSliderLevel: (value) {
              viewModel.handleSetVolumeAction(value.round());
            },
          ),
          ToggleButton(
            label: viewModel.timerModel.lightStartAction.title,
            activeIcon: MdiIcons.lightbulbOffOutline,
            disabledIcon: MdiIcons.lightbulbOutline,
            onChanged: viewModel.onChangeLight,
            onLongPress: viewModel.navigateToLightsGroups,
            value: viewModel.timerModel.lightStartAction.enabled,
          ),
          ToggleButton(
            label: viewModel.timerModel.doNotDisturbStartAction.title,
            activeIcon: MdiIcons.minusCircleOutline,
            disabledIcon: MdiIcons.minusCircleOffOutline,
            onChanged: viewModel.onChangeDoNotDisturb,
            onLongPress: () => viewModel.navigateToSettings(
                notificationSettingsAccessFocused: true),
            value: viewModel.timerModel.doNotDisturbStartAction.enabled &&
                viewModel.hasNotificationSettingsAccess,
          ),
        ],
      ),
    );
  }

  ToggleButton _buildVolumeToggle(
      {required ValueActionModel actionModel,
      required void Function(bool value) onToggle,
      void Function(double)? onChangeSliderLevel}) {
    return ToggleButton(
      label: actionModel.label,
      activeIcon: Icons.volume_down_outlined,
      disabledIcon: Icons.volume_mute_outlined,
      onChanged: (value) async {
        // setup the volumeLevel if no value exists
        if (value && actionModel.value == null) {
          await _showVolumeLevelPicker(
            actionModel: actionModel,
            onChangeEnd: onChangeSliderLevel,
          );
        }
        onToggle(value);
      },
      onLongPress: () => _showVolumeLevelPicker(
        actionModel: actionModel,
        onChangeEnd: onChangeSliderLevel,
      ),
      value: actionModel.enabled,
    );
  }

  Future<Map<Permission, PermissionStatus>> getPermissionStatus() async {
    final permissions = viewModel.timerModel.endActions
        .map((e) => e.permission)
        .whereType<Permission>()
        .toList();

    final Map<Permission, PermissionStatus> permissionStatus = {};
    await Future.forEach(permissions, (Permission permission) async {
      final status = await permission.status;
      permissionStatus[permission] = status;
    });

    return permissionStatus;
  }

  Widget _buildEndedActions(final ThemeData theme) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: kVerticalPaddingSmall),
      child: FutureBuilder<Map<Permission, PermissionStatus>>(
          future: getPermissionStatus(),
          builder: (context, snapshot) {
            final permissionStatus = snapshot.data;

            if (snapshot.hasData && permissionStatus != null) {
              final bluetoothPermission =
                  viewModel.timerModel.bluetoothAction.permission;
              final bluetoothPermissionStatus =
                  permissionStatus[bluetoothPermission];

              return Wrap(
                alignment: WrapAlignment.spaceEvenly,
                runSpacing: kVerticalPadding,
                direction: Axis.horizontal,
                children: [
                  ToggleButton(
                      label: viewModel.timerModel.mediaAction.title,
                      activeIcon: Icons.music_off_outlined,
                      disabledIcon: Icons.music_note_outlined,
                      onChanged: viewModel.onChangeMedia,
                      value: viewModel.timerModel.mediaAction.enabled),
                  _buildVolumeToggle(
                      actionModel: viewModel.timerModel.volumeAction,
                      onToggle: viewModel.onChangeVolumeEndAction),
                  if (viewModel.wifiSupported)
                    ToggleButton(
                        label: viewModel.timerModel.wifiAction.title,
                        activeIcon: Icons.wifi_off_outlined,
                        disabledIcon: Icons.wifi_outlined,
                        onChanged: viewModel.onChangeWifi,
                        value: viewModel.timerModel.wifiAction.enabled),
                  if (viewModel.bluetoothSupported)
                    _buildBluetoothToggleButton(
                        bluetoothPermission, bluetoothPermissionStatus),
                  ToggleButton(
                      label: viewModel.timerModel.screenAction.title,
                      activeIcon: Icons.tv_off_outlined,
                      disabledIcon: Icons.tv_outlined,
                      onChanged: viewModel.onChangeScreen,
                      value: viewModel.timerModel.screenAction.enabled &&
                          viewModel.isDeviceAdmin),
                ],
              );
            } else {
              return Center(
                child: CircularProgressIndicator(),
              );
            }
          }),
    );
  }

  ToggleButton _buildBluetoothToggleButton(
      Permission? permission, PermissionStatus? permissionStatus) {
    final needsAttention = permissionStatus?.isGranted == false ||
        permissionStatus?.isPermanentlyDenied == true;

    // Disable action in prefs if it needs attention
    if (needsAttention) {
      viewModel.onChangeBluetooth(false, silent: true);
    }

    return ToggleButton(
        label: viewModel.timerModel.bluetoothAction.title,
        activeIcon: needsAttention
            ? Icons.warning_amber_rounded
            : Icons.bluetooth_disabled_outlined,
        disabledIcon: needsAttention
            ? Icons.warning_amber_rounded
            : Icons.bluetooth_outlined,
        onChanged: (enable) async {
          final permissionStatus = await permission?.request();

          if (permissionStatus == null || permissionStatus.isGranted) {
            viewModel.onChangeBluetooth(enable);
          } else if (permissionStatus.isPermanentlyDenied == true) {
            await openAppSettings();
          }
        },
        value: viewModel.timerModel.bluetoothAction.enabled);
  }

  Future<void> _showVolumeLevelPicker(
      {required ValueActionModel actionModel,
      void Function(double)? onChangeEnd}) async {
    final volumeLevel = await showDialog<double>(
        context: context,
        builder: (context) => FutureBuilder<VolumeResponse>(
            future: viewModel.volume,
            builder: (context, snapshot) {
              if (snapshot.hasData) {
                final currentLevel =
                    snapshot.data?.currentLevel?.toDouble() ?? 3;
                final maxVolume =
                    snapshot.data?.maxSystemIndex?.toDouble() ?? 10;

                return SliderDialog(
                  title: S.of(context).setVolumeTitle,
                  initialValue: actionModel.value ?? currentLevel,
                  maxValue: maxVolume,
                  onChangeEnd: onChangeEnd,
                );
              } else {
                return CircularProgressIndicator();
              }
            }));

    if (volumeLevel != null) {
      await viewModel.onChangeVolumeLevel(actionModel, volumeLevel);
    }
  }

  Widget _buildFAB(ThemeData theme) {
    final foregroundColor = Colors.white;
    final textStyle =
        theme.textTheme.titleLarge!.copyWith(color: foregroundColor);

    if (viewModel.timerStatus == TimerStatus.running) {
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
            viewModel.timerStatus == TimerStatus.running ||
                    viewModel.timerStatus == TimerStatus.initial
                ? S.of(context).buttonTimerPause
                : viewModel.timerStatus == TimerStatus.elapsed
                    ? S.of(context).buttonTimerStart
                    : S.of(context).buttonTimerContinue,
            style: textStyle,
          ),
          onPressed: () {
            if (viewModel.timerStatus == TimerStatus.running) {
              viewModel.pauseTimer();
            } else {
              viewModel.startTimer();
            }
          }),
    );
  }
}
