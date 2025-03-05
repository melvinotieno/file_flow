package com.melvinotieno.file_flow

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.provider.DocumentsContract
import android.util.Log
import com.melvinotieno.file_flow.pigeons.PickerFlutterError
import com.melvinotieno.file_flow.pigeons.PickerHostApi
import io.flutter.plugin.common.PluginRegistry

class PickerManager : PickerHostApi, PluginRegistry.ActivityResultListener {
    companion object {
        private const val TAG = "PickerManager"
        private const val REQUEST_CODE_PICK_DIRECTORY = 100
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

    override fun pickDirectory(
        directory: Any?,
        persist: Boolean,
        callback: (Result<String>) -> Unit
    ) {
        if (this.callback != null) {
            Log.e(TAG, "Another picker is already active")
            callback(Result.failure(PickerFlutterError("active", "Directory picker is active")))
            return
        }

        this.callback = callback

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

            // Persistable permission to access the directory later.
            if (persist) flags = flags or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION

            // Set the starting directory if provided
            if (directory != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                putExtra(DocumentsContract.EXTRA_INITIAL_URI, directory as? String)
            }
        }

        activity?.startActivityForResult(intent, REQUEST_CODE_PICK_DIRECTORY)
    }

    override fun pickFile(
        directory: Any?,
        extensions: List<String>?,
        persist: Boolean,
        callback: (Result<String>) -> Unit
    ) {
        if (this.callback != null) {
            Log.e(TAG, "Another picker is already active")
            callback(Result.failure(PickerFlutterError("active")))
            return
        }
    }

    override fun pickFiles(
        directory: Any?,
        extensions: List<String>?,
        persist: Boolean,
        callback: (Result<List<String>>) -> Unit
    ) {
        if (this.callback != null) {
            Log.e(TAG, "Another picker is already active")
            callback(Result.failure(PickerFlutterError("active")))
            return
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?): Boolean {
        var success = false

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_PICK_DIRECTORY) {
                val directoryUri = intent?.data

                if (directoryUri != null) {
                    callback?.invoke(Result.success(directoryUri.toString()))
                } else {
                    callback?.invoke(Result.failure(PickerFlutterError("cancelled")))
                }
            }
        }

        return success
    }
}
