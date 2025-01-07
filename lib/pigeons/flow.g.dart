// Autogenerated from Pigeon (v22.7.2), do not edit directly.
// See also: https://pub.dev/packages/pigeon
// ignore_for_file: public_member_api_docs, non_constant_identifier_names, avoid_as, unused_import, unnecessary_parenthesis, prefer_null_aware_operators, omit_local_variable_types, unused_shown_name, unnecessary_import, no_leading_underscores_for_local_identifiers

import 'dart:async';
import 'dart:typed_data' show Float64List, Int32List, Int64List, Uint8List;

import 'package:flutter/foundation.dart' show ReadBuffer, WriteBuffer;
import 'package:flutter/services.dart';

PlatformException _createConnectionError(String channelName) {
  return PlatformException(
    code: 'channel-error',
    message: 'Unable to establish connection on channel: "$channelName".',
  );
}

List<Object?> wrapResponse({Object? result, PlatformException? error, bool empty = false}) {
  if (empty) {
    return <Object?>[];
  }
  if (error == null) {
    return <Object?>[result];
  }
  return <Object?>[error.code, error.message, error.details];
}

/// Represents the base storage directory for task files.
enum StorageDirectory {
  applicationDocuments,
  downloads,
}

/// The type of a task.
enum TaskType {
  /// Task is a download task.
  download,
  /// Task is an upload task.
  upload,
}

/// The state of a task.
enum TaskState {
  ///
  pending,
  /// Task is running.
  running,
  /// Task is paused.
  paused,
  /// Task has completed successfully.
  completed,
  /// Task has failed.
  failed,
  /// Task has been canceled.
  canceled,
}

class ProxyConfig {
  ProxyConfig({
    required this.address,
    required this.port,
  });

  String address;

  int port;

  Object encode() {
    return <Object?>[
      address,
      port,
    ];
  }

  static ProxyConfig decode(Object result) {
    result as List<Object?>;
    return ProxyConfig(
      address: result[0]! as String,
      port: result[1]! as int,
    );
  }
}

class Task {
  Task({
    required this.id,
    required this.url,
    required this.method,
    required this.headers,
    required this.timeout,
    this.proxy,
    required this.baseDirectory,
    this.directory,
    this.filename,
    required this.group,
    required this.type,
  });

  String id;

  String url;

  String method;

  Map<String, String> headers;

  int timeout;

  ProxyConfig? proxy;

  StorageDirectory baseDirectory;

  String? directory;

  String? filename;

  String group;

  TaskType type;

  Object encode() {
    return <Object?>[
      id,
      url,
      method,
      headers,
      timeout,
      proxy,
      baseDirectory,
      directory,
      filename,
      group,
      type,
    ];
  }

  static Task decode(Object result) {
    result as List<Object?>;
    return Task(
      id: result[0]! as String,
      url: result[1]! as String,
      method: result[2]! as String,
      headers: (result[3] as Map<Object?, Object?>?)!.cast<String, String>(),
      timeout: result[4]! as int,
      proxy: result[5] as ProxyConfig?,
      baseDirectory: result[6]! as StorageDirectory,
      directory: result[7] as String?,
      filename: result[8] as String?,
      group: result[9]! as String,
      type: result[10]! as TaskType,
    );
  }
}

sealed class TaskEvent {
}

class TaskProgress extends TaskEvent {
  TaskProgress({
    required this.taskId,
    required this.progress,
  });

  String taskId;

  int progress;

  Object encode() {
    return <Object?>[
      taskId,
      progress,
    ];
  }

  static TaskProgress decode(Object result) {
    result as List<Object?>;
    return TaskProgress(
      taskId: result[0]! as String,
      progress: result[1]! as int,
    );
  }
}

class TaskStatus extends TaskEvent {
  TaskStatus({
    required this.taskId,
    required this.state,
  });

  String taskId;

  TaskState state;

  Object encode() {
    return <Object?>[
      taskId,
      state,
    ];
  }

  static TaskStatus decode(Object result) {
    result as List<Object?>;
    return TaskStatus(
      taskId: result[0]! as String,
      state: result[1]! as TaskState,
    );
  }
}


class _PigeonCodec extends StandardMessageCodec {
  const _PigeonCodec();
  @override
  void writeValue(WriteBuffer buffer, Object? value) {
    if (value is int) {
      buffer.putUint8(4);
      buffer.putInt64(value);
    }    else if (value is StorageDirectory) {
      buffer.putUint8(129);
      writeValue(buffer, value.index);
    }    else if (value is TaskType) {
      buffer.putUint8(130);
      writeValue(buffer, value.index);
    }    else if (value is TaskState) {
      buffer.putUint8(131);
      writeValue(buffer, value.index);
    }    else if (value is ProxyConfig) {
      buffer.putUint8(132);
      writeValue(buffer, value.encode());
    }    else if (value is Task) {
      buffer.putUint8(133);
      writeValue(buffer, value.encode());
    }    else if (value is TaskProgress) {
      buffer.putUint8(134);
      writeValue(buffer, value.encode());
    }    else if (value is TaskStatus) {
      buffer.putUint8(135);
      writeValue(buffer, value.encode());
    } else {
      super.writeValue(buffer, value);
    }
  }

