import 'package:file_flow/file_flow.dart';
import 'package:file_flow_example/utilities.dart';
import 'package:file_flow_example/widgets/selector_buttons.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

enum Picker { directory, file, files, mediaFile, mediaFiles, persisted }

Map<Picker, IconData> _pickerIcons = {
  Picker.directory: Icons.folder,
  Picker.file: Icons.insert_drive_file,
  Picker.files: Icons.insert_drive_file,
  Picker.mediaFile: Icons.image,
  Picker.mediaFiles: Icons.image,
  Picker.persisted: Icons.save,
};

Map<PickerMedia, IconData> _mediaIcons = {
  PickerMedia.image: Icons.image,
  PickerMedia.video: Icons.video_collection,
};

class PickerExample extends StatefulWidget {
  const PickerExample({super.key});

  @override
  State<PickerExample> createState() => _PickerExampleState();
}

class _PickerExampleState extends State<PickerExample> {
  final TextEditingController _directoryController = TextEditingController();
  final TextEditingController _mimeTypesController = TextEditingController();

  Picker _picker = Picker.directory;
  PickerDirectory? _pickerDirectory;
  dynamic _exact = false;
  bool _persist = false;
  PickerMedia _media = PickerMedia.image;
  String? _result;

  String? get _directory =>
      _directoryController.text.isEmpty ? null : _directoryController.text;

  String? get _mimeTypes =>
      _mimeTypesController.text.isEmpty ? null : _mimeTypesController.text;

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        SelectorButtons(
          values: Picker.values,
          selected: _picker,
          icons: _pickerIcons,
          onSelected: (value) => setState(() => _picker = value),
        ),
        if (!(_picker == Picker.mediaFile || _picker == Picker.mediaFiles)) ...[
          InputDecorator(
            decoration: const InputDecoration(
              labelText: 'Picker Directory',
              contentPadding: EdgeInsets.symmetric(horizontal: 16.0),
              border: OutlineInputBorder(),
            ),
            child: DropdownButtonHideUnderline(
              child: DropdownButton<PickerDirectory?>(
                isExpanded: true,
                value: _pickerDirectory,
                items: [
                  const DropdownMenuItem(
                    value: null,
                    child: Text('None'),
                  ),
                  for (final value in PickerDirectory.values)
                    DropdownMenuItem(
                      value: value,
                      child: Text(getNameFromEnum(value)),
                    ),
                ],
                onChanged: (value) => setState(() => _pickerDirectory = value),
              ),
            ),
          ),
          if (_pickerDirectory == null) ...[
            const SizedBox(height: 4.0),
            TextField(
              controller: _directoryController,
              decoration: const InputDecoration(labelText: 'Custom Directory'),
            ),
            const SizedBox(height: 8.0),
          ],
          if (_picker == Picker.directory) ...[
            const SizedBox(height: 16.0),
            InputDecorator(
              decoration: const InputDecoration(
                labelText: "Exact",
                contentPadding: EdgeInsets.symmetric(horizontal: 16.0),
                border: OutlineInputBorder(),
              ),
              child: DropdownButtonHideUnderline(
                child: DropdownButton<dynamic>(
                  isExpanded: true,
                  value: _exact,
                  items: const [
                    DropdownMenuItem(
                      value: false,
                      child: Text('False'),
                    ),
                    DropdownMenuItem(
                      value: true,
                      child: Text('True'),
                    ),
                    DropdownMenuItem(
                      value: "subdirectory",
                      child: Text('Subdirectory'),
                    ),
                  ],
                  onChanged: (value) => setState(() => _exact = value),
                ),
              ),
            ),
          ],
          if (_picker == Picker.file || _picker == Picker.files) ...[
            TextField(
              controller: _mimeTypesController,
              decoration: const InputDecoration(labelText: 'Mime Types'),
            ),
            const SizedBox(height: 24.0),
            InputDecorator(
              decoration: const InputDecoration(
                labelText: "Exact",
                contentPadding: EdgeInsets.symmetric(horizontal: 16.0),
                border: OutlineInputBorder(),
              ),
              child: DropdownButtonHideUnderline(
                child: DropdownButton<dynamic>(
                  isExpanded: true,
                  value: _exact,
                  items: const [
                    DropdownMenuItem(
                      value: false,
                      child: Text('False'),
                    ),
                    DropdownMenuItem(
                      value: true,
                      child: Text('True'),
                    ),
                  ],
                  onChanged: (value) => setState(() => _exact = value),
                ),
              ),
            ),
          ],
        ],
        if (_picker == Picker.mediaFile || _picker == Picker.mediaFiles) ...[
          const SizedBox(height: 8.0),
          Text("Media Type", style: Theme.of(context).textTheme.titleSmall),
          const SizedBox(height: 8.0),
          SelectorButtons(
            values: PickerMedia.values,
            icons: _mediaIcons,
            selected: _media,
            crossAxisCount: 2,
            onSelected: (value) => setState(() => _media = value),
          ),
        ],
        if (_picker != Picker.persisted)
          InkWell(
            onTap: () => setState(() => _persist = !_persist),
            highlightColor: Colors.transparent,
            splashFactory: NoSplash.splashFactory,
            child: Row(
              mainAxisSize: MainAxisSize.min,
              children: [
                const Text('Persist'),
                Checkbox(
                  value: _persist,
                  onChanged: (value) =>
                      setState(() => _persist = value ?? false),
                ),
              ],
            ),
          )
        else
          const SizedBox(height: 16.0),
        SizedBox(
          width: double.infinity,
          child: ElevatedButton(
            onPressed: _openPicker,
            child: Text(_picker == Picker.persisted ? "Check" : "Open Picker"),
          ),
        ),
        if (_result != null) ...[
          const SizedBox(height: 16.0),
          Text("Result: $_result"),
        ],
      ],
    );
  }

  void _openPicker() async {
    setState(() => _result = null);
    final directory = _directory ?? _pickerDirectory;
    final mimeTypes = _mimeTypes?.split(',');

    dynamic result;

    try {
      switch (_picker) {
        case Picker.directory:
          result = await FileFlow.picker.directory(
            directory: directory,
            exact: _exact,
            persist: _persist,
          );
          break;
        case Picker.file:
          result = await FileFlow.picker.file(
            directory: directory,
            mimeTypes: mimeTypes,
            exact: _exact,
            persist: _persist,
          );
          break;
        case Picker.files:
          result = await FileFlow.picker.files(
            directory: directory,
            mimeTypes: mimeTypes,
            exact: _exact,
            persist: _persist,
          );
          break;
        case Picker.mediaFile:
          result = await FileFlow.picker.mediaFile(
            media: _media,
            persist: _persist,
          );
          break;
        case Picker.mediaFiles:
          result = await FileFlow.picker.mediaFiles(
            media: _media,
            persist: _persist,
          );
          break;
        case Picker.persisted:
          if (directory != null) {
            final persisted = await FileFlow.picker.persisted(directory);
            showSnackBar("Uri persisted: $persisted");
          }
          break;
      }
    } on PlatformException catch (e) {
      showSnackBar("Error Code: ${e.code}");
    }

    if (result != null) {
      setState(() => _result = result.toString());
    }
  }
}
