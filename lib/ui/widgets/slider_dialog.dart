import 'package:flutter/material.dart';

class SliderDialog extends StatefulWidget {
  final String title;
  final double initialValue, minValue, maxValue;

  const SliderDialog(
      {@required this.title,
      @required this.initialValue,
      this.minValue = 0,
      @required this.maxValue})
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
            child: Row(children: [
              Icon(Icons.volume_mute_outlined),
              Slider(
                value: _initialValue,
                min: widget.minValue,
                max: widget.maxValue,
                onChanged: (value) {
                  setState(() => _initialValue = value);
                },
              ),
              SizedBox(width: 24, child: Text(_initialValue.floor().toString()))
            ])),
        actions: [
          FlatButton(
            onPressed: () => Navigator.pop(context, _initialValue),
            child: Text('DONE'),
          )
        ]);
  }
}
