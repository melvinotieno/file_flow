import 'package:file_flow/file_flow.dart';
import 'package:file_flow_example/widgets/selector_buttons.dart';
import 'package:flutter/material.dart';

class DirectoryStorage extends StatelessWidget {
  DirectoryStorage({
    super.key,
    required this.baseDirectory,
    required this.onBaseDirectoryChanged,
    required this.directoryController,
    required this.filenameController,
  }) {
    _initializeStorageLists();
  }

  /// The selected base directory.
  final StorageDirectory baseDirectory;

  /// Callback for when the base directory is selected.
  final ValueChanged<StorageDirectory> onBaseDirectoryChanged;

  /// Controller for the directory text field.
  final TextEditingController directoryController;

  /// Controller for the filename text field.
  final TextEditingController filenameController;

  late final List<StorageDirectory> _sharedStorage;
  late final List<StorageDirectory> _privateStorage;

  void _initializeStorageLists() {
    _sharedStorage = StorageDirectory.values
        .where((element) => element.isSharedStorage)
        .toList();
    _privateStorage = StorageDirectory.values
        .where((element) => !element.isSharedStorage)
        .toList();
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text('Storage', style: theme.textTheme.titleMedium),
        SelectorButtons(
          values: _privateStorage,
          selected: baseDirectory,
          onSelected: onBaseDirectoryChanged,
          crossAxisCount: 2,
        ),
        Text('Shared Storage', style: theme.textTheme.titleSmall),
        SelectorButtons(
          values: _sharedStorage,
          selected: baseDirectory,
          onSelected: onBaseDirectoryChanged,
        ),
        Row(
          children: [
            Expanded(
              child: TextField(
                controller: directoryController,
                decoration: const InputDecoration(labelText: 'Directory'),
              ),
            ),
            const SizedBox(width: 16.0),
            Expanded(
              child: TextField(
                controller: filenameController,
                decoration: const InputDecoration(labelText: 'File Name'),
              ),
            ),
          ],
        ),
      ],
    );
  }
}
