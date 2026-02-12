import 'package:flutter/services.dart';

class AncoEnv {
  static const MethodChannel _channel = MethodChannel('anco_env');

  static Future<String?> getPlatformVersion() {
    return _channel.invokeMethod<String>('getPlatformVersion');
  }

  static Future<bool> isHarmonyAnco() async {
    try {
      final bool? isAnco = await _channel.invokeMethod<bool>('isHarmonyAnco');
      return isAnco ?? false;
    } catch (e) {
      return false;
    }
  }

  static Future<Map<String, dynamic>> getEnvInfo() async {
    try {
      final Map? result = await _channel.invokeMethod<Map>('getEnvInfo');
      if (result != null) {
        return Map<String, dynamic>.from(result);
      }
    } catch (e) {
      // Ignore
    }
    return {};
  }

  static Future<String?> getProp(String key) async {
    try {
      return await _channel.invokeMethod<String>('getProp', {'key': key});
    } catch (e) {
      return null;
    }
  }
}
