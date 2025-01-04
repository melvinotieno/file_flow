// Autogenerated from Pigeon (v22.7.1), do not edit directly.
// See also: https://pub.dev/packages/pigeon
@file:Suppress("UNCHECKED_CAST", "ArrayInDataClass")

package com.melvinotieno.file_flow.pigeons

import android.util.Log
import io.flutter.plugin.common.BasicMessageChannel
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MessageCodec
import io.flutter.plugin.common.StandardMethodCodec
import io.flutter.plugin.common.StandardMessageCodec
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

private fun wrapResult(result: Any?): List<Any?> {
  return listOf(result)
}

private fun wrapError(exception: Throwable): List<Any?> {
  return if (exception is FlowFlutterError) {
    listOf(
      exception.code,
      exception.message,
      exception.details
    )
  } else {
    listOf(
      exception.javaClass.simpleName,
      exception.toString(),
      "Cause: " + exception.cause + ", Stacktrace: " + Log.getStackTraceString(exception)
    )
  }
}

/**
 * Error class for passing custom error details to Flutter via a thrown PlatformException.
 * @property code The error code.
 * @property message The error message.
 * @property details The error details. Must be a datatype supported by the api codec.
 */
class FlowFlutterError (
  val code: String,
  override val message: String? = null,
  val details: Any? = null
) : Throwable()

/** Represents the base storage directory for task files. */
enum class StorageDirectory(val raw: Int) {
  APPLICATION_DOCUMENTS(0),
  DOWNLOADS(1);

  companion object {
    fun ofRaw(raw: Int): StorageDirectory? {
      return values().firstOrNull { it.raw == raw }
    }
  }
}

/** The type of a task. */
enum class TaskType(val raw: Int) {
  /** Task is a download task. */
  DOWNLOAD(0),
  /** Task is an upload task. */
  UPLOAD(1);

  companion object {
    fun ofRaw(raw: Int): TaskType? {
      return values().firstOrNull { it.raw == raw }
    }
  }
}

/** The state of a task. */
enum class TaskState(val raw: Int) {
  /** */
  PENDING(0),
  /** Task is running. */
  RUNNING(1),
  /** Task is paused. */
  PAUSED(2),
  /** Task has completed successfully. */
  COMPLETED(3),
  /** Task has failed. */
  FAILED(4),
  /** Task has been canceled. */
  CANCELED(5);

  companion object {
    fun ofRaw(raw: Int): TaskState? {
      return values().firstOrNull { it.raw == raw }
    }
  }
}

/** Generated class from Pigeon that represents data sent in messages. */
data class ProxyConfig (
  val address: String,
  val port: Long
)
 {
  companion object {
    fun fromList(pigeonVar_list: List<Any?>): ProxyConfig {
      val address = pigeonVar_list[0] as String
      val port = pigeonVar_list[1] as Long
      return ProxyConfig(address, port)
    }
  }
  fun toList(): List<Any?> {
    return listOf(
      address,
      port,
    )
  }
}

/** Generated class from Pigeon that represents data sent in messages. */
data class Task (
  val id: String,
  val url: String,
  val method: String,
  val headers: Map<String, String>,
  val timeout: Long,
  val proxy: ProxyConfig? = null,
  val baseDirectory: StorageDirectory,
  val directory: String? = null,
  val filename: String? = null,
  val group: String,
  val type: TaskType
)
 {
  companion object {
    fun fromList(pigeonVar_list: List<Any?>): Task {
      val id = pigeonVar_list[0] as String
      val url = pigeonVar_list[1] as String
      val method = pigeonVar_list[2] as String
      val headers = pigeonVar_list[3] as Map<String, String>
      val timeout = pigeonVar_list[4] as Long
      val proxy = pigeonVar_list[5] as ProxyConfig?
      val baseDirectory = pigeonVar_list[6] as StorageDirectory
      val directory = pigeonVar_list[7] as String?
      val filename = pigeonVar_list[8] as String?
      val group = pigeonVar_list[9] as String
      val type = pigeonVar_list[10] as TaskType
      return Task(id, url, method, headers, timeout, proxy, baseDirectory, directory, filename, group, type)
    }
  }
  fun toList(): List<Any?> {
    return listOf(
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
    )
  }
}

