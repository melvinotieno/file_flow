import 'package:file_flow/src/utilities.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  group('urlWithQueryParams', () {
    test('should return the original URL if params are empty', () {
      final url = 'https://example.com';
      final params = <String, String>{};
      final result = urlWithQueryParams(url, params);
      expect(result, equals(url));
    });

    test('should append query parameters to the URL', () {
      final url = 'https://example.com';
      final params = {'key1': 'value1', 'key2': 'value2'};
      final result = urlWithQueryParams(url, params);
      expect(result, equals('https://example.com?key1=value1&key2=value2'));
    });

    test('should append query parameters to existing ones', () {
      final url = 'https://example.com?key1=value1';
      final params = {'key2': 'value2'};
      final result = urlWithQueryParams(url, params);
      expect(result, equals('https://example.com?key1=value1&key2=value2'));
    });

    test('should filter out empty keys and values', () {
      final url = 'https://example.com';
      final params = {'key1': 'value1', '': 'value2', 'key3': ''};
      final result = urlWithQueryParams(url, params);
      expect(result, equals('https://example.com?key1=value1'));
    });
  });
}
