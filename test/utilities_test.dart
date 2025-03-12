import 'package:file_flow/src/utilities.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  group('urlWithQueryParams', () {
    test('returns the original URL if params are empty', () {
      final url = 'https://example.com';
      final params = <String, String>{};
      final result = urlWithQueryParams(url, params);
      expect(result, equals(url));
    });

    test('appends query parameters to the URL', () {
      final url = 'https://example.com';
      final params = {'key1': 'value1', 'key2': 'value2'};
      final result = urlWithQueryParams(url, params);
      expect(result, equals('https://example.com?key1=value1&key2=value2'));
    });

    test('appends query parameters to a URL that already has parameters', () {
      final url = 'https://example.com?key1=value1';
      final params = {'key2': 'value2'};
      final result = urlWithQueryParams(url, params);
      expect(result, equals('https://example.com?key1=value1&key2=value2'));
    });

    test('filters out empty keys and values', () {
      final url = 'https://example.com';
      final params = {'key1': 'value1', '': 'value2', 'key3': ''};
      final result = urlWithQueryParams(url, params);
      expect(result, equals('https://example.com?key1=value1'));
    });
  });
}
