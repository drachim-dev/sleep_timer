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
          height: 60,
          child: Row(
            children: [
              Icon(Icons.volume_mute_outlined),
              Slider(
                label: _initialValue.floor().toString(),
                value: _initialValue,
                min: widget.minValue,
                max: widget.maxValue,
                divisions: widget.maxValue.floor(),
                onChanged: (value) {
                  setState(() => _initialValue = value);
                },
              ),
              Icon(Icons.volume_up_outlined),
            ],
          ),
        ),
        actions: [
          FlatButton(
            onPressed: () => Navigator.pop(context, _initialValue),
            child: Text('DONE'),
          )
        ]);
  }
}
