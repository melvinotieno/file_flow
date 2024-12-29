package com.melvinotieno.file_flow.helpers

import com.melvinotieno.file_flow.pigeons.StorageDirectory

fun StorageDirectory.isPublicStorage(): Boolean {
    return when (this) {
        StorageDirectory.APPLICATION_DOCUMENTS -> false
        StorageDirectory.DOWNLOADS -> true
    }
}
