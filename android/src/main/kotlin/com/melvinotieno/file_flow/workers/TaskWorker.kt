package com.melvinotieno.file_flow.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.melvinotieno.file_flow.exceptions.FlowException
import com.melvinotieno.file_flow.helpers.ErrorCode
import com.melvinotieno.file_flow.helpers.ExceptionSerializer
import com.melvinotieno.file_flow.helpers.StateDataSerializer
import com.melvinotieno.file_flow.helpers.TaskSerializer
import com.melvinotieno.file_flow.models.FlowResult
import com.melvinotieno.file_flow.pigeons.FlowTask
import com.melvinotieno.file_flow.pigeons.TaskException
import com.melvinotieno.file_flow.pigeons.TaskState
import com.melvinotieno.file_flow.pigeons.TaskStateData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.InetSocketAddress
import java.net.MalformedURLException
import java.net.Proxy
import java.net.URL

abstract class TaskWorker(
    protected val context: Context, params: WorkerParameters
) : CoroutineWorker(context, params) {
    companion object {
        const val TAG = "TaskWorker"
        const val KEY_TASK = "Task"
        const val KEY_STATE = "TaskState"
        const val KEY_DATA = "TaskData"
        const val KEY_EXCEPTION = "TaskException"
    }

    lateinit var task: FlowTask

    override suspend fun doWork(): Result {
        val taskString = inputData.getString(KEY_TASK) ?: return Result.failure()

        task = try {
            Json.decodeFromString<FlowTask>(TaskSerializer, taskString)
        } catch (e: Exception) {
            Log.e(TAG, "[${task.id}] Failed to decode task", e)
            return Result.failure()
        }

        setProgress(workDataOf(KEY_STATE to TaskState.RUNNING.name))

        val result = try {
            handleRequest()
        } catch (e: FlowException) {
            val exception = TaskException(e.code, e.description, e.response).let {
                Log.e(TAG, "[${task.id}] ${it.message}", e)
                Json.encodeToString<TaskException>(ExceptionSerializer, it)
            }

            return Result.failure(workDataOf(KEY_EXCEPTION to exception))
        }

        val workData = result.data?.let {
            val data = Json.encodeToString<TaskStateData>(StateDataSerializer, it)
            workDataOf(KEY_STATE to result.state.name, KEY_DATA to data)
        } ?: workDataOf(KEY_STATE to result.state.name)

        return Result.success(workData)
    }

    private val proxy: Proxy
        get() {
            val address = task.proxyAddress
            val port = task.proxyPort?.toInt()

            return if (address != null && port != null) {
                Log.i(TAG, "[${task.id}] Using proxy: $address:$port")
                Proxy(Proxy.Type.HTTP, InetSocketAddress(address, port))
            } else {
                Proxy.NO_PROXY
            }
        }

    private suspend fun handleRequest(): FlowResult = withContext(Dispatchers.IO) {
        try {
            val connection = (URL(task.url).openConnection(proxy) as HttpURLConnection).apply {
                requestMethod = task.method
                connectTimeout = task.timeout.toInt() * 1000
                task.headers.forEach { (key, value) -> setRequestProperty(key, value) }
            }

            return@withContext processRequest(connection)
        } catch (e: FlowException) {
            throw e // rethrow FlowException
        } catch (e: MalformedURLException) {
            throw FlowException(ErrorCode.URL, e.message ?: "Invalid URL", e)
        } catch (e: Exception) {
            throw FlowException(ErrorCode.CONNECTION, e.message ?: "Failed to handle request", e)
        }
    }

    @Throws(FlowException::class)
    abstract suspend fun processRequest(connection: HttpURLConnection): FlowResult

    suspend fun transferBytes(
        inputStream: InputStream,
        outputStream: OutputStream,
    ): TaskState = coroutineScope {
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)

        var bytes: Int

        try {
            while (inputStream.read(buffer).also { bytes = it } >= 0) {
                if (!isActive) {
                    return@coroutineScope TaskState.FAILED
                }

                outputStream.write(buffer, 0, bytes)
            }

            TaskState.COMPLETED
        } catch (e: Exception) {
            throw FlowException(ErrorCode.FILESYSTEM, e.message ?: "Failed to transfer bytes", e)
        }
    }
}
