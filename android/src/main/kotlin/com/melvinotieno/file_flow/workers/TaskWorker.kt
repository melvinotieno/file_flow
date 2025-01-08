package com.melvinotieno.file_flow.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.melvinotieno.file_flow.helpers.TaskSerializer
import com.melvinotieno.file_flow.pigeons.Task
import com.melvinotieno.file_flow.pigeons.TaskState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.URL

abstract class TaskWorker(
    protected val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val TAG = "TaskWorker"
        const val KEY_TASK = "Task"
        const val BUFFER_SIZE = 2 shl 12
    }

    lateinit var task: Task

    @ExperimentalSerializationApi
    override suspend fun doWork(): Result {
        val taskJson = inputData.getString(KEY_TASK) ?: return Result.failure()

        task = try {
            withContext(Dispatchers.Default) {
                Json.decodeFromString<Task>(TaskSerializer, taskJson)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to decode task", e)
            return Result.failure()
        }

        handleRequest()

        return Result.success()
    }

    fun transferBytes(inputStream: InputStream, outputStream: OutputStream): TaskState {
        val buffer = ByteArray(BUFFER_SIZE)
        var bytes = inputStream.read(buffer)
        var downloaded = 0L

        while (bytes >= 0) {
            outputStream.write(buffer, 0, bytes)
            downloaded += bytes
            bytes = inputStream.read(buffer)
        }

        return TaskState.COMPLETED
    }

    private suspend fun handleRequest(): TaskState = withContext(Dispatchers.IO) {
        try {
            val proxyAddress = task.proxyAddress
            val proxyPort = task.proxyPort?.takeIf { it > 0 }

            val proxy = if (!proxyAddress.isNullOrBlank() && proxyPort != null) {
                Log.i("$TAG (${task.id})", "Using proxy $proxyAddress:$proxyPort")
                Proxy(Proxy.Type.HTTP, InetSocketAddress(proxyAddress, proxyPort.toInt()))
            } else {
                Proxy.NO_PROXY
            }

            val connection = (URL(task.url).openConnection(proxy) as HttpURLConnection).apply {
                requestMethod = task.method
                connectTimeout = task.timeout.toInt() * 1000
                task.headers.forEach { (key, value) -> setRequestProperty(key, value) }
            }

            return@withContext processRequest(connection)
        } catch (e: Exception) {
            Log.e("$TAG (${task.id})", "Failed to process request", e)
            return@withContext TaskState.FAILED
        }
    }

    abstract suspend fun processRequest(connection: HttpURLConnection): TaskState
}
