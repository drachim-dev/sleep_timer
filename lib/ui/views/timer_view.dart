import 'dart:async';
import 'dart:convert';
import 'dart:ui';

import 'package:device_functions/messages_generated.dart';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter_native_admob/flutter_native_admob.dart';
import 'package:flutter_native_admob/native_admob_controller.dart';
import 'package:flutter_native_admob/native_admob_options.dart';
import 'package:sleep_timer/common/ad_manager.dart';
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
  final TimerModel timerModel;

  const TimerView({Key key, @required this.timerModel});

  @override
  _TimerViewState createState() => _TimerViewState();
}

class _TimerViewState extends State<TimerView> with TickerProviderStateMixin {
  final _adController = NativeAdmobController();
  final _scrollController = ScrollController();
  AnimationController _hideFabAnimController;
  AnimationController _fabAnimController;
  Animation<Color> _colorAnimation;

  TimerViewModel model;

  _TimerViewState();

  StreamSubscription _adControllerSubscription;
  double _adHeight = 0;

  @override
  void initState() {
    _adController.setTestDeviceIds(AdManager.testDeviceId);
    _adControllerSubscription =
        _adController.stateChanged.listen(_onStateChanged);

    super.initState();

    initAnimations();
  }

  void _onStateChanged(AdLoadState state) {
    switch (state) {
      case AdLoadState.loadCompleted:
        setState(() {
          _adHeight = kAdHeight;
        });
        break;

      default:
        break;
    }
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
    _adControllerSubscription.cancel();
    _adController.dispose();
    _scrollController.dispose();
    _hideFabAnimController.dispose();
    _fabAnimController.dispose();

    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return ViewModelBuilder<TimerViewModel>.reactive(
        viewModelBuilder: () => TimerViewModel(widget.timerModel),
        onModelReady: (model) => this.model = model,
        builder: (context, model, child) {
          return WillPopScope(
            onWillPop: () async {
              await model.navigateBack();
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
    final titleStyle = theme.textTheme.headline2.copyWith(
        fontSize: 46, shadows: [Shadow(blurRadius: 5.0, color: Colors.white)]);

    return SliverAppBar(
        primary: true,
        pinned: true,
        expandedHeight: 286,
        leading: IconButton(
          icon: Icon(Icons.arrow_back),
          onPressed: () => model.navigateBack(),
        ),
        automaticallyImplyLeading: false,
        actions: [
          IconButton(
              icon: Icon(Icons.clear), onPressed: () => model.cancelTimer()),
        ],
        centerTitle: true,
        title: SABT(
            child: Text(
                Utils.secondsToString(model.remainingTime.round(),
                    spacing: true),
                style: theme.textTheme.headline2
                    .copyWith(fontSize: 40, color: Colors.white))),
        elevation: 0,
        flexibleSpace: FlexibleSpaceBar(
          background: Padding(
            padding: const EdgeInsets.only(top: kVerticalPadding, bottom: 80),
            child: SafeArea(
              child: TimerSlider(
                  initialValue: model.remainingTime,
                  maxValue: model.maxTime,
                  hasHandle: false,
                  showGlow: model.showGlow,
                  labelStyle: titleStyle,
                  size: 180,
                  onUpdateLabel: (value) {
                    return Utils.secondsToString(value.round(), spacing: true);
                  }),
            ),
          ),
        ),
        bottom: PreferredSize(
            child: _buildExtendTimeRow(), preferredSize: Size.fromHeight(80)));
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
              _buildQuickLaunch(),
              SectionHeader(S.of(context).timerStartsActionsTitle,
                  leftPadding: kHorizontalPadding),
              if (model.showLongPressHint)
                Dismissible(
                  key: UniqueKey(),
                  child: Card(
                    margin: EdgeInsets.symmetric(
                        horizontal: kHorizontalPadding,
                        vertical: kVerticalPaddingSmall),
                    elevation: 0,
                    child: ListTile(
                      leading: Icon(Icons.info_outline),
                      title: Text(S.of(context).longPressToAdjust),
                      trailing: IconButton(
                          icon: Icon(Icons.delete_sweep_outlined),
                          onPressed: () => model.dismissLongPressHint()),
                    ),
                  ),
                  onDismissed: (direction) => model.dismissLongPressHint(),
                ),
              _buildCompactStartedActions(theme),
              if (!model.isAdFree) _buildAd(theme),
              SectionHeader(S.of(context).timerEndsActionsTitle,
                  leftPadding: kHorizontalPadding),
              _buildEndedActions(theme),
            ]),
          ),
        ),
      ],
    );
  }

  SizedBox _buildQuickLaunch() {
    return SizedBox(
      height: 56,
      child: ListView(
        padding: const EdgeInsets.symmetric(
            horizontal: kHorizontalPadding, vertical: kVerticalPaddingSmall),
        shrinkWrap: true,
        scrollDirection: Axis.horizontal,
        children: [
          OutlineButton.icon(
            icon: Icon(Icons.play_arrow_outlined),
            label: Text(S.of(context).buttonShowPlayerApps),
            shape: StadiumBorder(),
            onPressed: () => _showAppSheet(model.playerApps),
          ),
          SizedBox(width: 12),
          OutlineButton.icon(
            icon: Icon(Icons.alarm),
            label: Text(S.of(context).buttonShowAlarmApps),
            shape: StadiumBorder(),
            onPressed: () => _showAppSheet(model.alarmApps),
          ),
        ],
      ),
    );
  }

  Future _showAppSheet(final Future<List<App>> apps) {
    const appSize = 40.0;
    const gridSize = 3;
    const labelMargin = 12.0;

    return showModalBottomSheet(
        context: context,
        builder: (_) {
          return FutureBuilder<List<App>>(
              future: apps,
              builder: (context, snapshot) {
                if (snapshot.hasData) {
                  final apps = snapshot.data;

                  return GridView.builder(
                      padding: const EdgeInsets.all(kBottomSheetPadding),
                      shrinkWrap: true,
                      physics: NeverScrollableScrollPhysics(),
                      gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                          crossAxisCount: gridSize),
                      itemBuilder: (_, index) {
                        final app = apps[index];

                        return Column(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          children: [
                            Ink.image(
                              width: appSize,
                              height: appSize,
                              image: MemoryImage(base64Decode(app.icon)),
                              child: InkWell(
                                onTap: () => model.openPackage(app.packageName),
                                customBorder: CircleBorder(),
                              ),
                            ),
                            SizedBox(height: labelMargin),
                            Text(app.title,
                                overflow: TextOverflow.ellipsis, maxLines: 1),
                          ],
                        );
                      },
                      itemCount: apps.length);
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
              onPressed: () => model.onExtendTime(minutes));
        }).toList());
  }

  Widget _buildCompactStartedActions(final ThemeData theme) {
    final size = 36.0;

    return Padding(
      padding: const EdgeInsets.all(8.0),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
        children: [
          ToggleButton(
            label: '${model.timerModel.volumeAction.description}',
            activeIcon: Icons.volume_down_outlined,
            disabledIcon: Icons.volume_mute_outlined,
            onChanged: (value) {
              value && model.timerModel.volumeAction.value == null
                  ? _showVolumeLevelPicker()
                  : model.onChangeVolume(value);
            },
            onLongPress: _showVolumeLevelPicker,
            value: model.timerModel.volumeAction.enabled,
            size: size,
          ),
          ToggleButton(
            label: model.timerModel.lightAction.title,
            activeIcon: Icons.flash_off_outlined,
            disabledIcon: Icons.flash_on_outlined,
            onChanged: model.onChangeLight,
            onLongPress: model.navigateToLightsGroups,
            value: model.timerModel.lightAction.enabled,
            size: size,
          ),
          ToggleButton(
            label: model.timerModel.doNotDisturbAction.title,
            activeIcon: Icons.do_not_disturb_on,
            disabledIcon: Icons.do_not_disturb,
            onChanged: model.hasNotificationSettingsAccess
                ? model.onChangeDoNotDisturb
                : (value) => model.navigateToSettings(
                    notificationSettingsAccessFocused: true),
            value: model.timerModel.doNotDisturbAction.enabled &&
                model.hasNotificationSettingsAccess,
            size: size,
          ),
        ],
      ),
    );
  }

  Widget _buildEndedActions(final ThemeData theme) {
    final size = 36.0;

    return Padding(
      padding: const EdgeInsets.all(8.0),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
        children: [
          ToggleButton(
            label: model.timerModel.mediaAction.title,
            activeIcon: Icons.music_off_outlined,
            disabledIcon: Icons.music_note_outlined,
            onChanged: model.onChangeMedia,
            value: model.timerModel.mediaAction.enabled,
            size: size,
          ),
          ToggleButton(
            label: model.timerModel.wifiAction.title,
            activeIcon: Icons.wifi_off_outlined,
            disabledIcon: Icons.wifi_outlined,
            onChanged: model.onChangeWifi,
            value: model.timerModel.wifiAction.enabled,
            size: size,
          ),
          ToggleButton(
            label: model.timerModel.bluetoothAction.title,
            activeIcon: Icons.bluetooth_disabled_outlined,
            disabledIcon: Icons.bluetooth_outlined,
            onChanged: model.onChangeBluetooth,
            value: model.timerModel.bluetoothAction.enabled,
            size: size,
          ),
          ToggleButton(
            label: model.timerModel.screenAction.title,
            activeIcon: Icons.tv_off_outlined,
            disabledIcon: Icons.tv_outlined,
            onChanged: model.onChangeScreen,
            value: model.timerModel.screenAction.enabled && model.isDeviceAdmin,
            size: size,
          ),
        ],
      ),
    );
  }

  Container _buildAd(final ThemeData theme) {
    return Container(
      height: _adHeight,
      padding: EdgeInsets.only(left: 16, right: 32, top: 8, bottom: 8),
      margin: EdgeInsets.only(bottom: 16),
      child: NativeAdmob(
        adUnitID: AdManager.nativeAdUnitId,
        loading: Center(child: CircularProgressIndicator()),
        error: Text(S.of(context).adLoadFailure),
        controller: _adController,
        type: NativeAdmobType.banner,
        options: NativeAdmobOptions(
            showMediaContent: true,
            ratingColor: Colors.red,
            headlineTextStyle:
                NativeTextStyle(color: theme.textTheme.subtitle1.color),
            advertiserTextStyle:
                NativeTextStyle(color: theme.textTheme.caption.color),
            storeTextStyle:
                NativeTextStyle(color: theme.textTheme.caption.color),
            priceTextStyle:
                NativeTextStyle(color: theme.textTheme.caption.color),
            callToActionStyle: NativeTextStyle(
              // color: theme.
              backgroundColor: Colors.grey,
            )),
      ),
    );
  }

  void _showVolumeLevelPicker() async {
    final volumeLevel = await showDialog<double>(
        context: context,
        builder: (context) => FutureBuilder<VolumeResponse>(
            future: model.volume,
            builder: (context, snapshot) {
              if (snapshot.hasData) {
                var currentLevel = snapshot.data?.currentLevel?.toDouble() ?? 3;
                var maxVolume = snapshot.data?.maxSystemIndex?.toDouble() ?? 10;

                return SliderDialog(
                    title: S.of(context).setVolumeTitle,
                    initialValue:
                        model.timerModel.volumeAction.value ?? currentLevel,
                    maxValue: maxVolume);
              } else {
                return CircularProgressIndicator();
              }
            }));

    if (volumeLevel != null) {
      model.onChangeVolumeLevel(volumeLevel);
    }
  }

  Widget _buildFAB(ThemeData theme) {
    final foregroundColor = Colors.white;
    final textStyle =
        theme.accentTextTheme.headline6.copyWith(color: foregroundColor);

    if (model.timerStatus == TimerStatus.RUNNING) {
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
            model.timerStatus == TimerStatus.RUNNING ||
                    model.timerStatus == TimerStatus.INITIAL
                ? S.of(context).buttonTimerPause
                : model.timerStatus == TimerStatus.ELAPSED
                    ? S.of(context).buttonTimerStart
                    : S.of(context).buttonTimerContinue,
            style: textStyle,
          ),
          onPressed: () {
            if (model.timerStatus == TimerStatus.RUNNING) {
              model.pauseTimer();
            } else {
              model.startTimer();
            }
          }),
    );
  }
}
