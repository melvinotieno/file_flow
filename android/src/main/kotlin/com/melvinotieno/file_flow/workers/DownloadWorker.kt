package com.melvinotieno.file_flow.workers

import android.content.Context
import android.util.Log
import androidx.work.WorkerParameters
import com.melvinotieno.file_flow.exceptions.FlowException
import com.melvinotieno.file_flow.helpers.copyDownloadFile
import com.melvinotieno.file_flow.helpers.encode
import com.melvinotieno.file_flow.helpers.getDownloadFileName
import com.melvinotieno.file_flow.pigeons.TaskCompleteData
import com.melvinotieno.file_flow.pigeons.TaskErrorCode
import com.melvinotieno.file_flow.pigeons.TaskState
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import kotlin.random.Random

class DownloadWorker(context: Context, params: WorkerParameters) : TaskWorker(context, params) {
    private val tempFilePath: String by lazy {
        "$tempDirPath/${Random.nextInt(Int.MAX_VALUE)}"
    }

    override suspend fun processRequest(connection: HttpURLConnection): Pair<TaskState, String?> {
        val response = mapOf(
            "code" to connection.responseCode,
            "headers" to connection.headerFields,
            "message" to connection.responseMessage,
        ).toString()

        return if (connection.responseCode in 200..206) {
            val taskState = BufferedInputStream(connection.inputStream).use { inputStream ->
                FileOutputStream(File(tempFilePath)).use { outputStream ->
                    transferBytes(inputStream, outputStream, connection.contentLength.toLong())
                }
            }

            when (taskState) {
                TaskState.COMPLETED -> handleCompletedState(connection.headerFields, response)
                else -> {
                    cleanup()
                    throw FlowException(TaskErrorCode.UNKNOWN, "Unexpected task state: $taskState")
                }
            }
        } else {
            throw FlowException(TaskErrorCode.HTTP, "HTTP error", response)
        }
    }

    override fun cleanup() {
        try {
            File(tempFilePath).takeIf { it.exists() }?.delete()
        } catch (e: Exception) {
            Log.e(TAG, "[${task.id}] Failed to delete temp file: $tempFilePath", e)
        }
    }

    private fun handleCompletedState(
        headers: Map<String, List<String>>, response: String
    ): Pair<TaskState, String?> {
        val filename = getDownloadFileName(task.url!!, headers, task.filename)

        return copyDownloadFile(context, task, tempFilePath, filename).let { (filePath, mimeType) ->
            cleanup()
            filePath ?: throw FlowException(TaskErrorCode.FILESYSTEM, "Failed to download file")
            Pair(TaskState.COMPLETED, TaskCompleteData(filePath, mimeType, response).encode())
        }
    }
}
