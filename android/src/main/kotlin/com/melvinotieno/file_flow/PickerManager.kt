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
import com.melvinotieno.file_flow.helpers.persistableUri
import com.melvinotieno.file_flow.pigeons.PickerDirectory
import com.melvinotieno.file_flow.pigeons.PickerFlutterError
import com.melvinotieno.file_flow.pigeons.PickerHostApi
import com.melvinotieno.file_flow.pigeons.PickerMedia
import io.flutter.plugin.common.PluginRegistry

class PickerManager : PickerHostApi, PluginRegistry.ActivityResultListener {
    companion object {
        private const val TAG = "PickerManager"
        private const val REQUEST_CODE_PICK_DIRECTORY = 100
        private const val REQUEST_CODE_PICK_FILE = 101
        private const val REQUEST_CODE_PICK_FILES = 102
    }

    private var activity: Activity? = null
    private var directory: Any? = null
    private var exact: Any = false
    private var persist: Boolean = false
    private var callback: ((Result<Any>) -> Unit)? = null

    private val directoryUri: Uri?
        get() = when (directory) {
            is String -> getPersistableUri(directory as String)
            is PickerDirectory -> (directory as PickerDirectory).persistableUri
            else -> null
        }

    /**
     * Set the activity to use for calling startActivityForResult.
     *
     * @param activity The activity to use for the picker.
     */
    fun setActivity(activity: Activity?) {
        this.activity = activity
    }

    /**
     * Check if the corresponding uri of value has persisted permissions.
     *
     * @param value The value to check for persisted permissions.
     * @return True if the URI has persisted permissions, false otherwise.
     */
    override fun persisted(value: Any): Boolean {
        val uri = when (value) {
            is String -> getPersistableUri(value)
            is PickerDirectory -> value.persistableUri
            else -> return false
        }

        return activity?.contentResolver?.persistedUriPermissions?.any { it.uri == uri } == true
    }

    /**
     * Pick a directory from the file system.
     *
     * @param directory The starting directory to pick from.
     * @param exact Whether to allow only the exact directory to be picked or any subdirectory.
     * @param persist Whether to persist the permission to access the directory.
     * @param callback The callback to invoke with the result.
     * @throws IllegalStateException If a picker is already active.
     */
    @Suppress("UNCHECKED_CAST")
    override fun pickDirectory(
        directory: Any?, exact: Any, persist: Boolean, callback: (Result<String>) -> Unit
    ) {
        if (this.callback != null) throw IllegalStateException("A picker is already active")
        this.directory = directory
        this.exact = exact
        this.persist = persist
        this.callback = { result -> callback(result as Result<String>) }
        val intent = createDirectoryPickerIntent(directory, persist)
        activity?.startActivityForResult(intent, REQUEST_CODE_PICK_DIRECTORY)
    }

    /**
     * Pick a file from the file system.
     *
     * @param directory The starting directory to pick from.
     * @param mimeTypes The mime types to filter the files by.
     * @param exact The file must be in the exact directory or any subdirectory.
     * @param persist Whether to persist the permission to access the file.
     * @param callback The callback to invoke with the result.
     * @throws IllegalStateException If a picker is already active.
     */
    @Suppress("UNCHECKED_CAST")
    override fun pickFile(
        directory: Any?,
        mimeTypes: List<String>?,
        exact: Boolean,
        persist: Boolean,
        callback: (Result<String>) -> Unit
    ) {
        if (this.callback != null) throw IllegalStateException("A picker is already active")
        this.directory = directory
        this.exact = exact
        this.persist = persist
        this.callback = { result -> callback(result as Result<String>) }
        val intent = createFilePickerIntent(directory, mimeTypes, persist)
        activity?.startActivityForResult(intent, REQUEST_CODE_PICK_FILE)
    }

    /**
     * Pick multiple files from the file system.
     *
     * @param directory The starting directory to pick from.
     * @param mimeTypes The mime types to filter the files by.
     * @param exact The files must be in the exact directory or any subdirectory.
     * @param persist Whether to persist the permission to access the files.
     * @param callback The callback to invoke with the result.
     * @throws IllegalStateException If a picker is already active.
     */
    @Suppress("UNCHECKED_CAST")
    override fun pickFiles(
        directory: Any?,
        mimeTypes: List<String>?,
        exact: Boolean,
        persist: Boolean,
        callback: (Result<List<String>>) -> Unit
    ) {
        if (this.callback != null) throw IllegalStateException("A picker is already active")
        this.directory = directory
        this.exact = exact
        this.persist = persist
        this.callback = { result -> callback(result as Result<List<String>>) }
        val intent = createFilePickerIntent(directory, mimeTypes, persist, true)
        activity?.startActivityForResult(intent, REQUEST_CODE_PICK_FILES)
    }

