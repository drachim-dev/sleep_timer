import 'package:flutter/material.dart';
import 'package:introduction_screen/introduction_screen.dart';
import 'package:sleep_timer/generated/l10n.dart';
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
      titleTextStyle: theme.textTheme.headline2.copyWith(fontSize: 36),
      bodyTextStyle: theme.textTheme.headline6
          .copyWith(color: theme.textTheme.headline2.color),
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
                title: S.of(context).introAutomateSleepRoutineTitle,
                body: S.of(context).introAutomateSleepRoutineSubtitle,
                image: _buildImage("img_automate.webp"),
                decoration: pageDecoration,
              ),
              PageViewModel(
                title: S.of(context).introNoInterruptionsTitle,
                body: S.of(context).introNoInterruptionsSubtitle,
                image: _buildImage("img_interruption.webp"),
                decoration: pageDecoration,
              ),
              PageViewModel(
                title: S.of(context).introMediaTitle,
                body: S.of(context).IntroMediaSubtitle,
                image: _buildImage("img_music.webp", bottomPadding: 24),
                decoration: pageDecoration,
              ),
              PageViewModel(
                title: S.of(context).introGoodNightTitle,
                body: S.of(context).introGoodNightSubtitle,
                image: _buildImage("img_sleep.webp"),
                decoration: pageDecoration,
              ),
            ],
            skipFlex: 0,
            nextFlex: 0,
            showSkipButton: true,
            showNextButton: true,
            skip: Text(S.of(context).introButtonSkip, textAlign: TextAlign.center,),
            next: Icon(Icons.arrow_forward,
                semanticLabel: S.of(context).introButtonNext),
            done: Text(S.of(context).introButtonDone),
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
