package com.melvinotieno.file_flow.helpers

import android.net.Uri
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
