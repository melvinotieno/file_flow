package com.melvinotieno.file_flow.helpers

import android.webkit.MimeTypeMap

/**
 * Utility class for working with MIME types.
 */
class MimeType {
    companion object {
        private val mimeTypeMap: MimeTypeMap = MimeTypeMap.getSingleton()

        /**
         * Get the file extension from a MIME type.
         *
         * @param mimeType The MIME type to get the extension for.
         * @return The file extension for the MIME type, or null if not found.
         */
        fun getExtensionFromMimeType(mimeType: String): String? {
            return mimeTypeMap.getExtensionFromMimeType(mimeType)
        }

        /**
         * Get the MIME type from a file extension.
         *
         * @param extension The file extension to get the MIME type for.
         * @return The MIME type for the file extension, or null if not found.
         */
        fun getMimeTypeFromExtension(extension: String): String? {
            return mimeTypeMap.getMimeTypeFromExtension(extension)
        }

        /**
         * Get the MIME type from a file name.
         *
         * @param fileName The file name to get the MIME type for.
         * @return The MIME type for the file name, or "application/octet-stream" if not found.
         */
        fun getMimeTypeFromFileName(fileName: String): String {
            val extension = fileName.substringAfterLast(".", "")
            return getMimeTypeFromExtension(extension) ?: "application/octet-stream"
        }
    }
}
