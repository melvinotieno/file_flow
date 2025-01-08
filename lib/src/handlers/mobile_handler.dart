import 'package:file_flow/pigeons/flow.g.dart';

import '../models.dart';
import 'base_handler.dart';

final class MobileHandler extends BaseHandler {
  static final _api = FileFlowHostApi();

  @override
  Future<bool> enqueue(FlowTask task) async {
    return await _api.enqueue(task.toPlatformTask());
  }
}
