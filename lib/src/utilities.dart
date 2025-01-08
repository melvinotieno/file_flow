String urlWithQueryParams(String url, Map<String, String> params) {
  if (params.isEmpty) return url;

  final separator = url.contains('?') ? '&' : '?';

  final queryParams =
      params.entries.map((e) => '${e.key}=${e.value}').join('&');

  return '$url$separator$queryParams';
}
