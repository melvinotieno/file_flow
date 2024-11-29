import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'file_flow_method_channel.dart';

abstract class FileFlowPlatform extends PlatformInterface {
  /// Constructs a FileFlowPlatform.
  FileFlowPlatform() : super(token: _token);

  static final Object _token = Object();

  static FileFlowPlatform _instance = MethodChannelFileFlow();

  /// The default instance of [FileFlowPlatform] to use.
  ///
  /// Defaults to [MethodChannelFileFlow].
  static FileFlowPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [FileFlowPlatform] when
  /// they register themselves.
  static set instance(FileFlowPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
