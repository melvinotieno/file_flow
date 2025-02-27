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
