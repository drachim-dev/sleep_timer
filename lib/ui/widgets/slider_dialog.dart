import 'package:flutter/material.dart';
import 'package:sleep_timer/generated/l10n.dart';

class SliderDialog extends StatefulWidget {
  final String title;
  final double initialValue, minValue, maxValue;
  final Function(double) onChangeEnd;

  const SliderDialog(
      {@required this.title,
      @required this.initialValue,
      this.minValue = 0,
      @required this.maxValue,
      this.onChangeEnd})
      : assert(initialValue >= minValue && initialValue <= maxValue),
        assert(maxValue > minValue);

  @override
  _SliderDialogState createState() => _SliderDialogState(initialValue);
}

class _SliderDialogState extends State<SliderDialog> {
  double _initialValue;

  _SliderDialogState(this._initialValue);

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
        title: Text(widget.title),
        content: Container(
            height: 40,
            width: 400,
            child: Row(children: [
              Icon(Icons.volume_mute_outlined),
              Expanded(
                child: Slider(
                  value: _initialValue,
                  min: widget.minValue,
                  max: widget.maxValue,
                  divisions: widget.maxValue.round(),
                  onChanged: (value) {
                    setState(() => _initialValue = value);
                  },
                  onChangeEnd: widget.onChangeEnd,
                ),
              ),
              SizedBox(width: 48, child: Text('${_initialValue.round()} %'))
            ])),
        actions: [
          FlatButton(
            onPressed: () => Navigator.pop(context),
            child: Text(S.of(context).dialogCancel),
          ),
          FlatButton(
            onPressed: () => Navigator.pop(context, _initialValue),
            child: Text(S.of(context).dialogDone),
          )
        ]);
  }
}
