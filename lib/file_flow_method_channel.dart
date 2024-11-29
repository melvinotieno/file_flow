import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'file_flow_platform_interface.dart';

/// An implementation of [FileFlowPlatform] that uses method channels.
class MethodChannelFileFlow extends FileFlowPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('file_flow');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
