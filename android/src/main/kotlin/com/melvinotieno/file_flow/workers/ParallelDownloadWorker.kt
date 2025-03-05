package com.melvinotieno.file_flow.workers

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.work.WorkerParameters
import com.melvinotieno.file_flow.exceptions.FlowException
import com.melvinotieno.file_flow.pigeons.TaskErrorCode
import com.melvinotieno.file_flow.pigeons.TaskState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.properties.Delegates
import kotlin.random.Random

class ParallelDownloadWorker(
    context: Context, params: WorkerParameters
) : TaskWorker(context, params) {
    private val tempChunksPath by lazy {
        "$tempDirPath/${Random.nextInt(Int.MAX_VALUE)}".also { File(it).mkdirs() }
    }

    private val tempFilePath: String by lazy {
        "$tempDirPath/${Random.nextInt(Int.MAX_VALUE)}"
    }

    private var fileSize by Delegates.notNull<Long>()

    override suspend fun handleRequest(): Pair<TaskState, String?> = coroutineScope {
        val urls = getValidUrls()

        // Get the file size from the first url
        fileSize = getFileSize(urls.first())

        // Calculate the number of chunks and the size of each chunk
        val totalChunks = (task.chunks?.toInt() ?: 1) * urls.size
        val chunkSize = fileSize / totalChunks

        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

        try {
            val downloadJobs = (0 until totalChunks).map { chunkIndex ->
                scope.async {
                    val url = urls[chunkIndex % urls.size]
                    val startByte = chunkIndex * chunkSize
                    val endByte = startByte + chunkSize - 1
                    Log.d(TAG, "[${task.id}] Downloading chunk $chunkIndex: $startByte-$endByte")

                    with(url.openConnection(proxy) as HttpURLConnection) {
                        setRequestProperty("Range", "bytes=$startByte-$endByte")
                        processRequest(this)
                    }
                }
            }

            val results = downloadJobs.map { it.await() }

            if (results.all { it.first == TaskState.COMPLETED }) {
                combineChunks()
                Pair(TaskState.COMPLETED, null)
            } else {
                Pair(TaskState.FAILED, "Failed to download all chunks")
            }
        } catch (e: Exception) {
            Log.e(TAG, "[${task.id}] Failed to download file: ${e.message}")
            scope.cancel()
            Pair(TaskState.FAILED, e.message)
        }
    }

    override suspend fun processRequest(connection: HttpURLConnection): Pair<TaskState, String?> {
        val response = mapOf(
            "code" to connection.responseCode,
            "headers" to connection.headerFields,
            "message" to connection.responseMessage,
        ).toString()

        return if (connection.responseCode in 200..206) {
            val endBytes = connection.getRequestProperty("Range")?.substringAfter("bytes=")
                ?.substringAfter("-")?.toIntOrNull() ?: 0

            val taskState = BufferedInputStream(connection.inputStream).use { inputStream ->
                FileOutputStream(File("$tempChunksPath/chunk_$endBytes")).use { outputStream ->
                    transferBytes(inputStream, outputStream, fileSize)
                }
            }

            return Pair(taskState, "")
        } else {
            throw FlowException(TaskErrorCode.HTTP, "HTTP error", response)
        }
    }

    override fun cleanup() {
        TODO("Not yet implemented")
    }

    private fun getValidUrls(): List<URL> {
        return task.urls?.mapNotNull { url ->
            runCatching { URL(url) }.onFailure {
                Log.w(TAG, "[${task.id}] Removing invalid url $url from urls")
            }.getOrNull()
        }?.takeIf { urls ->
            urls.isNotEmpty()
        } ?: throw FlowException(TaskErrorCode.URL, "No valid url provided in urls")
    }

    private suspend fun getFileSize(url: URL): Long = withContext(Dispatchers.IO) {
        try {
            with(url.openConnection(proxy) as HttpURLConnection) {
                requestMethod = "HEAD"
                connectTimeout = task.timeout.toInt() * 1000
                task.headers.forEach { (key, value) -> setRequestProperty(key, value) }

                connect()

                val contentLength = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    contentLengthLong
                } else {
                    contentLength.toLong()
                }

                disconnect()

                return@with contentLength
            }
        } catch (e: Exception) {
            throw FlowException(TaskErrorCode.CONNECTION, e.message ?: "Failed to get file size")
        }
    }

    private suspend fun combineChunks() = withContext(Dispatchers.IO) {
        val chunks = File(tempChunksPath).listFiles()?.sortedBy { it.name } ?: return@withContext

        FileOutputStream(File(tempFilePath)).use { outputStream ->
            chunks.forEach { chunk ->
                FileInputStream(chunk).use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }

        File(tempChunksPath).deleteRecursively()
    }
}
