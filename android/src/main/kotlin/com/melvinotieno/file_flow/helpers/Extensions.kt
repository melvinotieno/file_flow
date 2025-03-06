package com.melvinotieno.file_flow.helpers

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import com.melvinotieno.file_flow.pigeons.PickerDirectory
import com.melvinotieno.file_flow.pigeons.Task
import com.melvinotieno.file_flow.pigeons.StorageDirectory
import io.flutter.util.PathUtils

/**
 * Get the directory path for the picker directory.
 */
val PickerDirectory.dirPath: String
    get() = when (this) {
        PickerDirectory.DOCUMENTS -> Environment.DIRECTORY_DOCUMENTS
        PickerDirectory.DOWNLOADS -> Environment.DIRECTORY_DOWNLOADS
        PickerDirectory.IMAGES -> Environment.DIRECTORY_PICTURES
        PickerDirectory.VIDEO -> Environment.DIRECTORY_MOVIES
        PickerDirectory.AUDIO -> Environment.DIRECTORY_MUSIC
    }

/**
 * Get the persistable URI for the picker directory.
 */
val PickerDirectory.persistableUri: Uri
    get() = "$DOCUMENT_TREE_PRIMARY$dirPath".toUri()

/**
 * Get the document URI for the picker directory.
 */
val PickerDirectory.documentUri: Uri
    get() = "$persistableUri/document/primary%3A$dirPath".toUri()

/**
 * Returns if the storage directory is intended for shared storage.
 */
val StorageDirectory.isSharedStorage: Boolean
    get() {
        return when (this) {
            StorageDirectory.APPLICATION_CACHE,
            StorageDirectory.APPLICATION_DOCUMENTS,
            StorageDirectory.APPLICATION_LIBRARY,
            StorageDirectory.APPLICATION_SUPPORT,
            StorageDirectory.TEMPORARY -> false
            StorageDirectory.DOWNLOADS,
            StorageDirectory.IMAGES,
            StorageDirectory.VIDEO,
            StorageDirectory.AUDIO,
            StorageDirectory.FILES -> true
        }
    }

/**
 * Get the MediaStore URI for the storage directory.
 */
val StorageDirectory.mediaStoreUri: Uri
    @RequiresApi(Build.VERSION_CODES.Q)
    get() {
        return when (this) {
            StorageDirectory.DOWNLOADS -> MediaStore.Downloads.EXTERNAL_CONTENT_URI
            StorageDirectory.IMAGES -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            StorageDirectory.VIDEO -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            StorageDirectory.AUDIO -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            StorageDirectory.FILES -> MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
            else -> throw IllegalArgumentException()
        }
    }

/**
 * Get the path of the storage directory.
 *
 * @param context The context to use for getting the base directory.
 * @return The path of the storage directory.
 */
fun StorageDirectory.getPath(context: Context): String {
    return when (this) {
        StorageDirectory.APPLICATION_CACHE -> context.cacheDir.path
        StorageDirectory.APPLICATION_DOCUMENTS -> PathUtils.getDataDirectory(context)
        StorageDirectory.APPLICATION_LIBRARY -> throw IllegalArgumentException()
        StorageDirectory.APPLICATION_SUPPORT -> PathUtils.getFilesDir(context)
        StorageDirectory.TEMPORARY -> context.cacheDir.path
        StorageDirectory.DOWNLOADS -> Environment.DIRECTORY_DOWNLOADS
        StorageDirectory.IMAGES -> Environment.DIRECTORY_PICTURES
        StorageDirectory.VIDEO -> Environment.DIRECTORY_MOVIES
        StorageDirectory.AUDIO -> Environment.DIRECTORY_MUSIC
        StorageDirectory.FILES -> Environment.DIRECTORY_DOCUMENTS
    }
}

/**
 * Get the relative path of the task directory in relation to the base directory.
 *
 * If the task directory is not set, the base directory is returned.
 *
 * @param context The context to use for getting the base directory.
 * @return The relative path of the task directory.
 */
fun Task.getRelativePath(context: Context): String {
    val basePath = this.baseDirectory.getPath(context)
    return if (this.directory.isNullOrEmpty()) basePath else "${basePath}/${this.directory}"
}
