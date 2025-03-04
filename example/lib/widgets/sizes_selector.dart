import 'package:flutter/material.dart';

class SizesSelector extends StatelessWidget {
  const SizesSelector({
    super.key,
    required this.sizes,
    required this.selected,
    this.onSelected,
    this.textController,
  });

  final List<String> sizes;
  final String selected;
  final ValueChanged<String>? onSelected;
  final TextEditingController? textController;

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          'Select file size',
          style: Theme.of(context).textTheme.titleMedium,
        ),
        Wrap(
          spacing: 8.0,
          runSpacing: 8.0,
          children: sizes.map((size) {
            return SizedBox(
              width: MediaQuery.of(context).size.width / 3 - 16.0,
              child: _Button(
                size: size,
                isSelected: selected == size,
                onSelected: (size) => onSelected?.call(size),
              ),
            );
          }).toList(),
        ),
        if (selected == 'Custom')
          Padding(
            padding: const EdgeInsets.only(top: 12.0),
            child: TextField(
              controller: textController,
              decoration: const InputDecoration(
                hintText: 'Enter custom url',
                border: OutlineInputBorder(),
              ),
            ),
          ),
        const SizedBox(height: 16.0),
      ],
    );
  }
}

class _Button extends StatelessWidget {
  const _Button({
    required this.size,
    required this.isSelected,
    required this.onSelected,
  });

  final String size;
  final bool isSelected;
  final ValueChanged<String>? onSelected;

  @override
  Widget build(BuildContext context) {
    return TextButton(
      onPressed: () {
        if (!isSelected) onSelected?.call(size);
      },
      style: TextButton.styleFrom(
        textStyle: const TextStyle(
          fontWeight: FontWeight.w500,
        ),
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(8.0),
          side: BorderSide(
            color: isSelected ? Colors.blue : Colors.grey,
          ),
        ),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          const Icon(Icons.file_present),
          const SizedBox(width: 8.0),
          Flexible(child: Text(size)),
        ],
      ),
    );
  }
}
