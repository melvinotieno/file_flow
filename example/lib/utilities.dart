String getNameFromEnum(Enum e) {
  final camelCase = e.toString().split('.').last;
  final words = camelCase.split(RegExp(r'(?<=[a-z])(?=[A-Z])'));

  // Capitalize the first letter of the first word
  words[0] = words[0][0].toUpperCase() + words[0].substring(1);

  return words.join(' ');
}
