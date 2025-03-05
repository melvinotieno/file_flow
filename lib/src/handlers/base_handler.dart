import 'package:file_flow/src/pigeons/flow.g.dart';

/// An abstract base class that defines the structure for handling tasks.
abstract base class BaseHandler {
  /// A map to store task progress callbacks, keyed by task ID.
  final Map<String, void Function(int, TaskProgressData)>
  _taskProgressCallbacks = {};

  /// A map to store task state callbacks, keyed by task ID.
  final Map<String, void Function(TaskState)> _taskStateCallbacks = {};

  /// Enqueues a task for processing.
  ///
  /// Parameters:
  /// - [task]: The task to enqueue.
  ///
  /// Returns a [Future] that completes with a boolean indicating whether the
  /// task was successfully enqueued.
  Future<bool> enqueue(Task task);

  /// Enqueues a task for processing, with optional callbacks for progress
  /// updates and state changes.
  ///
  /// Parameters:
  /// - [task]: The task to enqueue.
  /// - [onProgressUpdate]: A callback to receive progress updates.
  /// - [onStateChanged]: A callback to receive state change updates.
  ///
  /// Returns a [Future] that completes when the task has been enqueued.
  Future<void> enqueueWithCallbacks(
    Task task, {
    void Function(TaskState)? onStateChanged,
    void Function(int, TaskProgressData)? onProgressUpdate,
  }) async {
    if (onProgressUpdate != null) {
      _taskProgressCallbacks[task.id] = onProgressUpdate;
    }

    if (onStateChanged != null) {
      _taskStateCallbacks[task.id] = onStateChanged;
    }

    final success = await enqueue(task);

    if (!success) {
      final callback = _taskStateCallbacks[task.id];
      callback?.call(TaskState.failed);
    }
  }

  /// Pauses a task with the given ID.
  ///
  /// Parameters:
  /// - [taskId]: The ID of the task to pause.
  ///
  /// Returns a [Future] that completes with a boolean indicating whether the
  /// task was successfully paused.
  Future<bool> pauseWithId(String taskId);

  /// Cancels a task with the given ID.
  ///
  /// Parameters:
  /// - [taskId]: The ID of the task to cancel.
  ///
  /// Returns a [Future] that completes with a boolean indicating whether the
  /// task was successfully canceled.
  Future<bool> cancelWithId(String taskId);

  /// Processes the progress update for a given task.
  ///
  /// Parameters:
  /// - [taskProgress]: The progress update data.
  void processProgressUpdate(TaskProgress taskProgress) {
    final callback = _taskProgressCallbacks[taskProgress.taskId];

    if (callback != null) {
      callback(taskProgress.progress, taskProgress.data);
    }
  }

  /// Processes the status update for a given task.
  ///
  /// Parameters:
  /// - [taskStatus]: The status update data.
  void processStatusUpdate(TaskStatus taskStatus) {
    final callback = _taskStateCallbacks[taskStatus.taskId];

    if (callback != null) {
      callback(taskStatus.state);
    }
  }
}
