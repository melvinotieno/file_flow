import 'package:file_flow/src/pigeons/picker.g.dart';

/// Provides methods to pick directories and files.
class Picker {
  static final _api = PickerHostApi();

  /// Checks if the specified URI use permission has been persisted.
  ///
  /// Parameters:
  /// - [uri]: The URI to check.
  ///
  /// Returns `true` if the URI use permission has been persisted, `false`
  /// otherwise.
  Future<bool> persisted(Uri uri) async {
    return _api.persisted(uri.toString());
  }

  /// Allows the user to select a directory from the file system.
  ///
  /// Parameters:
  /// - [directory]: The base directory to start the picker from. This can
  /// either be a [PickerDirectory] or a [String] representing a directory URI.
  /// - [persist]: Whether the selected directory should be persisted.
  ///
  /// Returns the URI of the selected directory.
  ///
  /// Throws an [ArgumentError] if the directory provided is neither a String
  /// nor a PickerDirectory. The directory can be null.
  Future<Uri> directory({dynamic directory, bool persist = false}) async {
    if (directory != null &&
        !(directory is String || directory is PickerDirectory)) {
      throw ArgumentError(
        'directory must be a String, PickerDirectory, or null',
      );
    }

    return Uri.parse(await _api.pickDirectory(directory, persist));
  }

  /// Allows the user to select a file from the file system.
  ///
  /// Parameters:
  /// - [directory]: The base directory to start the picker from. This can
  /// either be a [PickerDirectory] or a [String] representing a directory URI.
  /// - [extensions]: A list of file extensions to filter the picker by.
  /// - [persist]: Whether the selected file should be persisted.
  ///
  /// Returns the URI of the selected file.
  ///
  /// Throws an [ArgumentError] if the directory provided is neither a String
  /// nor a PickerDirectory. The directory can be null.
  Future<Uri> file({
    dynamic directory,
    List<String>? extensions,
    bool persist = false,
  }) async {
    if (directory != null &&
        !(directory is String || directory is PickerDirectory)) {
      throw ArgumentError(
        'directory must be a String, PickerDirectory, or null',
      );
    }

    return Uri.parse(await _api.pickFile());
  }

  /// Allows the user to select multiple files from the file system.
  ///
  /// Parameters:
  /// - [directory]: The base directory to start the picker from. This can
  /// either be a [PickerDirectory] or a [String] representing a directory URI.
  /// - [extensions]: A list of file extensions to filter the picker by.
  /// - [persist]: Whether the selected files should be persisted.
  ///
  /// Returns the URIs of the selected files.
  ///
  /// Throws an [ArgumentError] if the directory provided is neither a String
  /// nor a PickerDirectory. The directory can be null.
  Future<List<Uri>> files({
    dynamic directory,
    List<String>? extensions,
    bool persist = false,
  }) async {
    if (directory != null &&
        !(directory is String || directory is PickerDirectory)) {
      throw ArgumentError(
        'directory must be a String, PickerDirectory, or null',
      );
    }

    final filePaths = await _api.pickFiles();
    return filePaths.map((path) => Uri.parse(path)).toList();
  }
}