    /**
     * Pick a media file from the file system.
     *
     * @param media The type of media to pick.
     * @param persist Whether to persist the permission to access the media file.
     * @param callback The callback to invoke with the result.
     * @throws IllegalStateException If a picker is already active.
     */
    @Suppress("UNCHECKED_CAST")
    override fun pickMediaFile(
        media: PickerMedia, persist: Boolean, callback: (Result<String>) -> Unit
    ) {
        if (this.callback != null) throw IllegalStateException("A picker is already active")
        this.persist = persist
        this.callback = { result -> callback(result as Result<String>) }
        val intent = createMediaPickerIntent(media, persist)
        activity?.startActivityForResult(intent, REQUEST_CODE_PICK_FILE)
    }

    /**
     * Pick multiple media files from the file system.
     *
     * @param media The type of media to pick.
     * @param persist Whether to persist the permission to access the media files.
     * @param callback The callback to invoke with the result.
     * @throws IllegalStateException If a picker is already active.
     */
    @Suppress("UNCHECKED_CAST")
    override fun pickMediaFiles(
        media: PickerMedia, persist: Boolean, callback: (Result<List<String>>) -> Unit
    ) {
        if (this.callback != null) throw IllegalStateException("A picker is already active")
        this.persist = persist
        this.callback = { result -> callback(result as Result<List<String>>) }
        val intent = createMediaPickerIntent(media, persist, true)
        activity?.startActivityForResult(intent, REQUEST_CODE_PICK_FILES)
    }

    /**
     * Handle the result of the picker activity.
     *
     * @param requestCode The request code of the activity.
     * @param resultCode The result code of the activity.
     * @param intent The intent containing the result data.
     * @return True if the result was handled, false otherwise.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?): Boolean {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_PICK_DIRECTORY -> handleDirectoryResult(intent)
                REQUEST_CODE_PICK_FILE -> handleFileResult(intent)
                REQUEST_CODE_PICK_FILES -> handleFilesResult(intent)
                else -> return false
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e(TAG, "No file/directory was selected")
            callback?.invoke(Result.failure(PickerFlutterError("canceled")))
        }

        directory = null
        exact = false
        persist = false
        callback = null

        return true
    }

    /**
     * Create an intent to pick a directory from the file system.
     *
     * @param directory The starting directory to pick from.
     * @param persist Whether to persist the permission to access the directory.
     * @return The intent to pick a directory.
     */
    private fun createDirectoryPickerIntent(
        directory: Any?, persist: Boolean
    ): Intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

        // Persistable permission to access the directory later.
        if (persist) flags = flags or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION

        // Set the starting directory.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, getStartDirectoryUri(directory))
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
        directory: Any?, mimeTypes: List<String>?, persist: Boolean, multiple: Boolean = false
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

