/// Constructs a URL with the given query parameters.
///
/// Parameters:
/// - `url`: The base URL to which the query parameters will be appended.
/// - `params`: A map of query parameters to append to the URL.
///
/// If the `url` already contains query parameters, the given parameters are
/// appended to the existing ones.
///
/// If the `params` map is empty, the `url` is returned as is.
///
/// Example:
/// ```dart
/// final url = urlWithQueryParams('https://example.com', {
///  'key1': 'value1',
///  'key2': 'value2',
/// });
///
/// print(url); // https://example.com?key1=value1&key2=value2
/// ```
String urlWithQueryParams(String url, Map<String, String> params) {
  if (params.isEmpty) return url;

  // Determine the separator to use between the URL and the query parameters.
  final separator = url.contains('?') ? '&' : '?';

  // Filter out empty keys and values, and join the entries with '&'.
  final queryParams = params.entries
      .where((entry) => entry.key.isNotEmpty && entry.value.isNotEmpty)
      .map((entry) => '${entry.key}=${entry.value}')
      .join('&');

  return '$url$separator$queryParams';
}
