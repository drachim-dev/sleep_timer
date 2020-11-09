import 'package:spotify/spotify.dart';

class SpotifyAuthentication {
  final String clientId, clientSecret, accessToken, refreshToken;
  final List<String> scopes;
  final int expiration;

  SpotifyAuthentication(
      {this.clientId,
      this.clientSecret,
      this.accessToken,
      this.refreshToken,
      this.scopes,
      this.expiration});

  SpotifyAuthentication.fromJson(Map<String, dynamic> json)
      : clientId = json['clientId'],
        clientSecret = json['clientSecret'],
        accessToken = json['accessToken'],
        refreshToken = json['refreshToken'],
        scopes = List<String>.from(json['scopes']),
        expiration = json['expiration'];

  SpotifyAuthentication.fromCredentials(SpotifyApiCredentials credentials)
      : clientId = credentials.clientId,
        clientSecret = credentials.clientSecret,
        accessToken = credentials.accessToken,
        refreshToken = credentials.refreshToken,
        scopes = credentials.scopes,
        expiration = credentials.expiration.millisecondsSinceEpoch;

  Map<String, dynamic> toJson() => {
        'clientId': clientId,
        'clientSecret': clientSecret,
        'accessToken': accessToken,
        'refreshToken': refreshToken,
        'scopes': scopes,
        'expiration': expiration,
      };

  SpotifyApiCredentials toCredentials() =>
      SpotifyApiCredentials(clientId, clientSecret,
          accessToken: accessToken,
          refreshToken: refreshToken,
          scopes: scopes,
          expiration: DateTime.fromMillisecondsSinceEpoch(expiration));
}
