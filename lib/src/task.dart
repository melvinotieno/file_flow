import 'package:uuid/uuid.dart';

import 'config.dart';
import 'flow.dart';
import 'handler.dart';
import 'pigeons/flow.g.dart';
import 'utilities.dart';

/// HTTP methods that can be used for a request.
enum RequestMethod { head, get, post, put, patch, delete }

/// Base class for all tasks.
sealed class _Task extends Task {
  _Task({
    required super.type,
    String? id,
    String? group,
    RequestMethod method = RequestMethod.get,
    Map<String, String> params = const {},
    super.headers = const {},
    RequestConfig? request,
    String? url,
    List<String>? urls,
    super.chunks,
    Uri? directoryUri,
    StorageDirectory? baseDirectory,
    super.directory,
    super.filename,
  }) : assert(id == null || id.isNotEmpty, 'id cannot be empty'),
       assert(group == null || group.isNotEmpty, 'group cannot be empty'),
       super(
         id: id ?? Uuid().v4(),
         group: group ?? FileFlow.config.defaultGroup,
         method: method.toString().split('.').last.toUpperCase(),
         timeout: request?.timeout ?? FileFlow.config.request.timeout,
         retries: request?.retries ?? FileFlow.config.request.retries,
         proxyAddress: request?.proxy?.address,
         proxyPort: request?.proxy?.port,
         url: url != null ? urlWithQueryParams(url, params) : null,
         urls: urls?.map((url) => urlWithQueryParams(url, params)).toList(),
         directoryUri: directoryUri?.toString(),
         baseDirectory: baseDirectory ?? FileFlow.config.baseDirectory,
       );

  Future<void> start({
    void Function(TaskState)? onStateChanged,
    void Function(int, TaskProgressData)? onProgressUpdate,
  }) async {
    await FlowHandler().platform.enqueueWithCallbacks(
      this,
      onStateChanged: onStateChanged,
      onProgressUpdate: onProgressUpdate,
    );
  }
}

/// A task to download a file.
final class DownloadTask extends _Task {
  DownloadTask({
    super.id,
    super.group,
    super.method,
    super.params,
    super.headers,
    super.request,
    required super.url,
    super.directoryUri,
    super.baseDirectory,
    super.directory,
    super.filename,
  }) : super(type: TaskType.download);

  /// Creates a download task with a directory picked by the user.
  ///
  /// This method will open a directory picker dialog for the user to select a
  /// directory where the file will be downloaded to.
  static Future<DownloadTask> withDirectoryPicker({
    String? id,
    String? group,
    RequestMethod method = RequestMethod.get,
    Map<String, String> params = const {},
    Map<String, String> headers = const {},
    RequestConfig? request,
    required String url,
    String? filename,
  }) async {
    final directoryUri = await FileFlow.picker.directory();

    return DownloadTask(
      id: id,
      group: group,
      method: method,
      params: params,
      headers: headers,
      request: request,
      url: url,
      directoryUri: directoryUri,
      filename: filename,
    );
  }
}

/// A task to upload a file.
final class UploadTask extends _Task {
  UploadTask({
    super.id,
    super.group,
    super.method = RequestMethod.post,
    super.params,
    super.headers,
    super.request,
    required super.url,
    super.directoryUri,
    super.baseDirectory,
    super.directory,
    super.filename,
  }) : super(type: TaskType.upload);
}

/// A task to upload multiple files.
final class MultiUploadTask extends _Task {
  MultiUploadTask({
    super.id,
    super.group,
    super.method = RequestMethod.post,
    super.params,
    super.headers,
    super.request,
    required super.url,
    super.directoryUri,
    super.baseDirectory,
    super.directory,
    super.filename,
  }) : super(type: TaskType.multiUpload);
}

/// A task to download a file in chunks.
final class ParallelDownloadTask extends _Task {
  ParallelDownloadTask({
    super.id,
    super.group,
    super.method,
    super.params,
    super.headers,
    super.request,
    required super.urls,
    super.chunks = 1,
    super.directoryUri,
    super.baseDirectory,
    super.directory,
    super.filename,
  }) : super(type: TaskType.parallelDownload);

  /// Creates a parallel download task with a directory picked by the user.
  ///
  /// This method will open a directory picker dialog for the user to select a
  /// directory where the file will be downloaded to.
  static Future<ParallelDownloadTask> withDirectoryPicker({
    String? id,
    String? group,
    RequestMethod method = RequestMethod.get,
    Map<String, String> params = const {},
    Map<String, String> headers = const {},
    RequestConfig? request,
    required List<String> urls,
    int chunks = 1,
    String? filename,
  }) async {
    final directoryUri = await FileFlow.picker.directory();

    return ParallelDownloadTask(
      id: id,
      group: group,
      method: method,
      params: params,
      headers: headers,
      request: request,
      urls: urls,
      chunks: chunks,
      directoryUri: directoryUri,
      filename: filename,
    );
  }
}
