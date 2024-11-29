
import 'file_flow_platform_interface.dart';

class FileFlow {
  Future<String?> getPlatformVersion() {
    return FileFlowPlatform.instance.getPlatformVersion();
  }
}
