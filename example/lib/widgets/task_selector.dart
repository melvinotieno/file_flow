import 'package:file_flow_example/utilities.dart';
import 'package:flutter/material.dart';

enum TaskType {
  download,

  upload,

  parallelDownload,

  multiUpload;

  IconData get icon {
    switch (this) {
      case TaskType.download:
        return Icons.download;
      case TaskType.upload:
        return Icons.upload;
      case TaskType.parallelDownload:
        return Icons.download_for_offline;
      case TaskType.multiUpload:
        return Icons.upload_file;
    }
  }
}

class TaskSelector extends StatelessWidget {
  final List<TaskType> tasks = [
    TaskType.download,
    TaskType.upload,
    TaskType.parallelDownload,
    TaskType.multiUpload
  ];

  TaskSelector({
    super.key,
    required this.selectedTask,
    required this.onTaskSelected,
  });

  final TaskType selectedTask;
  final ValueChanged<TaskType> onTaskSelected;

  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
      scrollDirection: Axis.horizontal,
      padding: const EdgeInsets.symmetric(horizontal: 16.0),
      child: Row(
        children: [
          for (final task in tasks) ...[
            _SelectorButton(
              task: task,
              isSelected: task == selectedTask,
              onSelected: onTaskSelected,
            ),
            if (task != tasks.last) const SizedBox(width: 16.0),
          ]
        ],
      ),
    );
  }
}

class _SelectorButton extends StatelessWidget {
  const _SelectorButton({
    required this.task,
    required this.isSelected,
    required this.onSelected,
  });

  final TaskType task;
  final bool isSelected;
  final ValueChanged<TaskType> onSelected;

  @override
  Widget build(BuildContext context) {
    return TextButton.icon(
      onPressed: () {
        if (!isSelected) onSelected(task);
      },
      label: Text(getNameFromEnum(task)),
      icon: Icon(task.icon),
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
