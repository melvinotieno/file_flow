import 'package:pigeon/pigeon.dart';

/// Represents the base directory for picker operations.
enum PickerDirectory { downloads, images, video, audio, files }

@HostApi()
abstract class PickerHostApi {
  /// Checks if the specified URI use permission has been persisted.
  ///
  /// Parameters:
  /// - [uri]: The URI to check.
  bool persisted(String uri);

  /// Allows the user to select a directory from the file system.
  ///
  /// Parameters:
  /// - [directory]: The base directory to start the picker from. This can
  /// either be a [PickerDirectory] or a [String] representing a directory URI.
  /// - [persist]: Whether the selected directory should be persisted.
  ///
  /// Returns the URI of the selected directory as a [String].
  @async
  String pickDirectory([Object? directory, bool persist = false]);

  /// Allows the user to select a file from the file system.
  ///
  /// Parameters:
  /// - [directory]: The base directory to start the picker from. This can
  /// either be a [PickerDirectory] or a [String] representing a directory URI.
  /// - [extensions]: A list of file extensions to filter the picker by.
  /// - [persist]: Whether the selected file should be persisted.
  ///
  /// Returns the URI of the selected file as a [String].
  @async
  String pickFile([
    Object? directory,
    List<String>? extensions,
    bool persist = false,
  ]);

  /// Allows the user to select multiple files from the file system.
  ///
  /// Parameters:
  /// - [directory]: The base directory to start the picker from. This can
  /// either be a [PickerDirectory] or a [String] representing a directory URI.
  /// - [extensions]: A list of file extensions to filter the picker by.
  /// - [persist]: Whether the selected files should be persisted.
  ///
  /// Returns the URIs of the selected files as a [List] of [String]s.
  @async
  List<String> pickFiles([
    Object? directory,
    List<String>? extensions,
    bool persist = false,
  ]);
}
