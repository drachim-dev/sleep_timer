import 'dart:async';
import 'dart:ui';

import 'package:android_alarm_manager/android_alarm_manager.dart';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter/services.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/model/action_model.dart';
import 'package:sleep_timer/common/utils.dart';
import 'package:sleep_timer/model/timer_model.dart';
import 'package:sleep_timer/ui/widgets/rounded_rect_button.dart';
import 'package:sleep_timer/ui/widgets/timer_slider.dart';
import 'package:stacked/stacked.dart';

import 'timer_detail_viewmodel.dart';

Future<bool> showNotification(
    {@required int initialTime, @required int remainingTime}) async {
  try {
    bool success = await kMethodChannel.invokeMethod("showNotification", {
      "id": kNotificationId,
      "title": "Sleep timer running",
      "subtitle": "Timer set for ${Utils.secondsToString(initialTime)} minutes",
      "seconds": remainingTime,
      "actionTitle1": "+5",
      "actionTitle2": "+20",
    });

    return success;
  } on PlatformException catch (e) {
    print(e.message);
    return false;
  }
}

Future<bool> pauseNotification(int remainingTime) async {
  try {
    bool success = await kMethodChannel.invokeMethod("pauseNotification", {
      "id": kNotificationId,
      "title": "Sleep timer pausing",
      "subtitle": "Time left: ${Utils.secondsToString(remainingTime)}",
      "seconds": remainingTime,
      "actionTitle1": "Continue",
    });
    return success;
  } on PlatformException catch (e) {
    print(e.message);
    return false;
  }
}

Future<bool> cancelNotification() async {
  try {
    bool success = await kMethodChannel.invokeMethod("cancelNotification", {
      "id": kNotificationId,
    });
    return success;
  } on PlatformException catch (e) {
    print(e.message);
    return false;
  }
}

class TimerDetailView extends StatefulWidget {
  final TimerModel timer;

  const TimerDetailView({Key key, @required this.timer});

  @override
  _TimerDetailViewState createState() => _TimerDetailViewState();
}

class _TimerDetailViewState extends State<TimerDetailView>
    with TickerProviderStateMixin {
  ScrollController _scrollController = ScrollController();
  AnimationController _hideFabAnimController;
  AnimationController _fabAnimController;
  Animation<Color> _colorAnimation;

  TimerDetailViewModel model;

  _TimerDetailViewState();

  @override
  void initState() {
    AndroidAlarmManager.initialize();

    initAnimations();

    super.initState();
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

  void _startTimer() {
    _fabAnimController.reverse();

    model.startTimer();

    showNotification(
        initialTime: model.initialTime, remainingTime: model.remainingTime);
  }

  void _cancelTimer() {
    model.stopTimer();
    AndroidAlarmManager.cancel(kAlarmId);
    cancelNotification();
  }

  void _pauseTimer() {
    _fabAnimController.forward();
    model.stopTimer();
    AndroidAlarmManager.cancel(kAlarmId);
    pauseNotification(model.remainingTime);
  }

  @override
  void dispose() {
    super.dispose();

    _scrollController.dispose();
    _hideFabAnimController.dispose();
    _fabAnimController.dispose();
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

  Widget _buildyBody(ThemeData theme) {
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
                  children: _buildCommonActions()),
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

  List<SwitchListTile> _buildCommonActions() {
    return [
      SwitchListTile(
        secondary: Icon(Icons.music_off_outlined),
        title: Text(model.timerModel.musicAction.title),
        subtitle: Text(model.timerModel.musicAction.description),
        value: model.timerModel.musicAction.value,
        onChanged: (value) => model.onChangeMusic,
      ),
      SwitchListTile(
        secondary: Icon(Icons.wifi_off_outlined),
        title: Text(model.timerModel.wifiAction.title),
        subtitle: Text(model.timerModel.wifiAction.description),
        value: model.timerModel.wifiAction.value,
        onChanged: (value) => model.onChangeWifi,
      ),
      SwitchListTile(
          secondary: Icon(Icons.bluetooth_disabled_outlined),
          title: Text(model.timerModel.bluetoothAction.title),
          subtitle: Text(model.timerModel.bluetoothAction.description),
          value: model.timerModel.bluetoothAction.value,
          onChanged: (value) => model.onChangeBluetooth),
    ];
  }

  List<SwitchListTile> _buildMoreActions() {
    return [
      SwitchListTile(
        secondary: Icon(Icons.tv_off_outlined),
        title: Text(model.timerModel.screenAction.title),
        subtitle: Text(model.timerModel.screenAction.description),
        value: model.timerModel.screenAction.value,
        onChanged: (value) => model.onChangeScreen,
      ),
      SwitchListTile(
          secondary: Icon(Icons.volume_down_outlined),
          title: Text(model.timerModel.volumeAction.title),
          subtitle: Text(model.timerModel.volumeAction.description),
          value: model.timerModel.volumeAction.value,
          onChanged: (value) => model.onChangeVolume),
      SwitchListTile(
        secondary: Icon(Icons.lightbulb_outlined),
        title: Text(model.timerModel.lightAction.title),
        subtitle: Text(model.timerModel.lightAction.description),
        value: model.timerModel.lightAction.value,
        onChanged: (value) => model.onChangeLight,
      ),
      SwitchListTile(
        secondary: Icon(Icons.close),
        title: Text(model.timerModel.appAction.title),
        subtitle: Text(model.timerModel.appAction.description),
        value: model.timerModel.appAction.value,
        onChanged: (value) => model.onChangeApp,
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
            model.isActive ? _pauseTimer() : _startTimer();
          }),
    );
  }
}
