import 'pigeons/flow.g.dart';

/// Extensions on `StorageDirectory` enum.
extension StorageDirectoryExtensions on StorageDirectory {
  /// Returns `true` if the `StorageDirectory` is a shared storage directory,
  /// otherwise returns `false`.
  bool get isSharedStorage {
    switch (this) {
      case StorageDirectory.applicationCache:
      case StorageDirectory.applicationDocuments:
      case StorageDirectory.applicationLibrary:
      case StorageDirectory.applicationSupport:
      case StorageDirectory.temporary:
        return false;
      case StorageDirectory.downloads:
      case StorageDirectory.images:
      case StorageDirectory.video:
      case StorageDirectory.audio:
      case StorageDirectory.files:
        return true;
    }
  }
}

/// Extensions on `TaskState` enum.
extension TaskStateExtensions on TaskState {
  /// Returns `true` if the `TaskState` is in a terminal state (no other state
  /// is expected), otherwise returns `false`.
  bool get isFinalState {
    switch (this) {
      case TaskState.completed:
      case TaskState.canceled:
      case TaskState.failed:
        return true;
      case TaskState.pending:
      case TaskState.running:
      case TaskState.paused:
        return false;
    }
  }
}