/**
 * Generated class from Pigeon that represents data sent in messages.
 * This class should not be extended by any user class outside of the generated file.
 */
sealed class TaskEvent 
/** Generated class from Pigeon that represents data sent in messages. */
data class TaskProgress (
  val taskId: String,
  val progress: Long
) : TaskEvent()
 {
  companion object {
    fun fromList(pigeonVar_list: List<Any?>): TaskProgress {
      val taskId = pigeonVar_list[0] as String
      val progress = pigeonVar_list[1] as Long
      return TaskProgress(taskId, progress)
    }
  }
  fun toList(): List<Any?> {
    return listOf(
      taskId,
      progress,
    )
  }
}

/** Generated class from Pigeon that represents data sent in messages. */
data class TaskStatus (
  val taskId: String,
  val state: TaskState
) : TaskEvent()
 {
  companion object {
    fun fromList(pigeonVar_list: List<Any?>): TaskStatus {
      val taskId = pigeonVar_list[0] as String
      val state = pigeonVar_list[1] as TaskState
      return TaskStatus(taskId, state)
    }
  }
  fun toList(): List<Any?> {
    return listOf(
      taskId,
      state,
    )
  }
}
private open class FlowPigeonCodec : StandardMessageCodec() {
  override fun readValueOfType(type: Byte, buffer: ByteBuffer): Any? {
    return when (type) {
      129.toByte() -> {
        return (readValue(buffer) as Long?)?.let {
          StorageDirectory.ofRaw(it.toInt())
        }
      }
      130.toByte() -> {
        return (readValue(buffer) as Long?)?.let {
          TaskType.ofRaw(it.toInt())
        }
      }
      131.toByte() -> {
        return (readValue(buffer) as Long?)?.let {
          TaskState.ofRaw(it.toInt())
        }
      }
      132.toByte() -> {
        return (readValue(buffer) as? List<Any?>)?.let {
          ProxyConfig.fromList(it)
        }
      }
      133.toByte() -> {
        return (readValue(buffer) as? List<Any?>)?.let {
          Task.fromList(it)
        }
      }
      134.toByte() -> {
        return (readValue(buffer) as? List<Any?>)?.let {
          TaskProgress.fromList(it)
        }
      }
      135.toByte() -> {
        return (readValue(buffer) as? List<Any?>)?.let {
          TaskStatus.fromList(it)
        }
      }
      else -> super.readValueOfType(type, buffer)
    }
  }
  override fun writeValue(stream: ByteArrayOutputStream, value: Any?)   {
    when (value) {
      is StorageDirectory -> {
        stream.write(129)
        writeValue(stream, value.raw)
      }
      is TaskType -> {
        stream.write(130)
        writeValue(stream, value.raw)
      }
      is TaskState -> {
        stream.write(131)
        writeValue(stream, value.raw)
      }
      is ProxyConfig -> {
        stream.write(132)
        writeValue(stream, value.toList())
      }
      is Task -> {
        stream.write(133)
        writeValue(stream, value.toList())
      }
      is TaskProgress -> {
        stream.write(134)
        writeValue(stream, value.toList())
      }
      is TaskStatus -> {
        stream.write(135)
        writeValue(stream, value.toList())
      }
      else -> super.writeValue(stream, value)
    }
  }
}

val FlowPigeonMethodCodec = StandardMethodCodec(FlowPigeonCodec());

/** Generated interface from Pigeon that represents a handler of messages from Flutter. */
interface FileFlowHostApi {
  fun enqueue(task: Task): Boolean
  fun pause(taskId: String): Boolean
  fun resume(taskId: String): Boolean

