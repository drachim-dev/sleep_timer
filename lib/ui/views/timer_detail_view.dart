import 'dart:async';
import 'dart:ui';

import 'package:device_functions/messages_generated.dart';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter_native_admob/flutter_native_admob.dart';
import 'package:flutter_native_admob/native_admob_controller.dart';
import 'package:flutter_native_admob/native_admob_options.dart';
import 'package:sleep_timer/common/ad_manager.dart';
import 'package:sleep_timer/common/utils.dart';
import 'package:sleep_timer/model/timer_model.dart';
import 'package:sleep_timer/ui/widgets/rounded_rect_button.dart';
import 'package:sleep_timer/ui/widgets/slider_dialog.dart';
import 'package:sleep_timer/ui/widgets/timer_slider.dart';
import 'package:stacked/stacked.dart';

import 'timer_detail_viewmodel.dart';

class TimerDetailView extends StatefulWidget {
  final TimerModel timerModel;

  const TimerDetailView({Key key, @required this.timerModel});

  @override
  _TimerDetailViewState createState() => _TimerDetailViewState();
}

class _TimerDetailViewState extends State<TimerDetailView>
    with TickerProviderStateMixin {
  final _adController = NativeAdmobController();
  final _scrollController = ScrollController();
  AnimationController _hideFabAnimController;
  AnimationController _fabAnimController;
  Animation<Color> _colorAnimation;

  TimerDetailViewModel model;

  _TimerDetailViewState();

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
          _adHeight = 80;
        });
        break;

      default:
        break;
    }
  }

  void initAnimations() {
    _hideFabAnimController = AnimationController(
        vsync: this, duration: kThemeAnimationDuration, value: 1);
    _fabAnimController =
        AnimationController(vsync: this, duration: kThemeAnimationDuration);

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
    final ThemeData theme = Theme.of(context);

    return ViewModelBuilder<TimerDetailViewModel>.reactive(
        viewModelBuilder: () => TimerDetailViewModel(widget.timerModel),
        onModelReady: (model) => this.model = model,
        builder: (context, model, child) {
          return WillPopScope(
            onWillPop: () async {
              model.navigateBack();
              return false;
            },
            child: NotificationListener(
              onNotification: _onScrollNotification,
              child: Scaffold(
                appBar: _buildAppBar(theme),
                body: _buildBody(theme),
                floatingActionButton: _buildFAB(theme),
                floatingActionButtonLocation:
                    FloatingActionButtonLocation.centerFloat,
              ),
            ),
          );
        });
  }

  AppBar _buildAppBar(final ThemeData theme) {
    const double height = 200;

    return AppBar(
      leading: IconButton(
        icon: Icon(Icons.arrow_back),
        onPressed: () => model.navigateBack(),
      ),
      automaticallyImplyLeading: false,
      bottom: PreferredSize(
        preferredSize: Size(height, height),
        child: TimerSlider(
            initialValue: model.remainingTime,
            maxValue: model.maxTime,
            hasHandle: false,
            labelStyle: theme.textTheme.headline2.copyWith(
              shadows: [Shadow(blurRadius: 5.0, color: Colors.white)],
            ),
            size: height,
            onUpdateLabel: (value) {
              return Utils.secondsToString(value.round(), spacing: true);
            }),
      ),
      actions: [
        IconButton(
          icon: Icon(Icons.clear),
          onPressed: () => model.cancelTimer(),
        ),
      ],
    );
  }

  Widget _buildBody(final ThemeData theme) {
    return Column(
      children: [
        SizedBox(height: 24),
        _buildExtendTimeRow(),
        Expanded(
          child: ListView(
            controller: _scrollController,
            shrinkWrap: true,
            physics: ScrollPhysics(),
            children: [
              ExpansionTile(
                  title: Text("Common actions"),
                  initiallyExpanded: true,
                  children: _buildCommonActions(theme)),
              ExpansionTile(
                  title: Text("More"),
                  initiallyExpanded: model.timerModel.actions.any((action) {
                    return !action.common && action.enabled;
                  }),
                  children: _buildMoreActions()),
            ],
          ),
        ),
      ],
    );
  }

  bool _onScrollNotification(ScrollNotification notification) {
    if (notification.depth == 0 && notification is UserScrollNotification) {
      switch (notification.direction) {
        case ScrollDirection.forward:
          if (notification.metrics.maxScrollExtent !=
              notification.metrics.minScrollExtent)
            _hideFabAnimController.forward();
          break;
        case ScrollDirection.reverse:
          if (notification.metrics.maxScrollExtent !=
              notification.metrics.minScrollExtent)
            _hideFabAnimController.reverse();
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
              title: "+ $minutes",
              onPressed: () => model.onExtendTime(minutes));
        }).toList());
  }

  List<Widget> _buildCommonActions(final ThemeData theme) {
    return [
      SwitchListTile(
        secondary: Icon(Icons.music_off_outlined),
        title: Text(model.timerModel.mediaAction.title),
        subtitle: Text(model.timerModel.mediaAction.description),
        value: model.timerModel.mediaAction.enabled,
        onChanged: model.onChangeMedia,
      ),
      if (!model.isAdFree) _buildAd(theme),
      SwitchListTile(
        secondary: Icon(Icons.wifi_off_outlined),
        title: Text(model.timerModel.wifiAction.title),
        subtitle: Text(model.timerModel.wifiAction.description),
        value: model.timerModel.wifiAction.enabled,
        onChanged: model.onChangeWifi,
      ),
      SwitchListTile(
        secondary: Icon(Icons.bluetooth_disabled_outlined),
        title: Text(model.timerModel.bluetoothAction.title),
        subtitle: Text(model.timerModel.bluetoothAction.description),
        value: model.timerModel.bluetoothAction.enabled,
        onChanged: model.onChangeBluetooth,
      ),
      if (model.notificationSettingsAccess)
        SwitchListTile(
          secondary: Icon(Icons.do_not_disturb_on),
          title: Text(model.timerModel.doNotDisturbAction.title),
          subtitle: Text(model.timerModel.doNotDisturbAction.description),
          value: model.timerModel.doNotDisturbAction.enabled,
          onChanged: model.onChangeDoNotDisturb,
        ),
    ];
  }

  List<Widget> _buildMoreActions() {
    return [
      if (model.deviceAdmin)
        SwitchListTile(
          secondary: Icon(Icons.tv_off_outlined),
          title: Text(model.timerModel.screenAction.title),
          subtitle: Text(model.timerModel.screenAction.description),
          value: model.timerModel.screenAction.enabled,
          onChanged: model.onChangeScreen,
        ),
      ListTile(
        leading: Icon(Icons.volume_down_outlined),
        title: Text(model.timerModel.volumeAction.title),
        subtitle: Text(model.timerModel.volumeAction.description),
        onTap: () => _showVolumeLevelPicker(),
        trailing: Switch(
          value: model.timerModel.volumeAction.enabled,
          onChanged: model.onChangeVolume,
        ),
      ),
      // TODO: Enable connection to philips hue
      if (false)
        SwitchListTile(
          secondary: Icon(Icons.lightbulb_outline),
          title: Text(model.timerModel.lightAction.title),
          subtitle: Text(model.timerModel.lightAction.description),
          value: model.timerModel.lightAction.enabled,
          onChanged: model.onChangeLight,
        ),
      // TODO: Implement kill app
      if (false)
        ListTile(
          leading: Icon(Icons.close_outlined),
          title: Text(model.timerModel.appAction.title),
          subtitle: Text(model.timerModel.appAction.description),
          onTap: null,
          trailing: Switch(
            value: model.timerModel.appAction.enabled,
            onChanged: model.onChangeApp,
          ),
        ),
    ];
  }

  Container _buildAd(ThemeData theme) {
    return Container(
      height: _adHeight,
      padding: EdgeInsets.only(left: 16, right: 32, top: 8, bottom: 8),
      margin: EdgeInsets.only(bottom: 16),
      child: NativeAdmob(
        adUnitID: AdManager.nativeAdUnitId,
        loading: Center(child: CircularProgressIndicator()),
        error: Text("Failed to load the ad"),
        controller: _adController,
        type: NativeAdmobType.banner,
        options: NativeAdmobOptions(
          ratingColor: Colors.red,
          headlineTextStyle:
              NativeTextStyle(color: theme.textTheme.subtitle1.color),
          advertiserTextStyle:
              NativeTextStyle(color: theme.textTheme.caption.color),
          storeTextStyle: NativeTextStyle(color: theme.textTheme.caption.color),
          priceTextStyle: NativeTextStyle(color: theme.textTheme.caption.color),
        ),
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
                    title: "Set volume",
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

  _buildFAB(ThemeData theme) {
    final Color foregroundColor = Colors.white;
    final TextStyle textStyle =
        theme.accentTextTheme.headline6.copyWith(color: foregroundColor);

    if (model.isRunning) {
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
            model.isRunning ? "Pause" : "Continue",
            style: textStyle,
          ),
          onPressed: () {
            if (model.isRunning) {
              model.pauseTimer();
            } else {
              model.startTimer();
            }
          }),
    );
  }
}
