import 'package:file_flow/src/models.dart';

import 'base_handler.dart';

final class IsolateHandler extends BaseHandler {
  @override
  Future<bool> enqueue(FlowTask task) {
    throw UnimplementedError();
  }
}
