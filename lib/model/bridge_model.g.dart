// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'bridge_model.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

BridgeModel _$BridgeModelFromJson(Map<String, dynamic> json) => BridgeModel(
  id: json['id'] as String?,
  ip: json['ip'] as String?,
  mac: json['mac'] as String?,
  name: json['name'] as String?,
  auth: json['auth'] as String?,
  state:
      $enumDecodeNullable(_$ConnectionEnumMap, json['state']) ??
      Connection.unsaved,
  groups: (json['groups'] as List<dynamic>?)
      ?.map((e) => LightGroup.fromJson(e as Map<String, dynamic>))
      .toList(),
);

Map<String, dynamic> _$BridgeModelToJson(BridgeModel instance) =>
    <String, dynamic>{
      'id': instance.id,
      'mac': instance.mac,
      'name': instance.name,
      'ip': instance.ip,
      'auth': instance.auth,
      'state': _$ConnectionEnumMap[instance.state],
      'groups': instance.groups,
    };

const _$ConnectionEnumMap = {
  Connection.unsaved: 'unsaved',
  Connection.pending: 'pending',
  Connection.connected: 'connected',
  Connection.failed: 'failed',
};
