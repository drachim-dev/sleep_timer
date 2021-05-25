// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'bridge_model.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

BridgeModel _$BridgeModelFromJson(Map<String, dynamic> json) {
  return BridgeModel(
    id: json['id'] as String?,
    ip: json['ip'] as String?,
    mac: json['mac'] as String?,
    name: json['name'] as String?,
    auth: json['auth'] as String?,
    state: _$enumDecodeNullable(_$ConnectionEnumMap, json['state']),
    groups: (json['groups'] as List<dynamic>?)
        ?.map((e) => LightGroup.fromJson(e as Map<String, dynamic>))
        .toList(),
  );
}

Map<String, dynamic> _$BridgeModelToJson(BridgeModel instance) =>
    <String, dynamic>{
      'id': instance.id,
      'ip': instance.ip,
      'mac': instance.mac,
      'name': instance.name,
      'auth': instance.auth,
      'state': _$ConnectionEnumMap[instance.state],
      'groups': instance.groups,
    };

K _$enumDecode<K, V>(
  Map<K, V> enumValues,
  Object? source, {
  K? unknownValue,
}) {
  if (source == null) {
    throw ArgumentError(
      'A value must be provided. Supported values: '
      '${enumValues.values.join(', ')}',
    );
  }

  return enumValues.entries.singleWhere(
    (e) => e.value == source,
    orElse: () {
      if (unknownValue == null) {
        throw ArgumentError(
          '`$source` is not one of the supported values: '
          '${enumValues.values.join(', ')}',
        );
      }
      return MapEntry(unknownValue, enumValues.values.first);
    },
  ).key;
}

K? _$enumDecodeNullable<K, V>(
  Map<K, V> enumValues,
  dynamic source, {
  K? unknownValue,
}) {
  if (source == null) {
    return null;
  }
  return _$enumDecode<K, V>(enumValues, source, unknownValue: unknownValue);
}

const _$ConnectionEnumMap = {
  Connection.unsaved: 'unsaved',
  Connection.connected: 'connected',
  Connection.failed: 'failed',
};
