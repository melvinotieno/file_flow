import 'package:flutter_test/flutter_test.dart';
import 'package:file_flow/file_flow.dart';
import 'package:file_flow/file_flow_platform_interface.dart';
import 'package:file_flow/file_flow_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockFileFlowPlatform
    with MockPlatformInterfaceMixin
    implements FileFlowPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final FileFlowPlatform initialPlatform = FileFlowPlatform.instance;

  test('$MethodChannelFileFlow is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelFileFlow>());
  });

  test('getPlatformVersion', () async {
    FileFlow fileFlowPlugin = FileFlow();
    MockFileFlowPlatform fakePlatform = MockFileFlowPlatform();
    FileFlowPlatform.instance = fakePlatform;

    expect(await fileFlowPlugin.getPlatformVersion(), '42');
  });
}
