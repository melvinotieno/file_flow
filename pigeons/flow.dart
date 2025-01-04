import 'package:pigeon/pigeon.dart';

class ProxyConfig {
  ProxyConfig({
    required this.address,
    required this.port,
  });

  final String address;

  final int port;
}

/// Represents the base storage directory for task files.
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
  ///
  pending,

  /// Task is running.
  running,

  /// Task is paused.
  paused,

  /// Task has completed successfully.
  completed,

  /// Task has failed.
  failed,

  // retrying, TODO: implement retrying state

  /// Task has been canceled.
  canceled,
}

class Task {
  Task({
    required this.id,
    required this.url,
    required this.method,
    required this.headers,
    required this.timeout,
    this.proxy,
    required this.baseDirectory,
    this.directory,
    this.filename,
    required this.group,
    required this.type,
  });

  final String id;

  final String url;

  final String method;

  final Map<String, String> headers;

  final int timeout;

  final ProxyConfig? proxy;

  final StorageDirectory baseDirectory;

  final String? directory;

  final String? filename;

  final String group;

  final TaskType type;
}

@HostApi()
abstract class FileFlowHostApi {
  bool enqueue(Task task);

  bool pause(String taskId);

  bool resume(String taskId);
}

sealed class TaskEvent {}

class TaskProgress extends TaskEvent {
  TaskProgress({
    required this.taskId,
    required this.progress,
  });

  final String taskId;

  final int progress;
}

class TaskStatus extends TaskEvent {
  TaskStatus({required this.taskId, required this.state});

  final String taskId;

  final TaskState state;
}

@EventChannelApi()
abstract class FileFlowEventChannelApi {
  TaskEvent streamTaskEvents();
}
