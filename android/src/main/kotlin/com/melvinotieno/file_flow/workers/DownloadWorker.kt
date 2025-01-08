package com.melvinotieno.file_flow.workers

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import androidx.work.WorkerParameters
import com.melvinotieno.file_flow.helpers.MimeType
import com.melvinotieno.file_flow.helpers.isPublicStorage
import com.melvinotieno.file_flow.pigeons.TaskState
import io.flutter.util.PathUtils
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import kotlin.collections.firstOrNull
import kotlin.random.Random

class DownloadWorker(context: Context, params: WorkerParameters) : TaskWorker(context, params) {
    private val tempFilePath: String by lazy {
        "${PathUtils.getFilesDir(context)}/${Random.nextInt(Int.MAX_VALUE)}"
    }

    override suspend fun processRequest(connection: HttpURLConnection): TaskState {
        return if (connection.responseCode in 200..206) {
            val taskState = BufferedInputStream(connection.inputStream).use { inputStream ->
                FileOutputStream(File(tempFilePath)).use { outputStream ->
                    transferBytes(inputStream, outputStream)
                }
            }

            when (taskState) {
                TaskState.COMPLETED -> {
                    val destFileName = getDestFileName(connection.headerFields)
                    val path = moveTempFile(destFileName)
                }
                TaskState.FAILED -> {
                    Log.i("DownloadWorker", "Failed to download file")
                }
                TaskState.PAUSED -> {
                    Log.i("DownloadWorker", "Download paused")
                }
                TaskState.PENDING -> {
                    Log.i("DownloadWorker", "Download pending")
                }
                TaskState.RUNNING -> {
                    Log.i("DownloadWorker", "Download running")
                }
                TaskState.CANCELED -> {
                    deleteTempFile()
                    Log.i(TAG, "Download canceled")
                }
            }

            taskState
        } else {
            TaskState.FAILED
        }
    }

    private fun moveTempFile(destFileName: String): String? {
        return if (task.baseDirectory.isPublicStorage()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                moveTempFileUsingMediaStore(destFileName)
            } else {
                moveTempFileUsingFileSystem(destFileName)
            }
        } else {
            moveTempFileToPrivateStorage(destFileName)
        }
    }

    private fun moveTempFileToPrivateStorage(destFileName: String): String? {
        return null
    }

    private fun moveTempFileUsingFileSystem(destFileName: String): String? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun moveTempFileUsingMediaStore(destFileName: String): String? {
        val tempFile = File(tempFilePath)

        if (!tempFile.exists()) {
            Log.e("$TAG (${task.id})", "Temp file does not exist")
            return null
        }

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, destFileName)
            put(MediaStore.MediaColumns.MIME_TYPE, MimeType.getMimeTypeFromFileName(destFileName))
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/${task.directory}")
        }

        val resolver = context.contentResolver

        return resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)?.let {
            try {
                resolver.openOutputStream(it)?.use { outputStream ->
                    tempFile.inputStream().use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                it.toString()
            } catch (e: Exception) {
                Log.e("$TAG (${task.id})", "Failed to move temp file $tempFilePath", e)
                null
            }
        }
    }

    private fun getDestFileName(headers: Map<String, List<String>>): String {
//        Log.d(TAG, "Response headers: $headers")

        var filename = task.filename

        if (filename.isNullOrEmpty()) {
            // Attempt to get filename from response headers
            (headers["Content-Disposition"] ?: headers["content-disposition"])?.let { disposition ->
                val filenameHeader = disposition.find {
                    it.contains("filename=") || it.contains("filename*=")
                }

                filenameHeader?.let { header ->
                    filename = when {
                        header.contains("filename*=") -> {
                            val startIndex = header.indexOf("filename*=") + 10
                            val encodedFilename = header.substring(startIndex).split("''").last()
                            Uri.decode(encodedFilename)
                        }
                        header.contains("filename=") -> {
                            val startIndex = header.indexOf("filename=") + 9
                            header.substring(startIndex)
                        }
                        else -> null
                    }
                }
            }

            // If filename is not found in headers, get it from the URL or generate a random one
            if (filename.isNullOrEmpty()) {
                try {
                    val uri = Uri.parse(task.url)
                    filename = uri.lastPathSegment ?: Random.nextInt(Int.MAX_VALUE).toString()
                } catch (e: Exception) {
                    filename = Random.nextInt(Int.MAX_VALUE).toString()
                    Log.e(TAG, "Failed to get filename from URL", e)
                }
            }
        }

        // Check if filename has an extension
        if (!filename!!.contains('.')) {
            // Attempt to get extension from headers
            var extension: String? = null
            headers["Content-Type"]?.let { contentType ->
                contentType.firstOrNull()?.let {
                    extension = MimeType.getExtensionFromMimeType(it)
                }
            }

            // If extension is not found in headers, attempt to get it from URL
            if (extension.isNullOrEmpty()) {
                try {
                    val mimeType = MimeTypeMap.getFileExtensionFromUrl(task.url)
                    extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to get extension from url", e)
                }
            }

            // Append extension to filename if found
            if (!extension.isNullOrEmpty()) filename += ".$extension"
        }

        return filename!!
    }

    private fun deleteTempFile() {
        try {
            File(tempFilePath).takeIf { it.exists() }?.delete()
        } catch (e: Exception) {
            Log.e("$TAG (${task.id})", "Failed to delete temp file $tempFilePath", e)
        }
    }
}
