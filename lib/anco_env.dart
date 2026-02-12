import 'package:flutter/services.dart';

/// A utility class for retrieving information about the HarmonyOS (Anco) environment.
class AncoEnv {
  static const MethodChannel _channel = MethodChannel('anco_env');

  /// Returns the underlying platform version string.
  static Future<String?> getPlatformVersion() {
    return _channel.invokeMethod<String>('getPlatformVersion');
  }

  /// Checks if the current application is running within the HarmonyOS Anco
  /// (Android Compatibility) environment.
  ///
  /// Returns `true` if it's HarmonyOS Anco, otherwise `false`.
  static Future<bool> isHarmonyAnco() async {
    try {
      final bool? isAnco = await _channel.invokeMethod<bool>('isHarmonyAnco');
      return isAnco ?? false;
    } catch (e) {
      return false;
    }
  }

  /// Retrieves a comprehensive map of environment information.
  ///
  /// This includes device build info, system properties, and flags indicating
  /// the presence of certain files or packages often found in HarmonyOS.
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

  /// Fetches a specific system property by its [key] using Android's `getprop`.
  static Future<String?> getProp(String key) async {
    try {
      return await _channel.invokeMethod<String>('getProp', {'key': key});
    } catch (e) {
      return null;
    }
  }
}
