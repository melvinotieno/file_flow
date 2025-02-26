import 'package:flutter/material.dart';

class UploadExample extends StatefulWidget {
  const UploadExample({super.key});

  @override
  State<UploadExample> createState() => _UploadState();
}

class _UploadState extends State<UploadExample> {
  @override
  Widget build(BuildContext context) {
    return const Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text('Upload Example'),
      ],
    );
  }
}
