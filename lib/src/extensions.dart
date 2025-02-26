import 'pigeons/flow.g.dart';

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
