import 'package:file_flow/file_flow.dart';
import 'package:file_flow_example/utilities.dart';
import 'package:flutter/material.dart';

class TaskUpdate extends StatelessWidget {
  const TaskUpdate({
    super.key,
    this.taskState,
    this.progress = 0,
    this.progressData,
    this.completeData,
    this.exception,
    this.onClear,
  });

  final TaskState? taskState;
  final int progress;
  final TaskProgressData? progressData;
  final TaskCompleteData? completeData;
  final TaskException? exception;
  final Function()? onClear;

  @override
  Widget build(BuildContext context) {
    if (taskState == null) return const SizedBox();

    return Container(
      width: double.infinity,
      padding: const EdgeInsets.symmetric(
        vertical: 8.0,
        horizontal: 16.0,
      ),
      margin: const EdgeInsets.only(top: 8.0, bottom: 16.0),
      decoration: BoxDecoration(
        border: Border.all(color: Colors.grey),
        borderRadius: BorderRadius.circular(4.0),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text('Task State: ${getNameFromEnum(taskState!)}'),
          const SizedBox(height: 8.0),
          Text('Progress: $progress%'),
          if (progressData != null) ...[
            const SizedBox(height: 8.0),
            Text('Expected: ${formatBytes(progressData!.expectedBytes)}'),
            Text('Downloaded: ${formatBytes(progressData!.transferredBytes)}'),
            Text('Speed: ${formatBytes(progressData!.networkSpeed)}/s'),
          ],
          if (completeData != null) ...[
            const SizedBox(height: 8.0),
            Text('Path: ${completeData!.path}'),
            Text('Mime: ${completeData!.mimeType}'),
            Text('Response: ${completeData!.rawResponse}'),
          ],
          if (exception != null) ...[
            const SizedBox(height: 8.0),
            Text('Code: ${exception!.code.name}'),
            Text('Exception: ${exception!.message}'),
            Text('Response: ${completeData!.rawResponse}'),
          ],
          if (_showClearButton) ...[
            const SizedBox(height: 8.0),
            TextButton(
              onPressed: onClear,
              style: TextButton.styleFrom(
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(4.0),
                  side: const BorderSide(color: Colors.red),
                ),
              ),
              child: const Text('Reset', style: TextStyle(color: Colors.red)),
            ),
          ],
        ],
      ),
    );
  }

  bool get _showClearButton =>
      taskState == TaskState.completed ||
      taskState == TaskState.failed ||
      taskState == TaskState.canceled;
}
