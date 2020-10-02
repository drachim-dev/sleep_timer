import 'dart:ui';

import 'package:firebase_admob/firebase_admob.dart';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter_native_admob/flutter_native_admob.dart';
import 'package:flutter_native_admob/native_admob_controller.dart';
import 'package:flutter_native_admob/native_admob_options.dart';
import 'package:sleep_timer/common/ad_manager.dart';
import 'package:sleep_timer/common/utils.dart';
import 'package:sleep_timer/model/timer_model.dart';
import 'package:sleep_timer/ui/widgets/rounded_rect_button.dart';
import 'package:sleep_timer/ui/widgets/timer_slider.dart';
import 'package:stacked/stacked.dart';

import 'timer_detail_viewmodel.dart';

class TimerDetailView extends StatefulWidget {
  final TimerModel timer;

  const TimerDetailView({Key key, @required this.timer});

  @override
  _TimerDetailViewState createState() => _TimerDetailViewState();
}

class _TimerDetailViewState extends State<TimerDetailView>
    with TickerProviderStateMixin {
  final targetingInfo = MobileAdTargetingInfo(
      testDevices: AdManager.testDeviceId != null
          ? <String>[AdManager.testDeviceId]
          : null);

  final _controller = NativeAdmobController();

  final _scrollController = ScrollController();
  AnimationController _hideFabAnimController;
  AnimationController _fabAnimController;
  Animation<Color> _colorAnimation;

  TimerDetailViewModel model;

  _TimerDetailViewState();

  @override
  void initState() {
    super.initState();

    initAnimations();
    FirebaseAdMob.instance.initialize(appId: AdManager.appId);
    _controller.setTestDeviceIds([AdManager.testDeviceId]);
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
    _scrollController.dispose();
    _hideFabAnimController.dispose();
    _fabAnimController.dispose();

    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final ThemeData theme = Theme.of(context);

    return ViewModelBuilder<TimerDetailViewModel>.reactive(
        viewModelBuilder: () => TimerDetailViewModel(widget.timer),
        onModelReady: (model) {
          this.model = model;
        },
        builder: (context, model, child) {
          return NotificationListener(
            onNotification: _onScrollNotification,
            child: Scaffold(
              body: _buildyBody(theme),
              floatingActionButton: _buildFAB(theme),
              floatingActionButtonLocation:
                  FloatingActionButtonLocation.centerFloat,
            ),
          );
        });
  }

  Widget _buildyBody(final ThemeData theme) {
    return Column(
      children: [
        SizedBox(height: 56),
        TimerSlider(
            initialValue: model.remainingTime,
            maxValue: model.maxTime,
            hasHandle: false,
            labelStyle: theme.textTheme.headline2.copyWith(
              shadows: [Shadow(blurRadius: 5.0, color: Colors.white)],
            ),
            size: 200,
            onUpdateLabel: (value) {
              return Utils.secondsToString(value.round(), spacing: true);
            }),
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
                    return !action.common && action.value;
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
        secondary: Icon(Icons.music_note),
        title: Text(model.timerModel.mediaAction.title),
        subtitle: Text(model.timerModel.mediaAction.description),
        value: model.timerModel.mediaAction.value,
        onChanged: model.onChangeMedia,
      ),
      _buildAd(theme),
      SwitchListTile(
        secondary: Icon(Icons.wifi),
        title: Text(model.timerModel.wifiAction.title),
        subtitle: Text(model.timerModel.wifiAction.description),
        value: model.timerModel.wifiAction.value,
        onChanged: model.onChangeWifi,
      ),
      SwitchListTile(
          secondary: Icon(Icons.bluetooth),
          title: Text(model.timerModel.bluetoothAction.title),
          subtitle: Text(model.timerModel.bluetoothAction.description),
          value: model.timerModel.bluetoothAction.value,
          onChanged: model.onChangeBluetooth),
    ];
  }

  Container _buildAd(ThemeData theme) {
    return Container(
      height: 80,
      padding: EdgeInsets.only(left: 16, right: 32, top: 8, bottom: 8),
      margin: EdgeInsets.only(bottom: 16),
      child: NativeAdmob(
        adUnitID: AdManager.nativeAdUnitId,
        loading: Center(child: CircularProgressIndicator()),
        error: Text("Failed to load the ad"),
        controller: _controller,
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

  List<SwitchListTile> _buildMoreActions() {
    return [
      SwitchListTile(
        secondary: Icon(Icons.tv),
        title: Text(model.timerModel.screenAction.title),
        subtitle: Text(model.timerModel.screenAction.description),
        value: model.timerModel.screenAction.value,
        onChanged: model.onChangeScreen,
      ),
      SwitchListTile(
          secondary: Icon(Icons.volume_down),
          title: Text(model.timerModel.volumeAction.title),
          subtitle: Text(model.timerModel.volumeAction.description),
          value: model.timerModel.volumeAction.value,
          onChanged: model.onChangeVolume),
      SwitchListTile(
        secondary: Icon(Icons.lightbulb_outline),
        title: Text(model.timerModel.lightAction.title),
        subtitle: Text(model.timerModel.lightAction.description),
        value: model.timerModel.lightAction.value,
        onChanged: model.onChangeLight,
      ),
      SwitchListTile(
        secondary: Icon(Icons.close),
        title: Text(model.timerModel.appAction.title),
        subtitle: Text(model.timerModel.appAction.description),
        value: model.timerModel.appAction.value,
        onChanged: model.onChangeApp,
      ),
    ];
  }

  _buildFAB(ThemeData theme) {
    final Color foregroundColor = Colors.white;
    final TextStyle textStyle =
        theme.accentTextTheme.headline6.copyWith(color: foregroundColor);

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
            model.isStarting || model.isActive ? "Pause" : "Continue",
            style: textStyle,
          ),
          onPressed: () {
            if (model.isActive) {
              _fabAnimController.forward();
              model.pauseTimer();
            } else {
              _fabAnimController.reverse();
              model.startTimer();
            }
          }),
    );
  }
}
