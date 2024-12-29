import 'package:pigeon/pigeon.dart';

///
enum StorageDirectory {
  applicationDocuments,

  downloads,
}

/// The type of a task.
enum TaskType {
  /// Task is a download task.
  download,

  /// Task is an upload task.
  upload,
}

/// The state of a task.
enum TaskState {
  /// Task is pending.
  pending,

  /// Task is running.
  running,

  /// Task is paused.
  paused,

  /// Task has completed successfully.
  completed,

  /// Task has failed.
  failed,

  /// Task has been canceled.
  canceled,
}

class TaskProgress {
  TaskProgress({
    required this.progress,
  });

  final int progress;
}

class TaskStatus {
  TaskStatus({required this.state});

  final TaskState state;
}

class Task {
  Task({
    required this.id,
    required this.url,
    required this.method,
    required this.headers,
    required this.timeout,
    this.proxyAddress,
    this.proxyPort,
    required this.baseDirectory,
    this.directory,
    this.filename,
    required this.type,
  });

  final String id;

  final String url;

  final String method;

  final Map<String, String> headers;

  final int timeout;

  final String? proxyAddress;

  final int? proxyPort;

  final StorageDirectory baseDirectory;

  final String? directory;

  final String? filename;

  final TaskType type;
}

@HostApi()
abstract class FileFlowHostApi {
  bool enqueue(Task task);

  bool pause(String taskId);

  bool resume(String taskId);
}

@FlutterApi()
abstract class FileFlowFlutterApi {
  void onProgress(String taskId, int progress);
}
