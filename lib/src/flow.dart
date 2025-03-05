import 'config.dart';
import 'picker.dart';

class FileFlow {
  FileFlow._();

  static FileFlowConfig _config = FileFlowConfig();

  /// Returns the current [FileFlowConfig] configuration.
  static FileFlowConfig get config => _config;

  /// Configures [FileFlow] with the provided [config].
  ///
  /// Parameters:
  /// - `config`: The [FileFlowConfig] configuration to use.
  ///
  /// Usage:
  ///
  /// ```dart
  /// FileFlow.configure(FileFlowConfig(
  ///   request: RequestConfig(
  ///     timeout: 60,
  ///     retries: 0,
  ///     proxy: ProxyConfig(address: 'http://proxy.example.com', port: 8080),
  ///   ),
  ///   baseDirectory: StorageDirectory.downloads,
  ///   defaultGroup: 'default',
  /// ));
  /// ```
  ///
  /// Note: The values provided above are the default values in exception of
  /// the `proxy` field which is not set by default. If you do not provide a
  /// value for a field, the default value will be used.
  static void configure(FileFlowConfig config) {
    _config = config;
  }

  /// Returns the [Picker] instance to use for picking a directory or file(s).
  static Picker get picker => Picker();
}