  @override
  Object? readValueOfType(int type, ReadBuffer buffer) {
    switch (type) {
      case 129: 
        final int? value = readValue(buffer) as int?;
        return value == null ? null : StorageDirectory.values[value];
      case 130: 
        final int? value = readValue(buffer) as int?;
        return value == null ? null : TaskType.values[value];
      case 131: 
        final int? value = readValue(buffer) as int?;
        return value == null ? null : TaskState.values[value];
      case 132: 
        return ProxyConfig.decode(readValue(buffer)!);
      case 133: 
        return Task.decode(readValue(buffer)!);
      case 134: 
        return TaskProgress.decode(readValue(buffer)!);
      case 135: 
        return TaskStatus.decode(readValue(buffer)!);
      default:
        return super.readValueOfType(type, buffer);
    }
  }
}

const StandardMethodCodec pigeonMethodCodec = StandardMethodCodec(_PigeonCodec());

class FileFlowHostApi {
  /// Constructor for [FileFlowHostApi].  The [binaryMessenger] named argument is
  /// available for dependency injection.  If it is left null, the default
  /// BinaryMessenger will be used which routes to the host platform.
  FileFlowHostApi({BinaryMessenger? binaryMessenger, String messageChannelSuffix = ''})
      : pigeonVar_binaryMessenger = binaryMessenger,
        pigeonVar_messageChannelSuffix = messageChannelSuffix.isNotEmpty ? '.$messageChannelSuffix' : '';
  final BinaryMessenger? pigeonVar_binaryMessenger;

  static const MessageCodec<Object?> pigeonChannelCodec = _PigeonCodec();

  final String pigeonVar_messageChannelSuffix;

  Future<bool> enqueue(Task task) async {
    final String pigeonVar_channelName = 'dev.flutter.pigeon.file_flow.FileFlowHostApi.enqueue$pigeonVar_messageChannelSuffix';
    final BasicMessageChannel<Object?> pigeonVar_channel = BasicMessageChannel<Object?>(
      pigeonVar_channelName,
      pigeonChannelCodec,
      binaryMessenger: pigeonVar_binaryMessenger,
    );
    final List<Object?>? pigeonVar_replyList =
        await pigeonVar_channel.send(<Object?>[task]) as List<Object?>?;
    if (pigeonVar_replyList == null) {
      throw _createConnectionError(pigeonVar_channelName);
    } else if (pigeonVar_replyList.length > 1) {
      throw PlatformException(
        code: pigeonVar_replyList[0]! as String,
        message: pigeonVar_replyList[1] as String?,
        details: pigeonVar_replyList[2],
      );
    } else if (pigeonVar_replyList[0] == null) {
      throw PlatformException(
        code: 'null-error',
        message: 'Host platform returned null value for non-null return value.',
      );
    } else {
      return (pigeonVar_replyList[0] as bool?)!;
    }
  }

  Future<bool> pause(String taskId) async {
    final String pigeonVar_channelName = 'dev.flutter.pigeon.file_flow.FileFlowHostApi.pause$pigeonVar_messageChannelSuffix';
    final BasicMessageChannel<Object?> pigeonVar_channel = BasicMessageChannel<Object?>(
      pigeonVar_channelName,
      pigeonChannelCodec,
      binaryMessenger: pigeonVar_binaryMessenger,
    );
    final List<Object?>? pigeonVar_replyList =
        await pigeonVar_channel.send(<Object?>[taskId]) as List<Object?>?;
    if (pigeonVar_replyList == null) {
      throw _createConnectionError(pigeonVar_channelName);
    } else if (pigeonVar_replyList.length > 1) {
      throw PlatformException(
        code: pigeonVar_replyList[0]! as String,
        message: pigeonVar_replyList[1] as String?,
        details: pigeonVar_replyList[2],
      );
    } else if (pigeonVar_replyList[0] == null) {
      throw PlatformException(
        code: 'null-error',
        message: 'Host platform returned null value for non-null return value.',
      );
    } else {
      return (pigeonVar_replyList[0] as bool?)!;
    }
  }

