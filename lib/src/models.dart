import 'package:file_flow/pigeons/flow.g.dart';
import 'package:uuid/uuid.dart';

import 'flow.dart';
import 'utilities.dart';

/// HTTP methods that can be used for a request.
enum RequestMethod { get, post, put, patch, delete }

/// Represents a server HTTP request.
base class Request {
  Request({
    required String url,
    this.params = const {},
    this.method = RequestMethod.get,
    this.headers = const {},
    int? timeout,
    int? retries,
    ProxyConfig? proxy,
  })  : url = urlWithQueryParams(url, params),
        timeout = timeout ?? FileFlow.config.request.timeout,
        retries = retries ?? FileFlow.config.request.retries,
        proxy = proxy ?? FileFlow.config.request.proxy;

  /// The URL to send the request to.
  final String url;

  /// The query parameters to send with the request.
  final Map<String, String> params;

  /// The HTTP method to use for the request.
  final RequestMethod method;

  /// The headers to send with the request.
  final Map<String, String> headers;

  /// The timeout for the request in seconds.
  final int timeout;

  /// The number of times to retry the request.
  final int retries;

  /// The proxy configuration to use for the request.
  final ProxyConfig? proxy;
}

sealed class FlowTask extends Request {
  FlowTask({
    required super.url,
    super.params,
    super.method,
    super.headers,
    super.timeout,
    super.proxy,
    String? id,
    StorageDirectory? baseDirectory,
    this.directory,
    this.filename,
    this.group = "default",
  })  : id = id ?? const Uuid().v4(),
        baseDirectory = baseDirectory ?? FileFlow.config.baseDirectory;

  /// The unique identifier for the task.
  final String id;

  final StorageDirectory baseDirectory;

  final String? directory;

  final String? filename;

  /// The group that the task belongs to.
  final String group;

  /// The type of task.
  TaskType get type;

  Task toPlatformTask() {
    return Task(
      id: id,
      url: url,
      method: method.toString().split('.').last.toUpperCase(),
      headers: headers,
      timeout: timeout,
      proxy: proxy,
      baseDirectory: baseDirectory,
      directory: directory,
      filename: filename,
      group: group,
      type: type,
    );
  }
}

final class DownloadTask extends FlowTask {
  DownloadTask({
    required super.url,
    super.params,
    super.headers,
    super.id,
    super.baseDirectory,
    super.directory,
    super.filename,
  });

  @override
  TaskType get type => TaskType.download;
}

final class UploadTask extends FlowTask {
  UploadTask({
    required super.url,
    super.params,
    super.headers,
    super.id,
    super.baseDirectory,
    super.directory,
    super.filename,
  });

  @override
  TaskType get type => TaskType.upload;
}