        // Set the starting directory.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, getStartDirectoryUri(directory))
        }
    }

    /**
     * Create an intent to pick a media file(s) from the file system.
     *
     * @param media The type of media to pick.
     * @param persist Whether to persist the permission to access the media file.
     * @param multiple Whether to allow multiple files to be picked.
     * @return The intent to pick a media file(s).
     */
    private fun createMediaPickerIntent(
        media: PickerMedia, persist: Boolean, multiple: Boolean = false
    ): Intent {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && SdkExtensions.getExtensionVersion(
                Build.VERSION_CODES.R
            ) >= 2
        ) {
            Intent(MediaStore.ACTION_PICK_IMAGES).apply {
                if (multiple) {
                    putExtra(MediaStore.EXTRA_PICK_IMAGES_MAX, MediaStore.getPickImagesMaxLimit())
                }
                type = if (media == PickerMedia.IMAGE) "image/*" else "video/*"
            }
        } else {
            val (directory, mimeTypes) = if (media == PickerMedia.IMAGE) {
                PickerDirectory.IMAGES to listOf("image/*")
            } else {
                PickerDirectory.VIDEO to listOf("video/*")
            }
            createFilePickerIntent(directory, mimeTypes, persist, multiple)
        }
    }

    /**
     * Handle the result of picking a directory.
     *
     * @param intent The intent containing the result data.
     */
    private fun handleDirectoryResult(intent: Intent?) {
        intent?.data?.let { directoryUri ->
            checkIfExactOrSubdirectoryUri(directoryUri)?.let {
                if (it == false) {
                    callback?.invoke(Result.failure(PickerFlutterError("invalid")))
                    return
                }
            }

            if (persist) {
                activity?.contentResolver?.takePersistableUriPermission(
                    directoryUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
            }

            callback?.invoke(Result.success(directoryUri.toString()))
        }
    }

    /**
     * Handle the result of picking a file.
     *
     * @param intent The intent containing the result data.
     */
    private fun handleFileResult(intent: Intent?, toList: Boolean = false) {
        intent?.data?.let { fileUri ->
            checkIfSubdirectoryUri(fileUri, true)?.let {
                if (it == false) {
                    callback?.invoke(Result.failure(PickerFlutterError("invalid")))
                    return
                }
            }

            if (persist) {
                activity?.contentResolver?.takePersistableUriPermission(
                    fileUri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }

            val result = if (toList) listOf(fileUri.toString()) else fileUri.toString()
            callback?.invoke(Result.success(result))
        }
    }

    /**
     * Handle the result of picking multiple files.
     *
     * @param intent The intent containing the result data.
     */
    private fun handleFilesResult(intent: Intent?) {
        intent?.data?.let { return handleFileResult(intent, true) }

        val fileUris = mutableListOf<String>()
        intent?.clipData?.let { clipData ->
            for (i in 0 until clipData.itemCount) {
                val fileUri = clipData.getItemAt(i).uri
                if (persist) {
                    activity?.contentResolver?.takePersistableUriPermission(
                        fileUri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }
                fileUris.add(fileUri.toString())
            }
        }

        // Files cannot be selected across multiple directories, so we only need to check one uri
        checkIfSubdirectoryUri(fileUris.first().toUri(), true)?.let {
            if (it == false) {
                callback?.invoke(Result.failure(PickerFlutterError("invalid")))
                return
            }
        }

        callback?.invoke(Result.success(fileUris))
    }

    /**
     * Check if the URI is the exact or a subdirectory of the starting directory.
     *
     * @param uri The URI to check.
     * @return True if the URI is the exact or a subdirectory, false otherwise.
     */
    private fun checkIfExactOrSubdirectoryUri(uri: Uri): Boolean? {
        if (directory == null || exact == false) return null

        // Downloads directory cannot be selected, therefore check for subdirectory
        if (directory is PickerDirectory && directory == PickerDirectory.DOWNLOADS) {
            return checkIfSubdirectoryUri(uri)
        }

        return directoryUri?.let {
            if (exact == true) it == uri else it == uri || checkIfSubdirectoryUri(uri) == true
        }
    }

    /**
     * Check if the URI is a subdirectory of the starting directory.
     *
     * @param uri The URI to check.
     * @return True if the URI is a subdirectory, false otherwise.
     */
    private fun checkIfSubdirectoryUri(uri: Uri, isFile: Boolean = false): Boolean? {
        if (directory == null || exact == false) return null

        return directoryUri?.let {
            val path = if (isFile) it.toString().replace("tree", "document") else it.toString()
            uri.toString().startsWith(path)
        }
    }

    /**
     * Get the URI for the starting directory to pick from.
     *
     * @param directory The starting directory to pick from.
     * @return The URI for the starting directory.
     */
    private fun getStartDirectoryUri(directory: Any?): Uri = when (directory) {
        is String -> getDocumentUri(directory)
        is PickerDirectory -> directory.documentUri
        else -> "$DOCUMENT_TREE_PRIMARY/document/primary%3A".toUri()
    }

    /**
     * Get the document URI for the directory.
     *
     * @param directory The directory to get the document URI for.
     * @return The document URI for the directory.
     */
    private fun getDocumentUri(directory: String): Uri {
        val uri = directory.toUri()
        return if (uri.scheme == "context") uri else "${getPersistableUri(directory)}$directory/document/primary%3A$directory".toUri()
    }

    /**
     * Get the persistable URI for the directory.
     *
     * @param directory The directory to get the persistable URI for.
     * @return The persistable URI for the directory.
     */
    private fun getPersistableUri(directory: String): Uri {
        val uri = directory.toUri()
        return if (uri.scheme == "context") uri else "$DOCUMENT_TREE_PRIMARY$directory".toUri()
    }
}
