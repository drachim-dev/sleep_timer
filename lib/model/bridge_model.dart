import 'dart:convert';

import 'package:json_annotation/json_annotation.dart';
import 'package:sleep_timer/model/light_group.dart';

part 'bridge_model.g.dart';

enum Connection { unsaved, connected, failed }

@JsonSerializable()
class BridgeModel {
  final String? id, ip, mac, name;
  String? auth;
  Connection? state;
  List<LightGroup> groups;

  BridgeModel(
      {required this.id,
      String? ip,
      String? mac,
      String? name,
      this.auth,
      this.state = Connection.unsaved,
      List<LightGroup>? groups})
      : ip = ip ?? '',
        mac = mac ?? '',
        name = name ?? 'Philips Hue Bridge',
        groups = groups ?? [];

  factory BridgeModel.fromJson(Map<String, dynamic> json) =>
      _$BridgeModelFromJson(json);
  Map<String, dynamic> toJson() => _$BridgeModelToJson(this);

  static String encode(List<BridgeModel> bridges) => json.encode(
        bridges.map((bridge) => bridge.toJson()).toList(),
      );

  static List<BridgeModel> decode(String bridges) =>
      List.from(json.decode(bridges))
          .map((item) => BridgeModel.fromJson(item))
          .toList();
}
