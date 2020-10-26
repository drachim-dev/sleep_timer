import 'package:auto_route/auto_route.dart';
import 'package:flutter/material.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/model/navigation_item_model.dart';
import 'package:sleep_timer/ui/widgets/rounded_rect_button.dart';
import 'package:sleep_timer/ui/widgets/timer_slider.dart';
import 'package:stacked/stacked.dart';

import 'timer_viewmodel.dart';

class TimerView extends StatefulWidget {
  final NavigationItemController controller;

  const TimerView({@required this.controller});

  @override
  _TimerViewState createState() => _TimerViewState(controller);
}

class _TimerViewState extends State<TimerView> {
  TimerViewModel model;

  _TimerViewState(NavigationItemController _controller) {
    _controller.onClickFAB = onClickFAB;
  }

  @override
  Widget build(BuildContext context) {
    final ThemeData theme = Theme.of(context);

    return ViewModelBuilder<TimerViewModel>.reactive(
        viewModelBuilder: () => TimerViewModel(),
        onModelReady: (model) => this.model = model,
        builder: (context, model, child) {
          return Padding(
            padding: const EdgeInsets.symmetric(
                horizontal: kHorizontalPadding, vertical: kVerticalPaddingBig),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.spaceAround,
              children: [
                model.hasActiveTimer ? _buildActiveTimers(theme) : SizedBox(),
                TimerSlider(
                  initialValue: model.initialTime,
                  onUpdateLabel: (value) => "$value Min",
                  onChange: (value) => model.setTime(value.round()),
                ),
                Padding(
                  padding: const EdgeInsets.only(bottom: kVerticalPaddingBig),
                  child: Row(
                      mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                      children: [10, 20, 30, 60].map((value) {
                        return Expanded(
                          child: RoundedRectButton(
                              title: "$value",
                              onPressed: () => model.updateTime(value)),
                        );
                      }).toList()),
                ),
              ],
            ),
          );
        });
  }

  Widget _buildActiveTimers(ThemeData theme) {
    return FlatButton(
      child: Text("Tap here to see your timer"),
      padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 12),
      shape: StadiumBorder(
          side: BorderSide(
        color: theme.colorScheme.primary.withAlpha(50),
        width: 3,
      )),
      onPressed: () => model.openActiveTimer(),
    );
  }

  void onClickFAB() => model.startNewTimer();
}
