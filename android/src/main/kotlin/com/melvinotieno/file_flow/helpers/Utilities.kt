package com.melvinotieno.file_flow.helpers

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.net.toFile
import androidx.documentfile.provider.DocumentFile
import com.melvinotieno.file_flow.pigeons.Task
import java.io.File
import kotlin.random.Random

/**
 * Get the filename for a download task (DownloadTask/ParallelDownloadTask).
 *
 * @param url The URL of the download request.
 * @param headers The response headers of the download request.
 * @param default The default filename to use if none is found.
 * @return The filename for the download request.
 */
fun getDownloadFileName(url: String, headers: Map<String, List<String>>, default: String?): String {
    var filename = default

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
                val uri = Uri.parse(url)
                filename = uri.lastPathSegment ?: Random.nextInt(Int.MAX_VALUE).toString()
            } catch (_: Exception) {
                filename = Random.nextInt(Int.MAX_VALUE).toString()
            }
        }
    }

    // Check if filename has an extension
    if (!filename.contains(".")) {
        // Attempt to get extension from response headers
        var extension = (headers["Content-Type"] ?: headers["content-type"])?.firstOrNull()?.let {
            MimeType.getExtensionFromContentType(it)
        }

        // If extension is not found in headers, get it from the URL
        if (extension.isNullOrEmpty()) extension = MimeType.getExtensionFromUrl(url)

        // Append extension to filename
        if (!extension.isNullOrEmpty()) filename += ".$extension"
    }

    return filename
}

/**
 * Copy the downloaded file to the destination directory.
 *
 * @param context The context to use for copying the file.
 * @param task The task that the file belongs to.
 * @param tempFilePath The path of the temporary file.
 * @param destFileName The name of the destination file.
 * @return A pair containing the path of the copied file and the MIME type of the file.
 */
fun copyDownloadFile(
    context: Context, task: Task, tempFilePath: String, destFileName: String
): Pair<String?, String> {
    val tempFile = File(tempFilePath)
    val mimeType = MimeType.fromFileName(destFileName)

    val path = task.directoryUri?.let {
        val directoryUri = Uri.parse(it)

        if (directoryUri.scheme == "file") {
            val destFile = File(directoryUri.toFile(), destFileName)
            tempFile.copyTo(destFile, overwrite = true)
            destFile.absolutePath
        } else {
            val documentFile = DocumentFile.fromTreeUri(context, directoryUri)
            val destFile = documentFile?.createFile(mimeType, destFileName)

            destFile?.uri?.let { uri ->
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    tempFile.inputStream().use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                uri.toString()
            }
        }
    } ?: run {
        if (task.baseDirectory.isSharedStorage) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, destFileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                    put(MediaStore.MediaColumns.RELATIVE_PATH, task.getRelativePath(context))
                }

                context.contentResolver.let { resolver ->
                    resolver.insert(task.baseDirectory.mediaStoreUri, contentValues)?.let { uri ->
                        resolver.openOutputStream(uri)?.use { outputStream ->
                            tempFile.inputStream().use { inputStream ->
                                inputStream.copyTo(outputStream)
                            }
                        }
                        uri.toString()
                    }
                }
            } else {
                val baseDirectoryPath = task.baseDirectory.getPath(context)
                val destDirParent = Environment.getExternalStoragePublicDirectory(baseDirectoryPath)
                val destDir = File(destDirParent, task.directory ?: "").also { it.mkdirs() }
                val destFile = File(destDir, destFileName)
                tempFile.copyTo(destFile, overwrite = true)
                destFile.absolutePath
            }
        } else {
            val destFile = File("${task.getRelativePath(context)}/${destFileName}")
            tempFile.copyTo(destFile, overwrite = true)
            destFile.absolutePath
        }
    }

    return Pair(path, mimeType)
}
