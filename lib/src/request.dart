import 'config.dart';
import 'flow.dart';
import 'utilities.dart';

/// HTTP methods that can be used for a request.
enum RequestMethod { head, get, post, put, patch, delete }

/// Represents a server HTTP request.
base class Request {
  Request({
    required String url,
    this.params = const {},
    this.method = RequestMethod.get,
    this.headers = const {},
    RequestConfig? config,
  }) : assert(url.isNotEmpty, 'Invalid URL: $url'),
       url = urlWithQueryParams(url, params),
       config = config ?? FileFlow.config.request;

  /// The URL to send the request to.
  final String url;

  /// The query parameters to send with the request.
  final Map<String, String> params;

  /// The HTTP method to use for the request.
  final RequestMethod method;

  /// The headers to send with the request.
  final Map<String, String> headers;

  /// The configuration to use for the request.
  final RequestConfig config;
}
