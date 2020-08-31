import 'dart:async';
import 'dart:math';
import 'dart:ui';

import 'package:android_alarm_manager/android_alarm_manager.dart';
import 'package:device_functions/device_functions.dart';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter/services.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/common/utils.dart';
import 'package:sleep_timer/widgets/rounded_rect_button.dart';
import 'package:sleep_timer/widgets/timer_slider.dart';

bool _musicAction = false;
bool _wifiAction = false;
bool _bluetoothAction = false;
bool _screenAction = false;
bool _volumeAction = false;
bool _lightAction = false;
bool _appAction = false;

triggerAlarm() async {
  if (_musicAction) DeviceFunctions.disableMedia();
  if (_wifiAction) DeviceFunctions.disableWifi();
  if (_bluetoothAction) DeviceFunctions.disableBluetooth();
  if (_screenAction) DeviceFunctions.disableScreen();
}

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

class TimerDetailPage extends StatefulWidget {
  final int initialTime;

  const TimerDetailPage({Key key, @required this.initialTime})
      : super(key: key);

  @override
  _TimerDetailPageState createState() =>
      _TimerDetailPageState(this.initialTime);
}

class _TimerDetailPageState extends State<TimerDetailPage>
    with TickerProviderStateMixin {
  ScrollController _scrollController = ScrollController();
  AnimationController _hideFabAnimController;
  AnimationController _fabAnimController;
  Animation<Color> _colorAnimation;

  int _initialTime;
  int _remainingTime;
  Future _timerStartFuture;
  Timer _timer;
  bool _timerIsStarting;

  List<SwitchListTile> _commonActions;
  List<SwitchListTile> _moreActions;

  _TimerDetailPageState(this._initialTime) : _remainingTime = _initialTime;

  @override
  void initState() {
    AndroidAlarmManager.initialize();

    initAnimations();
    initCommonActions();
    initMoreActions();

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

    _timerIsStarting = true;
    _timerStartFuture = Future.delayed(const Duration(milliseconds: 1500), () {
      _startTimer();
    }).then((_) {
      _timerIsStarting = false;
    });
  }

  void initCommonActions() {
    _commonActions = [
      SwitchListTile(
        secondary: Icon(Icons.music_off_outlined),
        title: Text("Media"),
        subtitle: _musicAction ? Text("Stop media playback") : null,
        value: _musicAction,
        onChanged: (value) => setState(() => _musicAction = value),
      ),
      SwitchListTile(
        secondary: Icon(Icons.wifi_off_outlined),
        title: Text("Wifi"),
        subtitle: _wifiAction ? Text("Turn off wifi") : null,
        value: _wifiAction,
        onChanged: (value) => setState(() => _wifiAction = value),
      ),
      SwitchListTile(
        secondary: Icon(Icons.bluetooth_disabled_outlined),
        title: Text("Bluetooth"),
        subtitle: _bluetoothAction ? Text("Turn off bluetooth") : null,
        value: _bluetoothAction,
        onChanged: (value) => setState(() => _bluetoothAction = value),
      ),
    ];
  }

  void initMoreActions() {
    _moreActions = [
      SwitchListTile(
        secondary: Icon(Icons.tv_off_outlined),
        title: Text("Screen"),
        subtitle: _screenAction ? Text("Turn off screen") : null,
        value: _screenAction,
        onChanged: (value) => setState(() => _screenAction = value),
      ),
      SwitchListTile(
        secondary: Icon(Icons.volume_down_outlined),
        title: Text("Volume"),
        subtitle: _volumeAction ? Text("Set media volume to 10") : null,
        value: _volumeAction,
        onChanged: (value) => setState(() => _volumeAction = value),
      ),
      SwitchListTile(
        secondary: Icon(Icons.lightbulb_outlined),
        title: Text("Light"),
        subtitle: _lightAction ? Text("Turn off 3 lights") : null,
        value: _lightAction,
        onChanged: (value) => setState(() => _lightAction = value),
      ),
      SwitchListTile(
        secondary: Icon(Icons.close),
        title: Text("App"),
        subtitle: _appAction ? Text("Force close YouTube") : null,
        value: _appAction,
        onChanged: (value) => setState(() => _appAction = value),
      ),
    ];
  }

  void _startTimer() {
    _fabAnimController.reverse();
    _timer = Timer.periodic(
      const Duration(seconds: 1),
      (timer) {
        if (!mounted) return;
        if (_remainingTime > 0) {
          setState(() => _remainingTime--);
        } else {
          _cancelTimer();
        }
      },
    );

    showNotification(initialTime: _initialTime, remainingTime: _remainingTime);
    AndroidAlarmManager.oneShot(
        Duration(seconds: _remainingTime), kAlarmId, triggerAlarm,
        allowWhileIdle: true, wakeup: true);
  }

  void _cancelTimer() {
    _timer?.cancel();
    AndroidAlarmManager.cancel(kAlarmId);
    cancelNotification();
  }

  void _pauseTimer() {
    _fabAnimController.forward();
    _timer?.cancel();
    AndroidAlarmManager.cancel(kAlarmId);
    pauseNotification(_remainingTime);
  }

  @override
  void dispose() {
    super.dispose();

    _scrollController.dispose();
    _hideFabAnimController.dispose();
    _fabAnimController.dispose();
    _timer?.cancel();
  }

  @override
  Widget build(BuildContext context) {
    final ThemeData theme = Theme.of(context);

    return NotificationListener(
      onNotification: _onScrollNotification,
      child: Scaffold(
        body: _buildyBody(theme),
        floatingActionButton: _buildFAB(theme),
        floatingActionButtonLocation: FloatingActionButtonLocation.centerFloat,
      ),
    );
  }

  Widget _buildyBody(ThemeData theme) {
    _initialTime = max(_initialTime, _remainingTime);

    return Column(
      children: [
        SizedBox(height: 56),
        TimerSlider(
            initialValue: _remainingTime,
            maxValue: _initialTime,
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
                  children: _commonActions),
              ExpansionTile(
                  title: Text("More"),
                  initiallyExpanded: _moreActions.any((item) => item.value),
                  children: _moreActions),
            ],
          ),
        ),
      ],
    );
  }

  Row _buildExtendTimeRow() {
    return Row(
        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
        children: [1, 5, 20].map((minutes) {
          return RoundedRectButton(
              title: "+ $minutes",
              onPressed: () => setState(() => _remainingTime += minutes * 60));
        }).toList());
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
            _timerIsStarting || _timer.isActive ? "Pause" : "Continue",
            style: textStyle,
          ),
          onPressed: () async {
            await _timerStartFuture;
            _timer.isActive ? _pauseTimer() : _startTimer();
          }),
    );
  }
}
