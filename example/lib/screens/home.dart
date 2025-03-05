import 'package:file_flow_example/examples/download_example.dart';
import 'package:file_flow_example/examples/multi_upload_example.dart';
import 'package:file_flow_example/examples/parallel_download_example.dart';
import 'package:file_flow_example/examples/upload_example.dart';
import 'package:file_flow_example/screens/downloads.dart';
import 'package:file_flow_example/screens/settings.dart';
import 'package:file_flow_example/widgets/task_selector.dart';
import 'package:flutter/material.dart';

class Home extends StatefulWidget {
  const Home({super.key});

  @override
  State<Home> createState() => _HomeState();
}

class _HomeState extends State<Home> {
  TaskType _selectedTask = TaskType.download;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('FileFlow Example'),
        actions: [
          IconButton(
            icon: const Icon(Icons.download),
            onPressed: () => Navigator.of(context).push(
              MaterialPageRoute(builder: (context) => const Downloads()),
            ),
          ),
          IconButton(
            icon: const Icon(Icons.settings),
            onPressed: () => Navigator.of(context).push(
              MaterialPageRoute(builder: (context) => const Settings()),
            ),
          ),
        ],
      ),
      body: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Padding(
            padding: const EdgeInsets.only(left: 16.0, bottom: 4.0),
            child: Text('Task', style: Theme.of(context).textTheme.titleMedium),
          ),
          TaskSelector(
            selectedTask: _selectedTask,
            onTaskSelected: (task) => setState(() => _selectedTask = task),
          ),
          const SizedBox(height: 8.0),
          Expanded(
            child: SingleChildScrollView(
              padding: const EdgeInsets.symmetric(
                vertical: 8.0,
                horizontal: 16.0,
              ),
              child: _buildExample(),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildExample() {
    switch (_selectedTask) {
      case TaskType.download:
        return const DownloadExample();
      case TaskType.upload:
        return const UploadExample();
      case TaskType.parallelDownload:
        return const ParallelDownloadExample();
      case TaskType.multiUpload:
        return const MultiUploadExample();
    }
  }
}
