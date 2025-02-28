import 'package:file_flow/file_flow.dart';
import 'package:file_flow_example/utilities.dart';
import 'package:flutter/material.dart';

class DirectoryStorage extends StatelessWidget {
  const DirectoryStorage({
    super.key,
    required this.usePicker,
    required this.onUsePicker,
    required this.baseDirectory,
    required this.onBaseDirectoryChanged,
    required this.directoryController,
    required this.filenameController,
  });

  final bool usePicker;
  final ValueChanged<bool?> onUsePicker;
  final StorageDirectory? baseDirectory;
  final ValueChanged<StorageDirectory?> onBaseDirectoryChanged;
  final TextEditingController directoryController;
  final TextEditingController filenameController;

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text('Storage', style: Theme.of(context).textTheme.titleMedium),
        const SizedBox(height: 8.0),
        InputDecorator(
          decoration: const InputDecoration(
            labelText: 'Base Directory',
            contentPadding: EdgeInsets.symmetric(horizontal: 16.0),
            border: OutlineInputBorder(),
          ),
          child: DropdownButtonHideUnderline(
            child: DropdownButton<StorageDirectory?>(
              isExpanded: true,
              value: baseDirectory,
              items: [
                const DropdownMenuItem(
                  value: null,
                  child: Text('None'),
                ),
                for (final value in StorageDirectory.values)
                  DropdownMenuItem(
                    value: value,
                    child: Text(getNameFromEnum(value)),
                  ),
              ],
              onChanged: onBaseDirectoryChanged,
            ),
          ),
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
        InkWell(
          onTap: () => onUsePicker(!usePicker),
          highlightColor: Colors.transparent,
          splashFactory: NoSplash.splashFactory,
          child: Row(
            mainAxisSize: MainAxisSize.min,
            children: [
              const Text('Use Picker'),
              Checkbox(
                value: usePicker,
                onChanged: onUsePicker,
              ),
            ],
          ),
        ),
      ],
    );
  }
}
