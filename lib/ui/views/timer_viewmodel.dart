import 'dart:async';
import 'dart:convert';

import 'package:device_functions/messages_generated.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:sleep_timer/app/auto_router.gr.dart';
import 'package:sleep_timer/app/locator.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/common/spotify_manager.dart';
import 'package:sleep_timer/common/timer_service_manager.dart';
import 'package:sleep_timer/model/action_model.dart';
import 'package:sleep_timer/model/playlist.dart' as MyPlaylist;
import 'package:sleep_timer/model/spotify_authentication.dart';
import 'package:sleep_timer/model/timer_model.dart';
import 'package:sleep_timer/services/device_service.dart';
import 'package:sleep_timer/services/purchase_service.dart';
import 'package:sleep_timer/services/theme_service.dart';
import 'package:sleep_timer/services/timer_service.dart';
import 'package:spotify/spotify.dart';
import 'package:spotify_sdk/spotify_sdk.dart';
import 'package:stacked/stacked.dart';
import 'package:stacked_services/stacked_services.dart';

class TimerViewModel extends ReactiveViewModel implements Initialisable {
  final TimerService _timerService;
  final TimerModel _timerModel;
  final _navigationService = locator<NavigationService>();

  final _prefsService = locator<SharedPreferences>();
  final _themeService = locator<ThemeService>();
  final _purchaseService = locator<PurchaseService>();
  final _deviceService = locator<DeviceService>();

  bool _newInstance;

  bool get deviceAdmin => _deviceService.deviceAdmin ?? false;
  bool get notificationSettingsAccess =>
      _deviceService.notificationSettingsAccess ?? false;

  TimerViewModel(this._timerModel)
      : _timerService =
            TimerServiceManager.getInstance().getTimerService(_timerModel.id) ??
                locator<TimerService>(param1: _timerModel) {
    _newInstance =
        TimerServiceManager.getInstance().getTimerService(timerModel.id) ==
            null;
    if (_newInstance) {
      TimerServiceManager.getInstance().setTimerService(_timerService);
    }

    // Check for adFree in-app purchase
    _isAdFree = _purchaseService.products.firstWhere(
            (element) =>
                element.productDetails.id == kProductRemoveAds &&
                element.purchased,
            orElse: () => null) !=
        null;
  }

  TimerModel get timerModel => _timerModel;
  int get initialTime => _timerModel.initialTimeInSeconds;
  int get remainingTime => _timerService.remainingTime;
  int get maxTime => _timerService.maxTime;

  TimerStatus get timerStatus => _timerService.status;

  bool _isAdFree = false;
  bool get isAdFree => _isAdFree;

  Future<VolumeResponse> get volume => _deviceService.volume;
  int get platformVersion => _deviceService.platformVersion;

  bool get showGlow => _themeService.showGlow;

  @override
  Future<void> initialise() async {
    await _deviceService.init();
    if (_newInstance) {
      await initActionPreferences();
    }
  }

  Future<void> initActionPreferences() async {
    _prefsService.setBool(
        ActionType.VOLUME.toString(), _timerModel.volumeAction.enabled);
    _prefsService.setDouble(
        _timerModel.volumeAction.key, _timerModel.volumeAction.value);
    _prefsService.setBool(
        ActionType.PLAY_MUSIC.toString(), _timerModel.playMusicAction.enabled);
    _prefsService.setBool(
        ActionType.DND.toString(), _timerModel.doNotDisturbAction.enabled);

    _prefsService.setBool(
        ActionType.MEDIA.toString(), _timerModel.mediaAction.enabled);
    _prefsService.setBool(
        ActionType.WIFI.toString(), _timerModel.wifiAction.enabled);
    _prefsService.setBool(
        ActionType.BLUETOOTH.toString(), _timerModel.bluetoothAction.enabled);
    _prefsService.setBool(
        ActionType.SCREEN.toString(), _timerModel.screenAction.enabled);

    _prefsService.setBool(
        ActionType.LIGHT.toString(), _timerModel.lightAction.enabled);
    _prefsService.setBool(
        ActionType.APP.toString(), _timerModel.appAction.enabled);
  }

  Future<void> startTimer(
      {final Duration delay = const Duration(seconds: 0)}) async {
    await runBusyFuture(Future.delayed(delay, () {
      _timerService.start();
    }));
  }

  void pauseTimer() => _timerService.pauseTimer();
  void cancelTimer() async {
    _navigationService.back();
    await runBusyFuture(_timerService.cancelTimer());
  }

  Future<void> navigateBack() async {
    _navigationService.back(result: timerModel.id);
  }

  Future<void> navigateToSettings(
      {final bool deviceAdminFocused,
      final bool notificationSettingsAccessFocused}) async {
    _navigationService.navigateTo(Routes.settingsView,
        arguments: SettingsViewArguments(
            deviceAdminFocused: deviceAdminFocused,
            notificationSettingsAccessFocused:
                notificationSettingsAccessFocused));
  }

