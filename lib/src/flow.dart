import 'package:file_flow/pigeons/flow.g.dart';

import 'config.dart';
import 'models.dart';
import 'operation.dart';

class FileFlow {
  FileFlow._();

  static FileFlowConfig _config = FileFlowConfig();

  static FileFlowConfig get config => _config;

  static void configure({
    RequestConfig? request,
    StorageDirectory? baseDirectory,
    String? defaultGroup,
  }) {
    _config = FileFlowConfig(
      request: request ?? _config.request, // TODO: merge request config
      baseDirectory: baseDirectory ?? _config.baseDirectory,
      defaultGroup: defaultGroup ?? _config.defaultGroup,
    );
  }

  static TaskOperation download(DownloadTask task) {
    return FileOperation().download(task);
  }

  static BatchTaskOperation downloadBatch(List<DownloadTask> tasks) {
    return FileOperation().downloadBatch(tasks);
  }

  static TaskOperation upload(UploadTask task) {
    return FileOperation().upload(task);
  }

  static BatchTaskOperation uploadBatch(List<UploadTask> tasks) {
    return FileOperation().uploadBatch(tasks);
  }
}
