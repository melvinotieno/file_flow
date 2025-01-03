import 'handlers/base_handler.dart';
import 'handlers/mobile_handler.dart';
import 'models.dart';

class FileOperation {
  static FileOperation? _instance;

  late final BaseHandler _handler;

  FileOperation._() {
    _handler = MobileHandler();
  }

  factory FileOperation() {
    return _instance ??= FileOperation._();
  }

  TaskOperation download(DownloadTask task) {
    return TaskOperation._(task: task);
  }

  BatchTaskOperation downloadBatch(List<DownloadTask> tasks) {
    return BatchTaskOperation._(tasks: tasks);
  }

  TaskOperation upload(UploadTask task) {
    return TaskOperation._(task: task);
  }

  BatchTaskOperation uploadBatch(List<UploadTask> tasks) {
    return BatchTaskOperation._(tasks: tasks);
  }
}

class TaskOperation {
  TaskOperation._({
    required this.task,
  });

  final FlowTask task;

  Future<void> start() async {}

  Future<void> enqueue() async {}
}

class BatchTaskOperation {
  BatchTaskOperation._({
    required this.tasks,
  });

  final List<FlowTask> tasks;

  Future<void> start() async {}

  Future<void> enqueue() async {}
}

// import 'package:file_flow/pigeons/flow.g.dart';

// import 'handlers/base_handler.dart';
// import 'handlers/mobile_handler.dart'
//     if (dart.library.html) 'handlers/web_handler.dart';
// import 'models.dart';

// class FlowOperation {
//   static FlowOperation? _instance;

//   late final BaseHandler _handler;

//   FlowOperation._() {
//     _handler = createPlatformHandler();
//   }

//   factory FlowOperation() {
//     return _instance ??= FlowOperation._();
//   }

//   /// Creates a download task operation.
//   ///
//   /// Returns a [TaskOperation] that can be started or enqueued.
//   TaskOperation download(
//     DownloadTask task, {
//     void Function(double)? onProgress,
//     void Function(TaskState)? onStateChanged,
//   }) {
//     return TaskOperation._(
//       handler: _handler,
//       task: task,
//       onProgress: onProgress,
//       onStateChanged: onStateChanged,
//     );
//   }
// }

// class TaskOperation {
//   TaskOperation._({
//     required BaseHandler handler,
//     required this.task,
//     this.onProgress,
//     this.onStateChanged,
//   }) : _handler = handler;

//   final BaseHandler _handler;

//   final FlowTask task;

//   final void Function(double)? onProgress;

//   final void Function(TaskState)? onStateChanged;

//   Future<TaskStatus> start() {
//     return _handler.enqueueAndAwait(
//       task,
//       onProgress: onProgress,
//       onStateChanged: onStateChanged,
//     );
//   }

//   Future<bool> enqueue() {
//     return Future.value(false);
//   }
// }

// import 'handlers/base_handler.dart';
// import 'handlers/mobile_handler.dart'
//     if (dart.library.html) 'handlers/web_handler.dart'
//     if (dart.library.io) 'handlers/desktop_handler.dart';
// import 'messages.g.dart';
// import 'task.dart';

// class FileOperation {
//   // Singleton handlers for each operation type
//   static BaseHandler? _taskHandler;
//   static BaseHandler? _batchHandler;

//   static BaseHandler get _platformTaskHandler {
//     return _taskHandler ??= createPlatformHandler();
//   }

//   static BaseHandler get _platformBatchHandler {
//     return _batchHandler ??= createPlatformHandler();
//   }

//   /// Creates a download operation
//   static TaskOperation download(
//     String url, {
//     void Function(double)? onProgress,
//     void Function(TaskState)? onStateChanged,
//   }) {
//     final task = DownloadTask(url: url);
//     return TaskOperation._(
//       handler: _platformTaskHandler,
//       task: task,
//       onProgress: onProgress,
//       onStateChanged: onStateChanged,
//     );
//   }

