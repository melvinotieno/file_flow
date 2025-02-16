import 'pigeons/flow.g.dart';

/// Configuration for proxy settings.
class ProxyConfig {
  /// Creates a new instance of [ProxyConfig].
  ProxyConfig({required this.address, required this.port})
    : assert(address.isNotEmpty, 'Address cannot be empty'),
      assert(port > 0, 'Port must be greater than 0');

  /// The address of the proxy server.
  final String address;

  /// The port of the proxy server.
  final int port;
}

/// Configuration for request settings.
class RequestConfig {
  /// Creates a new instance of [RequestConfig].
  ///
  /// [timeout] defaults to 60 seconds if not provided.
  ///
  /// [retries] defaults to 0 if not provided.
  const RequestConfig({this.timeout = 60, this.retries = 0, this.proxy})
    : assert(timeout > 0, 'Timeout must be greater than 0'),
      assert(retries >= 0, 'Retries must be greater than or equal to 0');

  /// The request timeout in seconds.
  final int timeout;

  /// The number of retry attempts.
  final int retries;

  /// The proxy configuration.
  final ProxyConfig? proxy;
}

/// Configuration for FileFlow settings.
class FileFlowConfig {
  /// Creates a new instance of [FileFlowConfig].
  ///
  /// [request] defaults to a timeout of 60 seconds and 0 retries.
  ///
  /// [baseDirectory] defaults to [StorageDirectory.downloads].
  ///
  /// [defaultGroup] defaults to 'default'.
  FileFlowConfig({
    this.request = const RequestConfig(),
    this.baseDirectory = StorageDirectory.downloads,
    this.defaultGroup = 'default',
  }) : assert(defaultGroup.isNotEmpty, 'Default group cannot be empty');

  /// The request configuration.
  final RequestConfig request;

  /// The base storage directory.
  final StorageDirectory baseDirectory;

  /// The default group name for tasks.
  final String defaultGroup;
}
