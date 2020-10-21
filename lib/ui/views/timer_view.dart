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
    return ViewModelBuilder<TimerViewModel>.reactive(
        viewModelBuilder: () => TimerViewModel(),
        onModelReady: (model) => this.model = model,
        builder: (context, model, child) {
          return Padding(
            padding: const EdgeInsets.symmetric(vertical: kVerticalPaddingBig),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              mainAxisAlignment: MainAxisAlignment.spaceAround,
              children: [
                TimerSlider(
                  initialValue: model.initialTime,
                  onUpdateLabel: (value) => "$value Min",
                  onChange: (value) => model.setTime(value.round()),
                ),
                Row(
                    mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                    children: [10, 20, 30, 60].map((value) {
                      return Expanded(
                        child: RoundedRectButton(
                            title: "$value",
                            onPressed: () => model.updateTime(value)),
                      );
                    }).toList()),
              ],
            ),
          );
        });
  }

  void onClickFAB() => model.navigateToTimerDetail();
}
