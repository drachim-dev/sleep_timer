import 'package:flutter/material.dart';
import 'package:introduction_screen/introduction_screen.dart';
import 'package:sleep_timer/ui/views/intro_viewmodel.dart';
import 'package:stacked/stacked.dart';

class IntroView extends StatefulWidget {
  @override
  _IntroViewState createState() => _IntroViewState();
}

class _IntroViewState extends State<IntroView> {
  final introKey = GlobalKey<IntroductionScreenState>();
  IntroViewModel model;

  @override
  Widget build(BuildContext context) {
    final ThemeData theme = Theme.of(context);

    final pageDecoration = PageDecoration(
      titleTextStyle: theme.textTheme.headline2
          .copyWith(fontSize: 36, color: Colors.grey[200]),
      bodyTextStyle:
          theme.textTheme.headline6.copyWith(color: Colors.grey[350]),
      descriptionPadding: EdgeInsets.fromLTRB(16, 8, 16, 0),
      pageColor: theme.scaffoldBackgroundColor,
      imagePadding: EdgeInsets.zero,
    );

    return ViewModelBuilder<IntroViewModel>.reactive(
        viewModelBuilder: () => IntroViewModel(),
        onModelReady: (model) => this.model = model,
        builder: (context, model, child) {
          return IntroductionScreen(
            key: introKey,
            pages: [
              PageViewModel(
                title: "Automate your daily sleep routine",
                body:
                    "Are you tired of adjusting the same settings every night in order to sleep well?",
                image: _buildImage("img_automate.webp"),
                decoration: pageDecoration,
              ),
              PageViewModel(
                title: "No interruptions",
                body:
                    "Don't get distracted by incoming messages or notifications",
                image: _buildImage("img_interruption.webp"),
                decoration: pageDecoration,
              ),
              PageViewModel(
                title: "Listen to your favorite music",
                body:
                    "Do you like to hear music or watch a movie while falling asleep?",
                image: _buildImage("img_music.webp", bottomPadding: 24),
                decoration: pageDecoration,
              ),
              PageViewModel(
                title: "Have a good night",
                body:
                    "Just set the timer and you're done.\nRelax and let your dreams come true ...",
                image: _buildImage("img_sleep.webp"),
                decoration: pageDecoration,
              ),
            ],
            skipFlex: 0,
            nextFlex: 0,
            showSkipButton: true,
            showNextButton: true,
            skip: Text('SKIP'),
            next: Icon(Icons.arrow_forward),
            done: Text('DONE'),
            onSkip: () => finishIntro(),
            onDone: () => finishIntro(),
            dotsDecorator: DotsDecorator(
              size: Size(10, 10),
              color: Colors.grey[350],
              activeColor: theme.accentColor,
              activeSize: Size(22, 10),
              activeShape: RoundedRectangleBorder(
                borderRadius: BorderRadius.all(Radius.circular(25)),
              ),
            ),
          );
        });
  }

  Widget _buildImage(String assetName, {double bottomPadding = 0}) {
    return Align(
      child: Padding(
        padding: EdgeInsets.only(bottom: bottomPadding),
        child: Image.asset('assets/intro/$assetName', width: 350.0),
      ),
      alignment: Alignment.bottomCenter,
    );
  }

  void finishIntro() => model.navigateToHome();
}
