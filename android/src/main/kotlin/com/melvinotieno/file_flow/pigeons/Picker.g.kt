// Autogenerated from Pigeon (v22.7.4), do not edit directly.
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
  return if (exception is PickerFlutterError) {
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
class PickerFlutterError (
  val code: String,
  override val message: String? = null,
  val details: Any? = null
) : Throwable()

/** Represents the base directory for picker operations. */
enum class PickerDirectory(val raw: Int) {
  DOWNLOADS(0),
  IMAGES(1),
  VIDEO(2),
  AUDIO(3),
  FILES(4);

  companion object {
    fun ofRaw(raw: Int): PickerDirectory? {
      return values().firstOrNull { it.raw == raw }
    }
  }
}
private open class PickerPigeonCodec : StandardMessageCodec() {
  override fun readValueOfType(type: Byte, buffer: ByteBuffer): Any? {
    return when (type) {
      129.toByte() -> {
        return (readValue(buffer) as Long?)?.let {
          PickerDirectory.ofRaw(it.toInt())
        }
      }
      else -> super.readValueOfType(type, buffer)
    }
  }
  override fun writeValue(stream: ByteArrayOutputStream, value: Any?)   {
    when (value) {
      is PickerDirectory -> {
        stream.write(129)
        writeValue(stream, value.raw)
      }
      else -> super.writeValue(stream, value)
    }
  }
}


/** Generated interface from Pigeon that represents a handler of messages from Flutter. */
interface PickerHostApi {
  /**
   * Checks if the specified URI use permission has been persisted.
   *
   * Parameters:
   * - [uri]: The URI to check.
   */
  fun persisted(uri: String): Boolean
  /**
   * Allows the user to select a directory from the file system.
   *
   * Parameters:
   * - [directory]: The base directory to start the picker from. This can
   * either be a [PickerDirectory] or a [String] representing a directory URI.
   * - [persist]: Whether the selected directory should be persisted.
   *
   * Returns the URI of the selected directory as a [String].
   */
  fun pickDirectory(directory: Any?, persist: Boolean, callback: (Result<String>) -> Unit)
  /**
   * Allows the user to select a file from the file system.
   *
   * Parameters:
   * - [directory]: The base directory to start the picker from. This can
   * either be a [PickerDirectory] or a [String] representing a directory URI.
   * - [extensions]: A list of file extensions to filter the picker by.
   * - [persist]: Whether the selected file should be persisted.
   *
   * Returns the URI of the selected file as a [String].
   */
  fun pickFile(directory: Any?, extensions: List<String>?, persist: Boolean, callback: (Result<String>) -> Unit)
  /**
   * Allows the user to select multiple files from the file system.
   *
   * Parameters:
   * - [directory]: The base directory to start the picker from. This can
   * either be a [PickerDirectory] or a [String] representing a directory URI.
   * - [extensions]: A list of file extensions to filter the picker by.
   * - [persist]: Whether the selected files should be persisted.
   *
   * Returns the URIs of the selected files as a [List] of [String]s.
   */
  fun pickFiles(directory: Any?, extensions: List<String>?, persist: Boolean, callback: (Result<List<String>>) -> Unit)

  companion object {
    /** The codec used by PickerHostApi. */
    val codec: MessageCodec<Any?> by lazy {
      PickerPigeonCodec()
    }
    /** Sets up an instance of `PickerHostApi` to handle messages through the `binaryMessenger`. */
    @JvmOverloads
    fun setUp(binaryMessenger: BinaryMessenger, api: PickerHostApi?, messageChannelSuffix: String = "") {
      val separatedMessageChannelSuffix = if (messageChannelSuffix.isNotEmpty()) ".$messageChannelSuffix" else ""
      run {
        val channel = BasicMessageChannel<Any?>(binaryMessenger, "dev.flutter.pigeon.file_flow.PickerHostApi.persisted$separatedMessageChannelSuffix", codec)
        if (api != null) {
          channel.setMessageHandler { message, reply ->
            val args = message as List<Any?>
            val uriArg = args[0] as String
            val wrapped: List<Any?> = try {
              listOf(api.persisted(uriArg))
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
        val channel = BasicMessageChannel<Any?>(binaryMessenger, "dev.flutter.pigeon.file_flow.PickerHostApi.pickDirectory$separatedMessageChannelSuffix", codec)
        if (api != null) {
          channel.setMessageHandler { message, reply ->
            val args = message as List<Any?>
            val directoryArg = args[0]
            val persistArg = args[1] as Boolean
            api.pickDirectory(directoryArg, persistArg) { result: Result<String> ->
              val error = result.exceptionOrNull()
              if (error != null) {
                reply.reply(wrapError(error))
              } else {
                val data = result.getOrNull()
                reply.reply(wrapResult(data))
              }
            }
          }
        } else {
          channel.setMessageHandler(null)
        }
      }
      run {
        val channel = BasicMessageChannel<Any?>(binaryMessenger, "dev.flutter.pigeon.file_flow.PickerHostApi.pickFile$separatedMessageChannelSuffix", codec)
        if (api != null) {
          channel.setMessageHandler { message, reply ->
            val args = message as List<Any?>
            val directoryArg = args[0]
            val extensionsArg = args[1] as List<String>?
            val persistArg = args[2] as Boolean
            api.pickFile(directoryArg, extensionsArg, persistArg) { result: Result<String> ->
              val error = result.exceptionOrNull()
              if (error != null) {
                reply.reply(wrapError(error))
              } else {
                val data = result.getOrNull()
                reply.reply(wrapResult(data))
              }
            }
          }
        } else {
          channel.setMessageHandler(null)
        }
      }
      run {
        val channel = BasicMessageChannel<Any?>(binaryMessenger, "dev.flutter.pigeon.file_flow.PickerHostApi.pickFiles$separatedMessageChannelSuffix", codec)
        if (api != null) {
          channel.setMessageHandler { message, reply ->
            val args = message as List<Any?>
            val directoryArg = args[0]
            val extensionsArg = args[1] as List<String>?
            val persistArg = args[2] as Boolean
            api.pickFiles(directoryArg, extensionsArg, persistArg) { result: Result<List<String>> ->
              val error = result.exceptionOrNull()
              if (error != null) {
                reply.reply(wrapError(error))
              } else {
                val data = result.getOrNull()
                reply.reply(wrapResult(data))
              }
            }
          }
        } else {
          channel.setMessageHandler(null)
        }
      }
    }
  }
}
