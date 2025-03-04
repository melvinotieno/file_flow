import 'package:flutter/material.dart';

class SelectorButtons<T extends Enum> extends StatelessWidget {
  const SelectorButtons({
    super.key,
    required this.values,
    this.selected,
    this.onSelected,
    this.crossAxisCount = 3,
    this.icons,
  });

  final List<T> values;
  final T? selected;
  final ValueChanged<T>? onSelected;
  final int crossAxisCount;
  final Map<T, IconData>? icons;

  // @override
  // Widget build(BuildContext context) {
  //   return Wrap(
  //     spacing: 8.0,
  //     runSpacing: 8.0,
  //     children: values.map((value) {
  //       return SizedBox(
  //         width: MediaQuery.of(context).size.width / crossAxisCount - 16.0,
  //         child: _Button(
  //           value: value,
  //           isSelected: selected == value,
  //           onSelected: onSelected,
  //           icon: icons?[value],
  //         ),
  //       );
  //     }).toList(),
  //   );
  // }

  @override
  Widget build(BuildContext context) {
    final width = MediaQuery.of(context).size.width;

    return GridView.builder(
      shrinkWrap: true,
      physics: const NeverScrollableScrollPhysics(),
      gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
        crossAxisCount: crossAxisCount,
        crossAxisSpacing: 8.0,
        mainAxisSpacing: 16.0,
        childAspectRatio: (width / crossAxisCount) / 50.0,
      ),
      itemCount: values.length,
      itemBuilder: (context, index) {
        final value = values[index];
        return _Button(
          value: value,
          isSelected: selected == value,
          onSelected: onSelected,
        );
      },
    );
  }
}

class _Button<T extends Enum> extends StatelessWidget {
  const _Button({
    required this.value,
    required this.isSelected,
    required this.onSelected,
    this.icon,
  });

  final T value;
  final bool isSelected;
  final ValueChanged<T>? onSelected;
  final IconData? icon;

  String get _label {
    final camelCase = value.toString().split('.').last;
    final words = camelCase.split(RegExp(r'(?<=[a-z])(?=[A-Z])'));

    // Capitalize the first letter of the first word
    words[0] = words[0][0].toUpperCase() + words[0].substring(1);

    return words.join(' ');
  }

  @override
  Widget build(BuildContext context) {
    return TextButton(
      onPressed: () {
        if (!isSelected) onSelected?.call(value);
      },
      style: TextButton.styleFrom(
        textStyle: const TextStyle(
          fontSize: 12.0,
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
          const Icon(Icons.ac_unit, size: 14.0),
          const SizedBox(width: 8.0),
          Flexible(child: Text(_label)),
        ],
      ),
    );
  }
}
