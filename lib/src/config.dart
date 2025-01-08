import 'package:file_flow/pigeons/flow.g.dart';

class FileFlowConfig {
  FileFlowConfig({
    this.request = const RequestConfig(),
    this.baseDirectory = StorageDirectory.downloads,
    this.defaultGroup = 'default',
  });

  final RequestConfig request;

  final StorageDirectory baseDirectory;

  final String defaultGroup;
}

class RequestConfig {
  const RequestConfig({this.timeout = 60, this.retries = 0, this.proxy});

  final int timeout;

  final int retries;

  final ProxyConfig? proxy;
}
