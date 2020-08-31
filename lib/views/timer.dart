import 'package:auto_route/auto_route.dart';
import 'package:flutter/material.dart';
import 'package:sleep_timer/model/navigation_item.dart';
import 'package:sleep_timer/auto_router.gr.dart';
import 'package:sleep_timer/widgets/rounded_rect_button.dart';
import 'package:sleep_timer/widgets/timer_slider.dart';

class TimerPage extends StatefulWidget {
  final NavigationItemController controller;

  const TimerPage({@required this.controller});

  @override
  _TimerPageState createState() => _TimerPageState(controller);
}

class _TimerPageState extends State<TimerPage> {
  int _initialTime = 15;

  _TimerPageState(NavigationItemController _controller) {
    _controller.onClickFAB = onClickFAB;
  }

  @override
  Widget build(BuildContext context) {
    final ThemeData theme = Theme.of(context);

    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      mainAxisAlignment: MainAxisAlignment.spaceAround,
      children: [
        TimerSlider(
          initialValue: _initialTime,
          onUpdateLabel: (value) => "$value Min",
          onChange: _onChange,
        ),
        Row(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: [10, 20, 30, 60].map((value) {
              return RoundedRectButton(
                  title: "$value",
                  onPressed: () {
                    _onChange(value);
                    setState(() {});
                  });
            }).toList()),
      ],
    );
  }

  void _onChange(int value) {
    _initialTime = value;
  }

  void onClickFAB() {
    ExtendedNavigator.root.push(Routes.timerDetailPage,
        arguments: TimerDetailPageArguments(initialTime: _initialTime * 60));
  }
}