//   /// Creates an upload operation
//   static TaskOperation upload(
//     String url, {
//     void Function(double)? onProgress,
//     void Function(TaskState)? onStateChanged,
//   }) {
//     final task = UploadTask(url: url);
//     return TaskOperation._(
//       handler: _platformTaskHandler,
//       task: task,
//       onProgress: onProgress,
//       onStateChanged: onStateChanged,
//     );
//   }

//   /// Creates a batch download operation
//   static BatchOperation batchDownload(
//     List<String> urls, {
//     void Function(double)? onOverallProgress,
//     void Function(int, double)? onIndividualProgress,
//     void Function(int, TaskState)? onIndividualStateChanged,
//   }) {
//     final tasks = urls.map((url) => DownloadTask(url: url)).toList();
//     return BatchOperation._(
//       handler: _platformBatchHandler,
//       tasks: tasks,
//       onOverallProgress: onOverallProgress,
//       onIndividualProgress: onIndividualProgress,
//       onIndividualStateChanged: onIndividualStateChanged,
//     );
//   }

//   /// Creates a batch upload operation
//   static BatchOperation batchUpload(
//     List<String> urls, {
//     void Function(double)? onOverallProgress,
//     void Function(int, double)? onIndividualProgress,
//     void Function(int, TaskState)? onIndividualStateChanged,
//   }) {
//     final tasks = urls.map((url) => UploadTask(url: url)).toList();
//     return BatchOperation._(
//       handler: _platformBatchHandler,
//       tasks: tasks,
//       onOverallProgress: onOverallProgress,
//       onIndividualProgress: onIndividualProgress,
//       onIndividualStateChanged: onIndividualStateChanged,
//     );
//   }

//   /// Disposes all handlers
//   static void dispose() {
//     _taskHandler?.dispose();
//     _taskHandler = null;
//     _batchHandler?.dispose();
//     _batchHandler = null;
//   }
// }

// /// Represents a single file operation that can be started or enqueued
// class TaskOperation {
//   TaskOperation._({
//     required BaseHandler handler,
//     required this.task,
//     this.onProgress,
//     this.onStateChanged,
//   }) : _handler = handler;

//   final BaseHandler _handler;
//   final FileTask task;
//   final void Function(double)? onProgress;
//   final void Function(TaskState)? onStateChanged;

//   /// Starts the operation and waits for completion
//   Future<TaskStatus> start() {
//     return _handler.enqueueAndAwait(
//       task,
//       onProgress: onProgress,
//       onStateChanged: onStateChanged,
//     );
//   }

//   /// Enqueues the operation without waiting
//   Future<bool> enqueue() {
//     return _handler.enqueue(task);
//   }
// }

// /// Represents a batch operation that can be started or enqueued
// class BatchOperation {
//   BatchOperation._({
//     required BaseHandler handler,
//     required this.tasks,
//     this.onOverallProgress,
//     this.onIndividualProgress,
//     this.onIndividualStateChanged,
//   }) : _handler = handler;

//   final BaseHandler _handler;
//   final List<FileTask> tasks;
//   final void Function(double)? onOverallProgress;
//   final void Function(int, double)? onIndividualProgress;
//   final void Function(int, TaskState)? onIndividualStateChanged;

//   /// Starts all operations and waits for completion
//   Future<List<TaskStatus>> start() async {
//     final futures = <Future<TaskStatus>>[];
//     var completedTasks = 0;

//     for (var i = 0; i < tasks.length; i++) {
//       futures.add(
//         _handler.enqueueAndAwait(
//           tasks[i],
//           onProgress: (progress) {
//             onIndividualProgress?.call(i, progress);
//             if (onOverallProgress != null) {
//               final overallProgress =
//                   (completedTasks + progress) / tasks.length;
//               onOverallProgress!(overallProgress);
//             }
//           },
//           onStateChanged: (state) {
//             if (state == TaskState.completed) completedTasks++;
//             onIndividualStateChanged?.call(i, state);
//           },
//         ),
//       );
//     }

//     return Future.wait(futures);
//   }

//   /// Enqueues all operations without waiting
//   Future<List<bool>> enqueue() {
//     return Future.wait(
//       tasks.map((task) => _handler.enqueue(task)),
//     );
//   }
// }