  companion object {
    /** The codec used by FileFlowHostApi. */
    val codec: MessageCodec<Any?> by lazy {
      FlowPigeonCodec()
    }
    /** Sets up an instance of `FileFlowHostApi` to handle messages through the `binaryMessenger`. */
    @JvmOverloads
    fun setUp(binaryMessenger: BinaryMessenger, api: FileFlowHostApi?, messageChannelSuffix: String = "") {
      val separatedMessageChannelSuffix = if (messageChannelSuffix.isNotEmpty()) ".$messageChannelSuffix" else ""
      run {
        val channel = BasicMessageChannel<Any?>(binaryMessenger, "dev.flutter.pigeon.file_flow.FileFlowHostApi.enqueue$separatedMessageChannelSuffix", codec)
        if (api != null) {
          channel.setMessageHandler { message, reply ->
            val args = message as List<Any?>
            val taskArg = args[0] as Task
            val wrapped: List<Any?> = try {
              listOf(api.enqueue(taskArg))
            } catch (exception: Throwable) {
              wrapError(exception)
            }
            reply.reply(wrapped)
          }
        } else {
          channel.setMessageHandler(null)
        }
      }
      run {
        val channel = BasicMessageChannel<Any?>(binaryMessenger, "dev.flutter.pigeon.file_flow.FileFlowHostApi.pause$separatedMessageChannelSuffix", codec)
        if (api != null) {
          channel.setMessageHandler { message, reply ->
            val args = message as List<Any?>
            val taskIdArg = args[0] as String
            val wrapped: List<Any?> = try {
              listOf(api.pause(taskIdArg))
            } catch (exception: Throwable) {
              wrapError(exception)
            }
            reply.reply(wrapped)
          }
        } else {
          channel.setMessageHandler(null)
        }
      }
      run {
        val channel = BasicMessageChannel<Any?>(binaryMessenger, "dev.flutter.pigeon.file_flow.FileFlowHostApi.resume$separatedMessageChannelSuffix", codec)
        if (api != null) {
          channel.setMessageHandler { message, reply ->
            val args = message as List<Any?>
            val taskIdArg = args[0] as String
            val wrapped: List<Any?> = try {
              listOf(api.resume(taskIdArg))
            } catch (exception: Throwable) {
              wrapError(exception)
            }
            reply.reply(wrapped)
          }
        } else {
          channel.setMessageHandler(null)
        }
      }
    }
  }
}

private class PigeonStreamHandler<T>(
    val wrapper: PigeonEventChannelWrapper<T>
) : EventChannel.StreamHandler {
  var pigeonSink: PigeonEventSink<T>? = null

  override fun onListen(p0: Any?, sink: EventChannel.EventSink) {
    pigeonSink = PigeonEventSink<T>(sink)
    wrapper.onListen(p0, pigeonSink!!)
  }

  override fun onCancel(p0: Any?) {
    pigeonSink = null
    wrapper.onCancel(p0)
  }
}

interface PigeonEventChannelWrapper<T> {
  open fun onListen(p0: Any?, sink: PigeonEventSink<T>) {}

  open fun onCancel(p0: Any?) {}
}

class PigeonEventSink<T>(private val sink: EventChannel.EventSink) {
  fun success(value: T) {
    sink.success(value)
  }

  fun error(errorCode: String, errorMessage: String?, errorDetails: Any?) {
    sink.error(errorCode, errorMessage, errorDetails)
  }
  
  fun endOfStream() { 
    sink.endOfStream()
  }
}
      
abstract class StreamTaskEventsStreamHandler : PigeonEventChannelWrapper<TaskEvent> {
  companion object {
    fun register(messenger: BinaryMessenger, streamHandler: StreamTaskEventsStreamHandler, instanceName: String = "") {
      var channelName: String = "dev.flutter.pigeon.file_flow.FileFlowEventChannelApi.streamTaskEvents"
      if (instanceName.isNotEmpty()) {
        channelName += ".$instanceName"
      }
      val internalStreamHandler = PigeonStreamHandler<TaskEvent>(streamHandler)
      EventChannel(messenger, channelName, FlowPigeonMethodCodec).setStreamHandler(internalStreamHandler)
    }
  }
}
      
