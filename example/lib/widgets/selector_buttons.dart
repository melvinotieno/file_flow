import 'package:file_flow_example/utilities.dart';
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
          icon: icons?[value],
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
          if (icon != null) ...[
            Icon(icon, size: 16.0),
            const SizedBox(width: 8.0)
          ],
          Flexible(child: Text(getNameFromEnum(value))),
        ],
      ),
    );
  }
}
