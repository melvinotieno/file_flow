import 'package:file_flow_example/main.dart';
import 'package:flutter/material.dart';

void navigate(Widget screen, {RouteSettings? settings}) {
  MyApp.navigatorKey.currentState!.push(
    MaterialPageRoute(builder: (context) => screen, settings: settings),
  );
}

void navigateBack() => MyApp.navigatorKey.currentState!.pop();

void showSnackBar(String message) {
  final state = MyApp.scaffoldMessengerKey.currentState!;
  state.hideCurrentSnackBar();
  final snackbar = SnackBar(content: Text(message));
  state.showSnackBar(snackbar);
}

String getNameFromEnum(Enum e) {
  final camelCase = e.toString().split('.').last;
  final words = camelCase.split(RegExp(r'(?<=[a-z])(?=[A-Z])'));
  words[0] = words[0][0].toUpperCase() + words[0].substring(1);
  return words.join(' ');
}

String formatBytes(int byts) {
  const units = ['B', 'KB', 'MB', 'GB', 'TB'];
  double size = byts.toDouble();
  int i = 0;
  while (size > 1024 && i < units.length - 1) {
    size /= 1024;
    i++;
  }
  return '${size.toStringAsFixed(2)} ${units[i]}';
}
