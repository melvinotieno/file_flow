import '../models.dart';
import 'base_handler.dart';

final class DesktopHandler extends BaseHandler {
  @override
  Future<bool> enqueue(FlowTask task) {
    return Future.value(true);
  }
}
