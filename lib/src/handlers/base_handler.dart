import 'dart:async';

import 'package:file_flow/pigeons/flow.g.dart';

import '../models.dart';

abstract class BaseHandler {
  final Map<String, Completer<void>> _taskCompleters = {};

  final Map<String, void Function(double)> _taskProgressCallbacks = {};

  final Map<String, void Function(TaskState)> _taskStateCallbacks = {};

  /// Enqueues a task without waiting for completion.
  Future<bool> enqueue(FlowTask task);

  /// Enqueues a task and waits for its completion.
  Future<TaskStatus> enqueueAndAwait(
    FlowTask task, {
    void Function(double)? onProgress,
    void Function(TaskState)? onStateChanged,
  }) async {
    if (onProgress != null) {
      _taskProgressCallbacks[task.id] = onProgress;
    }

    if (onStateChanged != null) {
      _taskStateCallbacks[task.id] = onStateChanged;
    }

    final taskCompleter = Completer<TaskStatus>();
    _taskCompleters[task.id] = taskCompleter;

    if (!await enqueue(task)) {
      return Future.value(TaskStatus(taskId: task.id, state: TaskState.failed));
    }

    return taskCompleter.future;
  }

  /////////////////////////////////////////////////////////////////////////

  // final Map<String, StreamController<double>> _progressControllers = {};

  // /// Enqueues a task and waits for its completion
  // Future<void> enqueueAndAwaitee(
  //   FlowTask task, {
  //   void Function(double)? onProgress,
  //   void Function(TaskState)? onStateChanged,
  // }) async {
  //   final completer = Completer<void>();
  //   _taskCompleters[task.id] = completer;

  //   if (onProgress != null) {
  //     final controller = StreamController<double>.broadcast();
  //     _progressControllers[task.id] = controller;
  //     controller.stream.listen(onProgress);
  //   }

  //   try {
  //     await enqueue(task);
  //     return completer.future;
  //   } catch (e) {
  //     _taskCompleters.remove(task.id);
  //     _progressControllers.remove(task.id)?.close();
  //     rethrow;
  //   }
  // }

  // /// Called by implementations when a task completes
  // void completeTask(String taskId) {
  //   _taskCompleters.remove(taskId)?.complete();
  //   _progressControllers.remove(taskId)?.close();
  // }

  // /// Called by implementations when a task fails
  // void failTask(String taskId, [Object? error]) {
  //   _taskCompleters.remove(taskId)?.completeError(error ?? 'Task failed');
  //   _progressControllers.remove(taskId)?.close();
  // }

  // /// Called by implementations to update progress
  // void updateProgress(String taskId, double progress) {
  //   _progressControllers[taskId]?.add(progress);
  // }

  // /// Cleanup resources
  // void dispose() {
  //   for (final completer in _taskCompleters.values) {
  //     if (!completer.isCompleted) {
  //       completer.completeError('Handler disposed');
  //     }
  //   }
  //   for (final controller in _progressControllers.values) {
  //     controller.close();
  //   }
  //   _taskCompleters.clear();
  //   _progressControllers.clear();
  // }
}
