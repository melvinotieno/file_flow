import 'package:file_flow/file_flow.dart';
import 'package:file_flow_example/widgets/directory_storage.dart';
import 'package:file_flow_example/widgets/sizes_selector.dart';
import 'package:flutter/material.dart';

class DownloadExample extends StatefulWidget {
  const DownloadExample({super.key});

  @override
  State<DownloadExample> createState() => _DownloadState();
}

class _DownloadState extends State<DownloadExample> {
  final TextEditingController _directoryController = TextEditingController();
  final TextEditingController _filenameController = TextEditingController();
  final TextEditingController _linkController = TextEditingController();

  bool _usePicker = false;
  String _selectedSize = '15 MB';
  StorageDirectory? _baseDirectory;
  bool _enqueue = false;
  bool _isDownloading = false;
  DownloadTask? _task;

  String? get _directory =>
      _directoryController.text.isEmpty ? null : _directoryController.text;

  String? get _filename =>
      _filenameController.text.isEmpty ? null : _filenameController.text;

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        SizesSelector(
          textController: _linkController,
          sizes: const [
            '15 MB',
            '30 MB',
            '70 MB',
            '150 MB',
            '300 MB',
            'Custom',
          ],
          selected: _selectedSize,
          onSelected: (value) {
            setState(() => _selectedSize = value);
          },
        ),
        DirectoryStorage(
          usePicker: _usePicker,
          onUsePicker: (value) => {
            setState(() => _usePicker = value ?? false),
          },
          onBaseDirectoryChanged: (value) => {
            setState(() => _baseDirectory = value),
          },
          baseDirectory: _baseDirectory,
          directoryController: _directoryController,
          filenameController: _filenameController,
        ),
        Row(
          children: [
            Expanded(
              child: ElevatedButton(
                onPressed: _onDownload,
                child: Text(_enqueue ? 'Enqueue' : 'Download'),
              ),
            ),
            if (_isDownloading) ...[
              const SizedBox(width: 16.0),
              IconButton(
                onPressed: _onPause,
                icon: const Icon(Icons.pause),
              ),
              IconButton(
                onPressed: _onCancel,
                icon: const Icon(Icons.cancel),
              ),
            ],
          ],
        ),
        Row(
          children: [
            const Spacer(),
            InkWell(
              onTap: () => setState(() => _enqueue = !_enqueue),
              highlightColor: Colors.transparent,
              splashFactory: NoSplash.splashFactory,
              child: Row(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Checkbox(
                    value: _enqueue,
                    onChanged: (value) {
                      setState(() => _enqueue = value ?? false);
                    },
                  ),
                  const Text('Enqueue Task'),
                ],
              ),
            ),
          ],
        ),
      ],
    );
  }

  void _onDownload() async {
    if (_task != null) return; // There already exists a task we are monitoring

    final url = _selectedSize == 'Custom'
        ? _linkController.text
        : 'https://link.testfile.org/${_selectedSize.replaceAll(' ', '')}';

    if (url.isEmpty) return; // URL is empty

    DownloadTask task;

    if (_usePicker) {
      task = await DownloadTask.withDirectoryPicker(
        url: url,
        filename: _filename,
      );
    } else {
      task = DownloadTask(
        url: url,
        baseDirectory: _baseDirectory,
        directory: _directory,
        filename: _filename,
      );
    }

    if (_enqueue) {
      // if (await task.enqueue()) {
      //   // task enqueued successfully
      // }
    } else {
      _task = task;
      await _task?.start(
        onStateChanged: onTaskStateChanged,
        onProgressUpdate: onTaskProgressUpdate,
      );
    }
  }

  void _onPause() async {
    // if (await _task?.pause() ?? false) {
    //   setState(() => _isDownloading = false);
    // }
  }

  void _onCancel() async {
    // if (await _task?.cancel() ?? false) {
    //   setState(() => _isDownloading = false);
    //   _task = null;
    // }
  }

  void onTaskStateChanged(TaskState state) {
    switch (state) {
      case TaskState.pending:
        break;
      case TaskState.running:
        setState(() {
          _isDownloading = true;
        });
        break;
      case TaskState.paused:
      case TaskState.completed:
      case TaskState.failed:
      case TaskState.canceled:
        setState(() {
          _isDownloading = false;
        });
        break;
    }
  }

  void onTaskProgressUpdate(int progress, TaskProgressData data) {
    print('Progress: $progress');
  }
}
