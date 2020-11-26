import 'package:flutter/material.dart';
import 'package:sleep_timer/app/locator.dart';
import 'package:stacked/stacked.dart';
import 'package:stacked_services/stacked_services.dart';
import 'package:webview_flutter/webview_flutter.dart';

class SpotifyAuthView extends StatefulWidget {
  final String url, redirectUrl;

  const SpotifyAuthView({@required this.url, @required this.redirectUrl});

  @override
  _SpotifyAuthViewState createState() => _SpotifyAuthViewState();
}

class _SpotifyAuthViewState extends State<SpotifyAuthView> {
  SpotifyAuthViewModel model;

  @override
  Widget build(BuildContext context) {
    return ViewModelBuilder<SpotifyAuthViewModel>.reactive(
        viewModelBuilder: () => SpotifyAuthViewModel(),
        onModelReady: (model) => this.model = model,
        builder: (context, model, child) {
          return Scaffold(
            appBar: AppBar(
              title: Text('Spotify authentication'),
            ),
            body: WebView(
              javascriptMode: JavascriptMode.unrestricted,
              initialUrl: widget.url,
              navigationDelegate: (navReq) {
                final responseUrl = navReq.url;
                if (responseUrl.startsWith(widget.redirectUrl)) {
                  model.navigateBack(responseUrl);
                  return NavigationDecision.prevent;
                }

                return NavigationDecision.navigate;
              },
            ),
          );
        });
  }
}

class SpotifyAuthViewModel extends BaseViewModel {
  final _navigationService = locator<NavigationService>();

  bool navigateBack(final String responseUrl) {
    return _navigationService.back(result: responseUrl);
  }
}
