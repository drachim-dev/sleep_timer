import 'dart:async';
import 'dart:io';
import 'dart:ui';

import 'package:android_intent/android_intent.dart';
import 'package:device_functions/messages_generated.dart';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter_native_admob/flutter_native_admob.dart';
import 'package:flutter_native_admob/native_admob_controller.dart';
import 'package:flutter_native_admob/native_admob_options.dart';
import 'package:sleep_timer/common/ad_manager.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/common/utils.dart';
import 'package:sleep_timer/model/timer_model.dart';
import 'package:sleep_timer/services/timer_service.dart';
import 'package:sleep_timer/ui/widgets/rounded_rect_button.dart';
import 'package:sleep_timer/ui/widgets/sabt.dart';
import 'package:sleep_timer/ui/widgets/section_header.dart';
import 'package:sleep_timer/ui/widgets/slider_dialog.dart';
import 'package:sleep_timer/ui/widgets/timer_slider.dart';
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

  String _selectedPlaylist = "Playlist 10";

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
    final ThemeData theme = Theme.of(context);

    return ViewModelBuilder<TimerViewModel>.reactive(
        viewModelBuilder: () => TimerViewModel(widget.timerModel),
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
    final TextStyle titleStyle = theme.textTheme.headline2
        .copyWith(shadows: [Shadow(blurRadius: 5.0, color: Colors.white)]);

    return SliverAppBar(
        primary: true,
        pinned: true,
        expandedHeight: 310,
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
            padding: const EdgeInsets.only(top: 72, bottom: 86),
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
        bottom: PreferredSize(
            child: _buildExtendTimeRow(), preferredSize: Size.fromHeight(80)));
  }

  Widget _buildBody(final ThemeData theme) {
    return CustomScrollView(
      slivers: [
        _buildAppBar(theme),
        SliverList(
          delegate: SliverChildListDelegate([
            Divider(height: 1),
            SectionHeader("Quick actions", leftPadding: kHorizontalPadding),
            _buildQuickActions(theme),
            ExpansionTile(
                title: Text("When timer starts"),
                initiallyExpanded: true,
                children: _buildStartedActions(theme)),
            ExpansionTile(
                title: Text("When time is up"),
                initiallyExpanded: true,
                children: _buildEndedActions(theme)),
          ]),
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

  Widget _buildQuickActionsHint(final ThemeData theme) {
    return ListTile(
      title: Text("Tap to add quick actions"),
      subtitle: Text("Add shortcuts to quickly open your players"),
      tileColor: Colors.grey.withOpacity(0.35),
      onTap: () {},
      trailing: FlatButton(
        child: Text("DISMISS"),
        onPressed: () {},
      ),
    );
  }

  Widget _buildQuickActions(final ThemeData theme) {
    const double appIconPadding = 4;

    return Padding(
      padding: const EdgeInsets.all(12.0),
      child: Row(
        children: [
          "assets/media_player/spotify.png",
        ].map((image) {
          return Padding(
            padding: const EdgeInsets.symmetric(horizontal: appIconPadding),
            child: Ink.image(
              width: 40,
              height: 40,
              image: AssetImage(image),
              child: InkWell(
                onTap: _openSpotify,
                customBorder: CircleBorder(),
              ),
            ),
          );
        }).toList(),
      ),
    );
  }

  List<Widget> _buildStartedActions(final ThemeData theme) {
    return [
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
      if (!model.isAdFree) _buildAd(theme),
      if (false)
        ListTile(
          leading: Icon(Icons.music_note_outlined),
          title: Text(model.timerModel.playMusicAction.title),
          subtitle: Text(model.timerModel.playMusicAction.description),
          onTap: () => _showPlaylistPicker(),
          trailing: Switch(
            value: model.timerModel.playMusicAction.enabled,
            onChanged: model.onChangePlayMusic,
          ),
        ),
      SwitchListTile(
        secondary: Icon(Icons.do_not_disturb_on),
        title: Text(model.timerModel.doNotDisturbAction.title),
        subtitle: Text(model.timerModel.doNotDisturbAction.description),
        value: model.timerModel.doNotDisturbAction.enabled &&
            model.notificationSettingsAccess,
        onChanged: model.notificationSettingsAccess
            ? model.onChangeDoNotDisturb
            : (value) => model.navigateToSettings(
                notificationSettingsAccessFocused: true),
      ),
    ];
  }

  List<Widget> _buildEndedActions(final ThemeData theme) {
    return [
      SwitchListTile(
        secondary: Icon(Icons.music_off_outlined),
        title: Text(model.timerModel.mediaAction.title),
        subtitle: Text(model.timerModel.mediaAction.description),
        value: model.timerModel.mediaAction.enabled,
        onChanged: model.onChangeMedia,
      ),
      if (model.platformVersion < 29)
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
      SwitchListTile(
        secondary: Icon(Icons.tv_off_outlined),
        title: Text(model.timerModel.screenAction.title),
        subtitle: Text(model.timerModel.screenAction.description),
        value: model.timerModel.screenAction.enabled && model.deviceAdmin,
        onChanged: model.onChangeScreen,
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

  Container _buildAd(final ThemeData theme) {
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

  void _showPlaylistPicker() async {
    final playlistUri = await showDialog<double>(
        context: context,
        builder: (context) => AlertDialog(
              title: Text("Choose playlist"),
              content: StatefulBuilder(
                  builder: (BuildContext context, StateSetter setState) {
                return Container(
                  width: 400,
                  child: ListView.builder(
                    shrinkWrap: true,
                    itemBuilder: (_, index) {
                      return RadioListTile(
                        title: Text(
                          "RAWER Than The Rest by Gearbox Digital $index",
                          maxLines: 2,
                        ),
                        groupValue: _selectedPlaylist,
                        value: "Playlist $index",
                        onChanged: (value) {
                          setState(() => _selectedPlaylist = value);
                        },
                      );
                    },
                    itemCount: 25,
                  ),
                );
              }),
              actions: [
                FlatButton(
                  onPressed: () => Navigator.pop(context),
                  child: Text("DONE"),
                ),
              ],
            ));
  }

  _buildFAB(ThemeData theme) {
    final Color foregroundColor = Colors.white;
    final TextStyle textStyle =
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
            model.timerStatus == TimerStatus.RUNNING
                ? "Pause"
                : model.timerStatus == TimerStatus.INITIAL
                    ? "Start timer"
                    : "Continue",
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

  void _openSpotify() async {
    final AndroidIntent intent = AndroidIntent(
        action: "action_view",
        package: "com.spotify.music",
        data: "spotify:home");

    await intent.launch();
  }

  void _startPlaylist() async {
    if (Platform.isAndroid) {
      model.navigateToSpotifyAuth();
    }
  }
}
