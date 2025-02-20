import 'package:file_flow_example/examples/download_example.dart';
import 'package:flutter/material.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('FileFlow Example'),
          actions: [
            IconButton(icon: const Icon(Icons.settings), onPressed: () {}),
          ],
        ),
        body: const Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Expanded(
              child: SingleChildScrollView(
                padding: EdgeInsets.all(16.0),
                child: DownloadExample(),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
