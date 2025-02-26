import 'package:flutter/material.dart';

class ParallelDownloadExample extends StatefulWidget {
  const ParallelDownloadExample({super.key});

  @override
  State<ParallelDownloadExample> createState() => _ParallelDownloadState();
}

class _ParallelDownloadState extends State<ParallelDownloadExample> {
  @override
  Widget build(BuildContext context) {
    return const Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text('Parallel Download Example'),
      ],
    );
  }
}
