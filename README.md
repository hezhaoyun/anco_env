# anco_env

[![pub package](https://img.shields.io/pub/v/anco_env.svg)](https://pub.dev/packages/anco_env)

A Flutter plugin for detecting the HarmonyOS (Anco) environment and collecting comprehensive system environment information.

[English](#english) | [简体中文](#chinese)

<a name="english"></a>

## Features

- **HarmonyOS Detection**: Detect if the app is running in the HarmonyOS Anco (Android Compatibility) environment.
- **Environment Info**: Collect detailed system information, including `android.os.Build` fields, system properties, and specific file/package existence.
- **System Properties**: Retrieve specific Android system properties via reflection.

## Getting Started

### Installation

Add `anco_env` to your `pubspec.yaml`:

```yaml
dependencies:
  anco_env: ^0.2.2
```

### Usage

#### Detect HarmonyOS Anco

```dart
bool isAnco = await AncoEnv.isHarmonyAnco();
print('Is HarmonyOS Anco: $isAnco');
```

#### Get Comprehensive Environment Info

```dart
Map<String, dynamic> envInfo = await AncoEnv.getEnvInfo();
print('Environment Info: $envInfo');
```

---

<a name="chinese"></a>

# anco_env (简体中文)

一个用于检测鸿蒙 (HarmonyOS Anco) 环境并收集详细系统环境信息的 Flutter 插件。

## 功能特性

- **鸿蒙检测**：由于 Anco 是华为 HarmonyOS 的安卓兼容层，该插件可以检测应用是否运行在鸿蒙系统的 Anco 环境中。
- **环境信息收集**：收集详细的系统信息，包括 `android.os.Build` 字段、系统属性、特定文件和包的存在情况等。
- **系统属性获取**：通过反射获取特定的 Android 系统属性。

## 快速上手

### 安装

在您的 `pubspec.yaml` 中添加 `anco_env`:

```yaml
dependencies:
  anco_env: ^0.1.0
```

### 使用方法

#### 检测鸿蒙 Anco 环境

```dart
bool isAnco = await AncoEnv.isHarmonyAnco();
print('是否为鸿蒙 Anco 环境: $isAnco');
```

#### 获取详细环境信息

```dart
Map<String, dynamic> envInfo = await AncoEnv.getEnvInfo();
print('环境信息: $envInfo');
```

## License

MIT License. See [LICENSE](LICENSE) for details.
