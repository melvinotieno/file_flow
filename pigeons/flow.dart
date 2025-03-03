import 'package:pigeon/pigeon.dart';

/// Represents the base storage directory for task files.
///
/// The temporary path and paths prefixed with `application` correspond to
/// those provided by the `path_provider` package. For more details, see:
/// https://pub.dev/packages/path_provider
///
/// The remaining directories are visible to the user i.e. they can easily
/// access the files stored in these directories from the file manager.
enum StorageDirectory {
  /// Path to a directory where the application may place application-specific
  /// cache files.
  ///
  /// If this directory does not exist, it is created automatically.
  applicationCache,

  /// Path to a directory where the application may place data that is
  /// user-generated, or that cannot otherwise be recreated by your application.
  ///
  /// Consider using another path, such as [applicationSupport], or
  /// [applicationCache], if the data is not user-generated.
  applicationDocuments,

  /// Path to the directory where application can store files that are
  /// persistent, backed up, and not visible to the user, such as sqlite.db.
  ///
  /// This directory is only supported on iOS and macOS.
  applicationLibrary,

  /// Path to a directory where the application may place application support
  /// files.
  ///
  /// If this directory does not exist, it is created automatically.
  ///
  /// Use this for files you don't want exposed to the user. Your app should not
  /// use this directory for user data files.
  applicationSupport,

  /// Path to the temporary directory on the device that is not backed up and is
  /// suitable for storing caches of downloaded files.
  ///
  /// Files in this directory may be cleared at any time. This does *not* return
  /// a new temporary directory. Instead, the caller is responsible for creating
  /// (and cleaning up) files or directories within this directory. This
  /// directory is scoped to the calling application.
  temporary,

  /// Path to downloaded files.
  downloads,

  /// Path to image files.
  images,

  /// Path to video files.
  video,

  /// Path to audio files.
  audio,

  /// Path to general files that do not fit the other categories. This is only
  /// available on Android.
  files,
}

/// Defines what type a task is.
enum TaskType {
  /// A task that downloads a file from a specified URL.
  download,

  /// A task that uploads a file to a specified URL.
  upload,

  /// A task that uploads multiple files to a specified URL.
  multiUpload,

  /// A task that downloads a file in chunks from one or more URLs.
  parallelDownload,
}

/// The state of a task.
enum TaskState {
  /// The task has been enqueued waiting to be executed.
  ///
  /// A task can remain in this state until certain constraints, if any, are
  /// met. For example, a task may be waiting for a network connection to be
  /// established before it can be executed.
  pending,

  /// The task is being executed. For example, if the task is a `DownloadTask`,
  /// this state indicates that the task is currently downloading the file.
  running,

  /// The task has been paused and may be resumed.
  paused,

  /// The task has been canceled. This can either be intentionally by the user
  /// or by the system.
  canceled,

  /// The task has been completed successfully.
  completed,

  /// The task has failed due to an error it encountered during its execution.
  failed,
}

/// The error code of a task exception.
enum TaskErrorCode {
  /// The error resulted from the url(s) being invalid.
  url,

  /// A network connection error resulted in the task failing.
  connection,

  /// An error was found in the response from the server.
  http,

  /// The error resulted during the file transfer process.
  transfer,

  /// An error occurred while writing to the filesystem.
  filesystem,

  /// The error was caused by an unknown reason.
  unknown,
}

/// Represents an exception that occurred during a task execution.
class TaskException {
  TaskException({required this.code, required this.message, this.rawResponse});

  /// The error code of the exception.
  final TaskErrorCode code;

  /// The error message of the exception.
  final String message;

  /// The response from the server that caused the exception. If provided, the
  /// exception is considered to be as a result of a failed HTTP request.
  final String? rawResponse;
}

/// The progress data of a task.
class TaskProgressData {
  TaskProgressData({
    required this.expectedBytes,
    required this.transferredBytes,
    required this.networkSpeed,
  });

  /// The total number of bytes that are expected to be transferred.
  final int expectedBytes;

  /// The number of bytes that have been transferred.
  final int transferredBytes;

  /// The network speed in bytes per second.
  final int networkSpeed;
}

/// The data of a task that has been paused.
class TaskResumeData {
  TaskResumeData({
    required this.taskString,
    required this.tempPath,
    required this.transferredBytes,
  });

