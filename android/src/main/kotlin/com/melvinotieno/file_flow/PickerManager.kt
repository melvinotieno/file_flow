package com.melvinotieno.file_flow

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.ext.SdkExtensions
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import androidx.core.net.toUri
import com.melvinotieno.file_flow.helpers.DOCUMENT_TREE_PRIMARY
import com.melvinotieno.file_flow.helpers.documentUri
import com.melvinotieno.file_flow.pigeons.PickerDirectory
import com.melvinotieno.file_flow.pigeons.PickerFlutterError
import com.melvinotieno.file_flow.pigeons.PickerHostApi
import io.flutter.plugin.common.PluginRegistry

class PickerManager : PickerHostApi, PluginRegistry.ActivityResultListener {
    companion object {
        private const val TAG = "PickerManager"
        private const val REQUEST_CODE_PICK_DIRECTORY = 100
        private const val REQUEST_CODE_PICK_FILE = 101
    }

    private var activity: Activity? = null
    private var callback: ((Result<String>) -> Unit)? = null

    /**
     * Set the activity to use for calling startActivityForResult.
     *
     * @param activity The activity to use for the picker.
     */
    fun setActivity(activity: Activity?) {
        this.activity = activity
    }

    /**
     * Check if the URI has persisted permissions.
     *
     * @param uri The URI to check.
     * @return True if the URI has persisted permissions, false otherwise.
     */
    override fun persisted(uri: String): Boolean {
        return activity?.contentResolver?.persistedUriPermissions?.any { permission ->
            permission.uri.toString() == uri
        } == true
    }

    /**
     * Pick a directory from the file system.
     *
     * @param directory The starting directory to pick from.
     * @param persist Whether to persist the permission to access the directory.
     * @param callback The callback to invoke with the result.
     * @throws IllegalStateException If a picker is already active.
     */
    override fun pickDirectory(
        directory: Any?, persist: Boolean, callback: (Result<String>) -> Unit
    ) {
        if (this.callback != null) throw IllegalStateException("A picker is already active")
        this.callback = callback
        val intent = createDirectoryPickerIntent(directory, persist)
        activity?.startActivityForResult(intent, REQUEST_CODE_PICK_DIRECTORY)
    }

    override fun pickFile(
        directory: Any?,
        mimeTypes: List<String>?,
        persist: Boolean,
        callback: (Result<String>) -> Unit
    ) {
        if (this.callback != null) throw IllegalStateException("A picker is already active")
        this.callback = callback
        val intent = createFilePickerIntent(directory, mimeTypes, persist)
        activity?.startActivityForResult(intent, REQUEST_CODE_PICK_FILE)
    }

    override fun pickFiles(
        directory: Any?,
        mimeTypes: List<String>?,
        persist: Boolean,
        callback: (Result<List<String>>) -> Unit
    ) {
        if (this.callback != null) throw IllegalStateException("A picker is already active")
        val intent = createFilePickerIntent(directory, mimeTypes, persist, true)
        activity?.startActivityForResult(intent, REQUEST_CODE_PICK_FILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?): Boolean {
        var success = false

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_PICK_DIRECTORY) {
                val directoryUri = intent?.data
                Log.d(TAG, "onActivityResult: $directoryUri")
                Log.d(TAG, "onActivityResult flags: ${intent?.flags}")
                Log.d(
                    TAG,
                    "onActivityResult persistable: ${intent?.flags?.and(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)}"
                )

                if (directoryUri != null) {
                    callback?.invoke(Result.success(directoryUri.toString()))
                } else {
                    callback?.invoke(Result.failure(PickerFlutterError("cancelled")))
                }
            } else if (requestCode == REQUEST_CODE_PICK_FILE) {
                Log.d(TAG, "onActivityResult: ${intent?.data}")
            }
        }

        callback = null

        return success
    }

    private fun createMediaPickerIntent(multiple: Boolean = false): Intent {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && SdkExtensions.getExtensionVersion(Build.VERSION_CODES.R) >= 2) {
            Intent(MediaStore.ACTION_PICK_IMAGES).apply {
                if (multiple) {
                    putExtra(MediaStore.EXTRA_PICK_IMAGES_MAX, MediaStore.getPickImagesMaxLimit())
                }
            }
        } else {
            createFilePickerIntent(null, listOf("image/*"), false, multiple)
        }
    }

    /**
     * Create an intent to pick a file(s) from the file system.
     *
     * @param directory The starting directory to pick from.
     * @param mimeTypes The mime types to filter the files by.
     * @param persist Whether to persist the permission to access the directory.
     * @param multiple Whether to allow multiple files to be picked.
     * @return The intent to pick a file(s).
     */
    private fun createFilePickerIntent(
        directory: Any?,
        mimeTypes: List<String>?,
        persist: Boolean,
        multiple: Boolean = false
    ): Intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        putExtra(Intent.EXTRA_ALLOW_MULTIPLE, multiple)
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

        // Persistable permission to access the directory later.
        if (persist) flags = flags or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION

        // Select files of any type or the provided mime types
        if (mimeTypes.isNullOrEmpty()) {
            type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("*/*"))
        } else {
            type = if (mimeTypes.size == 1) mimeTypes[0] else "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes.toTypedArray())
        }

        // Set the starting directory if provided
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, getStartDirectoryUri(directory))
        }
    }

    /**
     * Create an intent to pick a directory from the file system.
     *
     * @param directory The starting directory to pick from.
     * @param persist Whether to persist the permission to access the directory.
     * @return The intent to pick a directory.
     */
    private fun createDirectoryPickerIntent(
        directory: Any?,
        persist: Boolean
    ): Intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

        // Persistable permission to access the directory later.
        if (persist) flags = flags or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION

        // Set the starting directory if provided
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, getStartDirectoryUri(directory))
        }
    }

    /**
     * Get the URI for the starting directory to pick from.
     *
     * @param directory The starting directory to pick from.
     * @return The URI for the starting directory.
     */
    private fun getStartDirectoryUri(directory: Any?): Uri = when (directory) {
        is String -> directory.toUri()
        is PickerDirectory -> directory.documentUri
        else -> "$DOCUMENT_TREE_PRIMARY/document/primary%3A".toUri()
    }
}