  Future<bool> resume(String taskId) async {
    final String pigeonVar_channelName = 'dev.flutter.pigeon.file_flow.FileFlowHostApi.resume$pigeonVar_messageChannelSuffix';
    final BasicMessageChannel<Object?> pigeonVar_channel = BasicMessageChannel<Object?>(
      pigeonVar_channelName,
      pigeonChannelCodec,
      binaryMessenger: pigeonVar_binaryMessenger,
    );
    final List<Object?>? pigeonVar_replyList =
        await pigeonVar_channel.send(<Object?>[taskId]) as List<Object?>?;
    if (pigeonVar_replyList == null) {
      throw _createConnectionError(pigeonVar_channelName);
    } else if (pigeonVar_replyList.length > 1) {
      throw PlatformException(
        code: pigeonVar_replyList[0]! as String,
        message: pigeonVar_replyList[1] as String?,
        details: pigeonVar_replyList[2],
      );
    } else if (pigeonVar_replyList[0] == null) {
      throw PlatformException(
        code: 'null-error',
        message: 'Host platform returned null value for non-null return value.',
      );
    } else {
      return (pigeonVar_replyList[0] as bool?)!;
    }
  }
}

abstract class FileFlowFlutterApi {
  static const MessageCodec<Object?> pigeonChannelCodec = _PigeonCodec();

  void updateTaskProgress(String taskId, int progress);

  void updateTaskState(String taskId, TaskState state);

  static void setUp(FileFlowFlutterApi? api, {BinaryMessenger? binaryMessenger, String messageChannelSuffix = '',}) {
    messageChannelSuffix = messageChannelSuffix.isNotEmpty ? '.$messageChannelSuffix' : '';
    {
      final BasicMessageChannel<Object?> pigeonVar_channel = BasicMessageChannel<Object?>(
          'dev.flutter.pigeon.file_flow.FileFlowFlutterApi.updateTaskProgress$messageChannelSuffix', pigeonChannelCodec,
          binaryMessenger: binaryMessenger);
      if (api == null) {
        pigeonVar_channel.setMessageHandler(null);
      } else {
        pigeonVar_channel.setMessageHandler((Object? message) async {
          assert(message != null,
          'Argument for dev.flutter.pigeon.file_flow.FileFlowFlutterApi.updateTaskProgress was null.');
          final List<Object?> args = (message as List<Object?>?)!;
          final String? arg_taskId = (args[0] as String?);
          assert(arg_taskId != null,
              'Argument for dev.flutter.pigeon.file_flow.FileFlowFlutterApi.updateTaskProgress was null, expected non-null String.');
          final int? arg_progress = (args[1] as int?);
          assert(arg_progress != null,
              'Argument for dev.flutter.pigeon.file_flow.FileFlowFlutterApi.updateTaskProgress was null, expected non-null int.');
          try {
            api.updateTaskProgress(arg_taskId!, arg_progress!);
            return wrapResponse(empty: true);
          } on PlatformException catch (e) {
            return wrapResponse(error: e);
          }          catch (e) {
            return wrapResponse(error: PlatformException(code: 'error', message: e.toString()));
          }
        });
      }
    }
    {
      final BasicMessageChannel<Object?> pigeonVar_channel = BasicMessageChannel<Object?>(
          'dev.flutter.pigeon.file_flow.FileFlowFlutterApi.updateTaskState$messageChannelSuffix', pigeonChannelCodec,
          binaryMessenger: binaryMessenger);
      if (api == null) {
        pigeonVar_channel.setMessageHandler(null);
      } else {
        pigeonVar_channel.setMessageHandler((Object? message) async {
          assert(message != null,
          'Argument for dev.flutter.pigeon.file_flow.FileFlowFlutterApi.updateTaskState was null.');
          final List<Object?> args = (message as List<Object?>?)!;
          final String? arg_taskId = (args[0] as String?);
          assert(arg_taskId != null,
              'Argument for dev.flutter.pigeon.file_flow.FileFlowFlutterApi.updateTaskState was null, expected non-null String.');
          final TaskState? arg_state = (args[1] as TaskState?);
          assert(arg_state != null,
              'Argument for dev.flutter.pigeon.file_flow.FileFlowFlutterApi.updateTaskState was null, expected non-null TaskState.');
          try {
            api.updateTaskState(arg_taskId!, arg_state!);
            return wrapResponse(empty: true);
          } on PlatformException catch (e) {
            return wrapResponse(error: e);
          }          catch (e) {
            return wrapResponse(error: PlatformException(code: 'error', message: e.toString()));
          }
        });
      }
    }
  }
}

Stream<TaskEvent> streamTaskEvents( {String instanceName = ''}) {
  if (instanceName.isNotEmpty) {
    instanceName = '.$instanceName';
  }
  const EventChannel streamTaskEventsChannel =
      EventChannel('dev.flutter.pigeon.file_flow.FileFlowEventChannelApi.streamTaskEvents', pigeonMethodCodec);
  return streamTaskEventsChannel.receiveBroadcastStream().map((dynamic event) {
    return event as TaskEvent;
  });
}
    
