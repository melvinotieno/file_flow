import 'dart:io';

import 'handlers/base_handler.dart';
import 'handlers/native_handler.dart';

/// A singleton class that handles platform-specific operations.
///
/// Example:
/// ```dart
/// final handler = FlowHandler();
/// handler.platform.enqueue();
/// ```
class FlowHandler {
  /// The singleton instance of the [FlowHandler].
  static FlowHandler? _instance;

  /// The platform-specific handler.
  late final BaseHandler platform;

  /// Private constructor for the singleton pattern.
  FlowHandler._internal() {
    if (Platform.isAndroid) {
      platform = NativeHandler();
    } else {
      throw UnsupportedError('Unsupported platform');
    }
  }

  /// Returns the singleton instance of the [FlowHandler].
  factory FlowHandler() {
    _instance ??= FlowHandler._internal();
    return _instance!;
  }
}
