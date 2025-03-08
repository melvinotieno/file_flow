import 'package:pigeon/pigeon.dart';

/// The base directory to start a picker from.
enum PickerDirectory { documents, downloads, images, video, audio }

/// The type of media to pick when using a media picker.
enum PickerMedia { image, video }

@HostApi()
abstract class PickerHostApi {
  /// Checks if the specified URI has been persisted.
  ///
  /// Parameters:
  /// - [uri]: The URI to check.
  ///
  /// Returns `true` if the URI has been persisted, `false` otherwise.
  bool persisted(Object uri);

  /// Allows the user to select a directory from the file system.
  ///
  /// Parameters:
  /// - [directory]: The base directory to start the picker from.
  /// - [exact]: Allow the exact directory or any subdirectory to be picked.
  /// - [persist]: Whether the selected directory should be persisted.
  ///
  /// Returns the URI of the selected directory as a [String].
  @async
  String pickDirectory([
    Object? directory,
    Object exact = false,
    bool persist = false,
  ]);

  /// Allows the user to select a file from the file system.
  ///
  /// Parameters:
  /// - [directory]: The base directory to start the picker from.
  /// - [mimeTypes]: A list of file mime types to filter the picker by.
  /// - [exact]: File must be within the base directory given.
  /// - [persist]: Whether the selected file should be persisted.
  ///
  /// Returns the URI of the selected file as a [String].
  @async
  String pickFile([
    Object? directory,
    List<String>? mimeTypes,
    bool exact = false,
    bool persist = false,
  ]);

  /// Allows the user to select multiple files from the file system.
  ///
  /// Parameters:
  /// - [directory]: The base directory to start the picker from.
  /// - [mimeTypes]: A list of file mime types to filter the picker by.
  /// - [exact]: Files must be within the base directory given.
  /// - [persist]: Whether the selected files should be persisted.
  ///
  /// Returns the URIs of the selected files as a [List] of [String]s.
  @async
  List<String> pickFiles([
    Object? directory,
    List<String>? mimeTypes,
    bool exact = false,
    bool persist = false,
  ]);

  /// Allows the user to select a media file (image or video) from the file
  /// system. By default, an image is picked.
  ///
  /// Parameters:
  /// - [media]: The type of media to pick.
  /// - [persist]: Whether the selected media file should be persisted.
  ///
  /// Returns the URI of the selected media file as a [String].
  @async
  String pickMediaFile([
    PickerMedia media = PickerMedia.image,
    bool persist = false,
  ]);

  /// Allows the user to select multiple media files (images or videos) from the
  /// file system. By default, images are picked.
  ///
  /// Parameters:
  /// - [media]: The type of media to pick.
  /// - [persist]: Whether the selected media files should be persisted.
  ///
  /// Returns the URIs of the selected media files as a [List] of [String]s.
  @async
  List<String> pickMediaFiles([
    PickerMedia media = PickerMedia.image,
    bool persist = false,
  ]);
}
