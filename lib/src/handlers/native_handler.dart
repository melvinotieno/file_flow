import 'package:file_flow/src/pigeons/flow.g.dart';

import 'base_handler.dart';

/// A concrete implementation of the [BaseHandler] class that interacts with the
/// native platform i.e. Android and iOS.
final class NativeHandler extends BaseHandler implements FileFlowFlutterApi {
  static final _api = FileFlowHostApi();

  @override
  Future<bool> enqueue(Task task) async {
    return _api.enqueue(task);
  }

  @override
  Future<bool> pauseWithId(String taskId) {
    return _api.pauseWithId(taskId);
  }

  @override
  Future<bool> cancelWithId(String taskId) async {
    return _api.cancelWithId(taskId);
  }

  @override
  void onProgressUpdate(TaskProgress data) {
    processProgressUpdate(data);
  }

  @override
  void onStatusUpdate(TaskStatus data) {
    processStatusUpdate(data);
  }
}