  Future<void> navigateToSpotifyAuth() async {
    final String clientId = SpotifyManager.clientId;

    // The URI to redirect to after the user grants or denies permission. It must
    // be in your Spotify application Redirect whitelist. This URI can be a fabricated
    // URI that allows the client device to function as an authorization server.
    final redirectUri = 'https://dr.achim.sleep_timer/auth';

    // See https://developer.spotify.com/documentation/general/guides/scopes/
    // for a complete list of these Spotify authorization permissions. If no
    // scopes are specified, only public Spotify information will be available.
    final scopes = [
      'playlist-read-collaborative',
      'playlist-read-private',
      'user-modify-playback-state'
    ];

    SpotifyApiCredentials authCredentials;
    SpotifyApi spotify;

    // load credentials from SharedPreferences
    // final jsonString = _prefsService.getString(kSpotifyCredentials);
    final jsonString = null;
    if (jsonString != null) {
      print("saved authentication loaded");
      authCredentials = SpotifyAuthentication.fromJson(jsonDecode(jsonString))
          .toCredentials();
      spotify = SpotifyApi(authCredentials);
    } else {
      // start new authentication flow
      print("new authentication");

      final String clientSecret = SpotifyManager.clientSecret;
      final credentials = SpotifyApiCredentials(clientId, clientSecret);
      final grant = SpotifyApi.authorizationCodeGrant(credentials);

      final authUri = grant.getAuthorizationUrl(
        Uri.parse(redirectUri),
        scopes: scopes, // scopes are optional
      );

      final String authTokenUrl = await _navigationService.navigateTo(
          Routes.spotifyAuthView,
          arguments: SpotifyAuthViewArguments(
              url: authUri.toString(), redirectUrl: redirectUri));

      spotify = SpotifyApi.fromAuthCodeGrant(grant, authTokenUrl);
      authCredentials = await spotify.getCredentials();

      // save authentication as own model
      final SpotifyAuthentication authentication =
          SpotifyAuthentication.fromCredentials(authCredentials);
      _prefsService.setString(
          kSpotifyCredentials, jsonEncode(authentication.toJson()));
    }

    // get playlists
    final playlists = await spotify.playlists.me.all();
    List<MyPlaylist.Playlist> myPlaylists = playlists.map((e) {
      return MyPlaylist.Playlist(id: e.uri, name: e.name);
    }).toList();

    final token = await SpotifySdk.getAuthenticationToken(
        clientId: clientId, redirectUrl: redirectUri, scope: scopes.join(", "));

    final success = await SpotifySdk.connectToSpotifyRemote(
        clientId: clientId, redirectUrl: redirectUri, accessToken: token);

    SpotifySdk.play(spotifyUri: playlists.first.uri);
  }

  void onExtendTime(int minutes) {
    final int seconds = minutes * 60;

    _timerService.extendTime(seconds);
  }

  void onChangeVolume(bool enabled) {
    _timerModel.volumeAction.enabled = enabled;
    _prefsService.setBool(ActionType.VOLUME.toString(), enabled);
    notifyListeners();
  }

  void onChangeVolumeLevel(double value) {
    _timerModel.volumeAction.value = value;
    _prefsService.setDouble(_timerModel.volumeAction.key, value);
    notifyListeners();
  }

  void onChangePlayMusic(bool enabled) {
    _timerModel.playMusicAction.enabled = enabled;
    _prefsService.setBool(ActionType.PLAY_MUSIC.toString(), enabled);
    notifyListeners();
  }

  void onChangeDoNotDisturb(bool enabled) {
    _timerModel.doNotDisturbAction.enabled = enabled;
    _prefsService.setBool(ActionType.DND.toString(), enabled);
    notifyListeners();
  }

  void onChangeMedia(bool enabled) {
    _timerModel.mediaAction.enabled = enabled;
    _prefsService.setBool(ActionType.MEDIA.toString(), enabled);
    notifyListeners();
  }

  void onChangeWifi(bool enabled) {
    _timerModel.wifiAction.enabled = enabled;
    _prefsService.setBool(ActionType.WIFI.toString(), enabled);
    notifyListeners();
  }

  void onChangeBluetooth(bool enabled) {
    _timerModel.bluetoothAction.enabled = enabled;
    _prefsService.setBool(ActionType.BLUETOOTH.toString(), enabled);
    notifyListeners();
  }

  void onChangeScreen(bool enabled) {
    if (deviceAdmin) {
      _timerModel.screenAction.enabled = enabled;
      _prefsService.setBool(ActionType.SCREEN.toString(), enabled);
      notifyListeners();
    } else {
      navigateToSettings(deviceAdminFocused: true);
    }
  }

  void onChangeLight(bool enabled) {
    _timerModel.lightAction.enabled = enabled;
    notifyListeners();
  }

  void onChangeApp(bool enabled) {
    _timerModel.appAction.enabled = enabled;
    notifyListeners();
  }

  @override
  List<ReactiveServiceMixin> get reactiveServices => [_timerService];
}
