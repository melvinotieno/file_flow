import 'package:file_flow_example/examples/download_example.dart';
import 'package:file_flow_example/examples/multi_upload_example.dart';
import 'package:file_flow_example/examples/parallel_download_example.dart';
import 'package:file_flow_example/examples/picker_example.dart';
import 'package:file_flow_example/examples/upload_example.dart';
import 'package:file_flow_example/screens/downloads.dart';
import 'package:file_flow_example/screens/settings.dart';
import 'package:file_flow_example/utilities.dart';
import 'package:file_flow_example/widgets/example_selector.dart';
import 'package:flutter/material.dart';

class Home extends StatefulWidget {
  const Home({super.key});

  @override
  State<Home> createState() => _HomeState();
}

class _HomeState extends State<Home> {
  Example _selectedExample = Example.download;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('FileFlow Example'),
        actions: [
          IconButton(
            icon: const Icon(Icons.download),
            onPressed: () => navigate(const Downloads()),
          ),
          IconButton(
            icon: const Icon(Icons.settings),
            onPressed: () => navigate(const Settings()),
          ),
        ],
      ),
      body: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Padding(
            padding: const EdgeInsets.only(left: 16.0, bottom: 4.0),
            child: Text(
              'Example',
              style: Theme.of(context).textTheme.titleMedium,
            ),
          ),
          ExampleSelector(
            selectedExample: _selectedExample,
            onExampleSelected: (example) =>
                setState(() => _selectedExample = example),
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
    switch (_selectedExample) {
      case Example.download:
        return const DownloadExample();
      case Example.upload:
        return const UploadExample();
      case Example.multiUpload:
        return const MultiUploadExample();
      case Example.parallelDownload:
        return const ParallelDownloadExample();
      case Example.picker:
        return const PickerExample();
    }
  }
}
