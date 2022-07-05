import 'package:flutter/material.dart';

class SABT extends StatefulWidget {
  final Widget child;

  const SABT({required this.child});

  @override
  State<SABT> createState() => _SABTState();
}

class _SABTState extends State<SABT> {
  ScrollPosition? _position;
  bool _visible = false;

  @override
  void dispose() {
    _removeListener();
    super.dispose();
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    _removeListener();
    _addListener();
  }

  void _addListener() {
    _position = Scrollable.of(context)?.position;
    _position?.addListener(_positionListener);
    _positionListener();
  }

  void _removeListener() {
    _position?.removeListener(_positionListener);
  }

  void _positionListener() {
    // ignore: omit_local_variable_types
    final FlexibleSpaceBarSettings? settings = context
        .dependOnInheritedWidgetOfExactType(aspect: FlexibleSpaceBarSettings);
    var visible =
        settings == null || settings.currentExtent <= settings.minExtent;
    if (_visible != visible) {
      setState(() => _visible = visible);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Visibility(
      visible: _visible,
      child: widget.child,
    );
  }
}
