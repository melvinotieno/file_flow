import 'package:file_flow_example/utilities.dart';
import 'package:flutter/material.dart';

enum Example {
  download,

  upload,

  multiUpload,

  parallelDownload,

  picker;

  IconData get icon {
    switch (this) {
      case Example.download:
        return Icons.download;
      case Example.upload:
        return Icons.upload;
      case Example.multiUpload:
        return Icons.upload_file;
      case Example.parallelDownload:
        return Icons.download_for_offline;
      case Example.picker:
        return Icons.file_copy;
    }
  }
}

class ExampleSelector extends StatelessWidget {
  final List<Example> examples = [
    Example.download,
    Example.upload,
    Example.multiUpload,
    Example.parallelDownload,
    Example.picker,
  ];

  ExampleSelector({
    super.key,
    required this.selectedExample,
    required this.onExampleSelected,
  });

  final Example selectedExample;
  final ValueChanged<Example> onExampleSelected;

  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
      scrollDirection: Axis.horizontal,
      padding: const EdgeInsets.symmetric(horizontal: 16.0),
      child: Row(
        children: [
          for (final example in examples) ...[
            _SelectorButton(
              example: example,
              isSelected: example == selectedExample,
              onSelected: onExampleSelected,
            ),
            if (example != examples.last) const SizedBox(width: 16.0),
          ]
        ],
      ),
    );
  }
}

class _SelectorButton extends StatelessWidget {
  const _SelectorButton({
    required this.example,
    required this.isSelected,
    required this.onSelected,
  });

  final Example example;
  final bool isSelected;
  final ValueChanged<Example> onSelected;

  @override
  Widget build(BuildContext context) {
    return TextButton.icon(
      onPressed: () {
        if (!isSelected) onSelected(example);
      },
      label: Text(getNameFromEnum(example)),
      icon: Icon(example.icon),
      style: TextButton.styleFrom(
        minimumSize: const Size(150.0, 50.0),
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(8.0),
          side: BorderSide(
            color: isSelected ? Colors.blue : Colors.grey,
          ),
        ),
      ),
    );
  }
}
