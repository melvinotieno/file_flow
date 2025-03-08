import 'package:file_flow/src/pigeons/picker.g.dart';

/// Provides methods to pick directories and files.
class Picker {
  static final _api = PickerHostApi();

  /// Checks if the specified value has `use permission` persisted.
  ///
  /// Parameters:
  /// - [value]: This can either be a [PickerDirectory] or a [String] that
  /// represents a directory URI.
  ///
  /// Returns `true` permission has been persisted, `false` otherwise.
  Future<bool> persisted(dynamic value) async {
    if (!(value is String || value is PickerDirectory)) {
      throw ArgumentError('value must be a String or PickerDirectory');
    }

    return _api.persisted(value);
  }

  /// Allows the user to select a directory from the file system.
  ///
  /// Parameters:
  /// - [directory]: The base directory to start the picker from. This can
  /// either be a [PickerDirectory] or a [String] representing a directory URI.
  /// - [exact]: Allow the exact directory or any subdirectory to be picked.
  /// The values allowed are [true], [false] or ['subdirectory'].
  /// - [persist]: Whether the selected directory should be persisted.
  ///
  /// Returns the URI of the selected directory.
  ///
  /// Throws an [ArgumentError] if the directory provided is not null, a String,
  /// or a PickerDirectory.
  ///
  /// Throws an [ArgumentError] if exact is neither a bool nor 'subdirectory'.
  Future<Uri> directory({
    dynamic directory,
    dynamic exact = false,
    bool persist = false,
  }) async {
    if (directory != null &&
        !(directory is String || directory is PickerDirectory)) {
      throw ArgumentError(
        'directory must be a String, PickerDirectory, or null',
      );
    }

    if (!(exact is bool || exact == 'subdirectory')) {
      throw ArgumentError('exact must be a bool or "subdirectory"');
    }

    return Uri.parse(await _api.pickDirectory(directory, exact, persist));
  }

  /// Allows the user to select a file from the file system.
  ///
  /// Parameters:
  /// - [directory]: The base directory to start the picker from. This can
  /// either be a [PickerDirectory] or a [String] representing a directory URI.
  /// - [mimeTypes]: A list of file extensions to filter the picker by.
  /// - [exact]: Whether the file must be within the directory given.
  /// - [persist]: Whether the selected file should be persisted.
  ///
  /// Returns the URI of the selected file.
  ///
  /// Throws an [ArgumentError] if the directory provided is neither a String
  /// nor a PickerDirectory. The directory can be null.
  Future<Uri> file({
    dynamic directory,
    List<String>? mimeTypes,
    bool exact = false,
    bool persist = false,
  }) async {
    if (directory != null &&
        !(directory is String || directory is PickerDirectory)) {
      throw ArgumentError(
        'directory must be a String, PickerDirectory, or null',
      );
    }

    return Uri.parse(await _api.pickFile(directory, mimeTypes, exact, persist));
  }

  /// Allows the user to select multiple files from the file system.
  ///
  /// Parameters:
  /// - [directory]: The base directory to start the picker from. This can
  /// either be a [PickerDirectory] or a [String] representing a directory URI.
  /// - [mimeTypes]: A list of file extensions to filter the picker by.
  /// - [exact]: Whether the files must be within the directory given.
  /// - [persist]: Whether the selected files should be persisted.
  ///
  /// Returns the URIs of the selected files.
  ///
  /// Throws an [ArgumentError] if the directory provided is neither a String
  /// nor a PickerDirectory. The directory can be null.
  Future<List<Uri>> files({
    dynamic directory,
    List<String>? mimeTypes,
    bool exact = false,
    bool persist = false,
  }) async {
    if (directory != null &&
        !(directory is String || directory is PickerDirectory)) {
      throw ArgumentError(
        'directory must be a String, PickerDirectory, or null',
      );
    }

    final paths = await _api.pickFiles(directory, mimeTypes, exact, persist);
    return paths.map((path) => Uri.parse(path)).toList();
  }

  /// Allows the user to select a media file (image or video) from the file
  /// system.
  ///
  /// Parameters:
  /// - [media]: The type of media to pick.
  /// - [persist]: Whether the selected media file should be persisted.
  ///
  /// Returns the URI of the selected media file.
  Future<Uri> mediaFile({
    PickerMedia media = PickerMedia.image,
    bool persist = false,
  }) async {
    return Uri.parse(await _api.pickMediaFile(media, persist));
  }

  /// Allows the user to select multiple media files (images or videos) from the
  /// file system.
  ///
  /// Parameters:
  /// - [media]: The type of media to pick.
  /// - [persist]: Whether the selected media files should be persisted.
  ///
  /// Returns the URIs of the selected media files.
  Future<List<Uri>> mediaFiles({
    PickerMedia media = PickerMedia.image,
    bool persist = false,
  }) async {
    final paths = await _api.pickMediaFiles(media, persist);
    return paths.map((path) => Uri.parse(path)).toList();
  }
}
