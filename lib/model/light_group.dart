import 'dart:convert';

import 'package:json_annotation/json_annotation.dart';

part 'light_group.g.dart';

@JsonSerializable()
class LightGroup {
  final String id, name, className;
  final int numberOfLights;
  bool actionEnabled;

  LightGroup(
      {this.id,
      this.name,
      String className,
      this.numberOfLights,
      this.actionEnabled = false})
      : className = className ?? 'Other';

  factory LightGroup.fromJson(Map<String, dynamic> json) =>
      _$LightGroupFromJson(json);
  Map<String, dynamic> toJson() => _$LightGroupToJson(this);

  static String encode(List<LightGroup> groups) =>
      json.encode(groups.map((group) => group.toJson()).toList());

  static List<LightGroup> decode(String groups) =>
      List.from(json.decode(groups))
          .map((item) => LightGroup.fromJson(item))
          .toList();
}
