package com.melvinotieno.file_flow.helpers

import android.webkit.MimeTypeMap

class MimeType {
    companion object {
        private val mimeTypeMap: MimeTypeMap = MimeTypeMap.getSingleton()

        /**
         * Get the file extension from a content type.
         *
         * @param contentType The content type to get the extension for.
         * @return The file extension for the content type, or null if not found.
         */
        fun getExtensionFromContentType(contentType: String): String? {
            return mimeTypeMap.getExtensionFromMimeType(contentType)
        }

        /**
         * Get the file extension from a URL.
         *
         * @param url The URL to get the extension for.
         * @return The file extension for the URL, or null if not found.
         */
        fun getExtensionFromUrl(url: String): String? {
            return MimeTypeMap.getFileExtensionFromUrl(url)
        }

        /**
         * Get the MIME type from a file extension.
         *
         * @param extension The file extension to get the MIME type for.
         * @return The MIME type for the file extension, or null if not found.
         */
        fun fromExtension(extension: String): String? {
            return mimeTypeMap.getMimeTypeFromExtension(extension)
        }

        /**
         * Get the MIME type from a file name.
         *
         * @param fileName The file name to get the MIME type for.
         * @return The MIME type for the file name, or "application/octet-stream" if not found.
         */
        fun fromFileName(fileName: String): String {
            val extension = fileName.substringAfterLast(".", "")
            return fromExtension(extension) ?: "application/octet-stream"
        }
    }
}