  /// The string representation of the task.
  final String taskString;

  /// The temporary path of the task file.
  final String tempPath;

  /// The number of bytes that have been transferred.
  final int transferredBytes;
}

/// The data of a task that has been completed.
class TaskCompleteData {
  TaskCompleteData({this.path, this.mimeType, this.rawResponse});

  /// The path of the task file.
  ///
  /// This is the path to the file that was downloaded or uploaded.
  final String? path;

  /// The MIME type of the task file.
  final String? mimeType;

  /// The server response of the url request.
  final String? rawResponse;
}

/// The task used by native code for execution.
class Task {
  Task({
    required this.type,
    required this.id,
    required this.group,
    required this.method,
    required this.headers,
    required this.timeout,
    required this.retries,
    this.proxyAddress,
    this.proxyPort,
    this.url,
    this.urls,
    this.chunks,
    this.directoryUri,
    this.baseDirectory,
    this.directory,
    this.filename,
  });

  /// The type of task to execute.
  final TaskType type;

  /// The unique identifier of the task.
  final String id;

  /// The group identifier of the task.
  final String group;

  /// The HTTP method to use for the request.
  final String method;

  /// The headers to send with the request.
  final Map<String, String> headers;

  /// The timeout for the task in milliseconds.
  final int timeout;

  /// The number of times to retry the task request if it fails.
  final int retries;

  /// The proxy address to use for the task request.
  final String? proxyAddress;

  /// The proxy port to use with the proxy address.
  final int? proxyPort;

  /// The url to use for the task request. This is required for all the tasks
  /// that are not of type [TaskType.parallelDownload].
  final String? url;

  /// A list of urls to use for the task request. This is required for tasks of
  /// type [TaskType.parallelDownload].
  final List<String>? urls;

  /// The number of chunks to download per URL for [TaskType.parallelDownload].
  final int? chunks;

  /// The directory URI for download tasks.
  ///
  /// Used in place of [baseDirectory]/[directory].
  final String? directoryUri;

  /// The base directory for the task files.
  final StorageDirectory? baseDirectory;

  /// The child directory within the base directory for the task files.
  final String? directory;

  /// The filename of the task file.
  final String? filename;
}

/// Represents a task event.
sealed class TaskEvent {}

/// The status event of a task execution.
class TaskStatus extends TaskEvent {
  TaskStatus({
    required this.taskId,
    required this.taskGroup,
    required this.state,
    this.completeData,
    this.resumeData,
    this.exception,
  });

  /// The task id.
  final String taskId;

  /// The task group.
  final String taskGroup;

  /// The current state of the task.
  final TaskState state;

  /// The data associated with a [TaskState.completed] state.
  final TaskCompleteData? completeData;

  /// The data associated with a [TaskState.paused] state.
  final TaskResumeData? resumeData;

  /// The exception that occurred during the task execution. This is only
  /// provided if the task state is [TaskState.failed].
  final TaskException? exception;
}

/// The progress event of a task execution.
class TaskProgress extends TaskEvent {
  TaskProgress({
    required this.taskId,
    required this.taskGroup,
    required this.progress,
    required this.data,
  });

  /// The task id.
  final String taskId;

  /// The task group.
  final String taskGroup;

  /// The progress of the task.
  final double progress;

  /// The progress data of the task.
  final TaskProgressData data;
}

@HostApi()
abstract class FileFlowHostApi {
  /// Enqueues a task for execution.
  ///
  /// Returns `true` if the task was successfully enqueued; otherwise, `false`.
  bool enqueue(Task task);

  /// Pauses a task with the specified id.
  ///
  /// Returns `true` if the task was successfully paused; otherwise, `false`.
  bool pauseWithId(String taskId);

  /// Resumes a task with the specified resume data.
  ///
  /// Returns `true` if the task was successfully resumed; otherwise, `false`.
  bool resume(TaskResumeData resumeData);

  /// Cancels a task with the specified id.
  ///
  /// Returns `true` if the task was successfully canceled; otherwise, `false`.
  bool cancelWithId(String taskId);
}

@FlutterApi()
abstract class FileFlowFlutterApi {
  void onStatusUpdate(TaskStatus taskStatus);

  void onProgressUpdate(TaskProgress taskProgress);
}
