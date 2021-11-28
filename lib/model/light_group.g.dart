// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'light_group.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

LightGroup _$LightGroupFromJson(Map<String, dynamic> json) => LightGroup(
      id: json['id'] as String?,
      name: json['name'] as String?,
      className: json['className'] as String?,
      numberOfLights: json['numberOfLights'] as int?,
      actionEnabled: json['actionEnabled'] as bool? ?? false,
    );

Map<String, dynamic> _$LightGroupToJson(LightGroup instance) =>
    <String, dynamic>{
      'id': instance.id,
      'name': instance.name,
      'className': instance.className,
      'numberOfLights': instance.numberOfLights,
      'actionEnabled': instance.actionEnabled,
    };
