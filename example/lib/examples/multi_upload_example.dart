import 'package:flutter/material.dart';

class MultiUploadExample extends StatefulWidget {
  const MultiUploadExample({super.key});

  @override
  State<MultiUploadExample> createState() => _MultiUploadState();
}

class _MultiUploadState extends State<MultiUploadExample> {
  @override
  Widget build(BuildContext context) {
    return const Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text('Multi Upload Example'),
      ],
    );
  }
}
