package com.melvinotieno.file_flow.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.melvinotieno.file_flow.exceptions.FlowException
import com.melvinotieno.file_flow.helpers.ProgressDataSerializer
import com.melvinotieno.file_flow.helpers.encode
import com.melvinotieno.file_flow.helpers.decode
import com.melvinotieno.file_flow.pigeons.Task
import com.melvinotieno.file_flow.pigeons.TaskErrorCode
import com.melvinotieno.file_flow.pigeons.TaskException
import com.melvinotieno.file_flow.pigeons.TaskProgressData
import com.melvinotieno.file_flow.pigeons.TaskResumeData
import com.melvinotieno.file_flow.pigeons.TaskState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.InetSocketAddress
import java.net.MalformedURLException
import java.net.Proxy
import java.net.URL
import java.util.Collections

abstract class TaskWorker(
    protected val context: Context, params: WorkerParameters
) : CoroutineWorker(context, params) {
    companion object {
        const val TAG = "TaskWorker"
        const val KEY_TASK = "Task"
        const val KEY_RESUME = "TaskResume"
        const val KEY_STATE = "TaskState"
        const val KEY_DATA = "TaskData"
        const val KEY_PROGRESS = "TaskProgress"
        const val KEY_EXCEPTION = "TaskException"

        private const val PROGRESS_UPDATE_INTERVAL_MS = 2000L // 2 seconds
        private const val PROGRESS_UPDATE_THRESHOLD_BYTES = 1024 * 1024 // 1MB

        private val pausedTasks = Collections.synchronizedSet(mutableSetOf<String>())

        fun pauseTask(taskId: String) = pausedTasks.add(taskId)
    }

    lateinit var task: Task

    val tempDirPath: String by lazy {
        "${context.cacheDir.path}/com.melvinotieno.file_flow".also { File(it).mkdirs() }
    }

    val proxy: Proxy by lazy {
        task.proxyAddress?.let { address ->
            task.proxyPort?.toInt()?.let { port ->
                Log.i(TAG, "[${task.id}] Using proxy: $address:$port")
                Proxy(Proxy.Type.HTTP, InetSocketAddress(address, port))
            }
        } ?: Proxy.NO_PROXY
    }

    var resumeData: TaskResumeData? = null

    var resumedBytes = 0L
    var transferredBytes = 0L

    private var bytesSinceLastUpdate = 0L
    private var lastProgressUpdateTime = 0L
    private var networkSpeed = 0L

    protected val isPaused: Boolean
        get() = pausedTasks.contains(task.id)

    override suspend fun doWork(): Result {
        task = Task.decode(inputData.getString(KEY_TASK)) ?: return Result.failure()
        resumeData = TaskResumeData.decode(inputData.getString(KEY_RESUME))

        // Update the task's state to running
        setProgress(workDataOf(KEY_STATE to TaskState.RUNNING.name))

        // Start network speed calculation
        val networkSpeedJob = CoroutineScope(Dispatchers.IO).launch { calculateNetworkSpeed() }

        return try {
            handleRequest().let { (taskState, taskData) ->
                Result.success(workDataOf(KEY_STATE to taskState.name, KEY_DATA to taskData))
            }
        } catch (e: FlowException) {
            handleException(e)
        } catch (e: Exception) {
            handleException(FlowException(TaskErrorCode.UNKNOWN, e.message ?: "unknown error", e))
        } finally {
            networkSpeedJob.cancel()
        }
    }

    open suspend fun handleRequest(): Pair<TaskState, String?> = withContext(Dispatchers.IO) {
        try {
            with(URL(task.url).openConnection(proxy) as HttpURLConnection) {
                requestMethod = task.method
                connectTimeout = task.timeout.toInt() * 1000
                task.headers.forEach { (key, value) -> setRequestProperty(key, value) }

                return@with processRequest(this)
            }
        } catch (e: FlowException) {
            throw e // rethrow FlowException
        } catch (e: MalformedURLException) {
            throw FlowException(TaskErrorCode.URL, e.message ?: "invalid url", e)
        } catch (e: Exception) {
            // Assumes all other exceptions are connection related
            throw FlowException(TaskErrorCode.CONNECTION, e.message ?: "connection error", e)
        }
    }

    abstract suspend fun processRequest(connection: HttpURLConnection): Pair<TaskState, String?>

    abstract fun cleanup()

    suspend fun transferBytes(
        inputStream: InputStream,
        outputStream: OutputStream,
        contentLength: Long
    ): TaskState = withContext(Dispatchers.IO) {
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        val expectedBytes = contentLength + resumedBytes

        var readBytes: Int

        try {
            while (inputStream.read(buffer).also { readBytes = it } >= 0) {
                if (!isActive) {
                    return@withContext TaskState.FAILED
                }

                if (isPaused) {
                    return@withContext TaskState.PAUSED
                }

                outputStream.write(buffer, 0, readBytes)
                sendTaskProgress(readBytes, expectedBytes)
            }
            TaskState.COMPLETED
        } catch (e: Exception) {
            throw FlowException(TaskErrorCode.TRANSFER, e.message ?: "transfer error", e)
        }
    }

    suspend fun sendTaskProgress(readBytes: Int, expectedBytes: Long) {
        transferredBytes += readBytes
        bytesSinceLastUpdate += readBytes

        val currentTime = System.nanoTime()
        val elapsedTime = currentTime - lastProgressUpdateTime

        val shouldUpdate = if (expectedBytes < PROGRESS_UPDATE_THRESHOLD_BYTES) {
            elapsedTime >= PROGRESS_UPDATE_INTERVAL_MS * 1000000
        } else {
            bytesSinceLastUpdate >= PROGRESS_UPDATE_THRESHOLD_BYTES
        }

        if (shouldUpdate) {
            val progress = (transferredBytes.toDouble() / expectedBytes * 100).toInt()

            val data = TaskProgressData(expectedBytes, transferredBytes, networkSpeed).let {
                Json.encodeToString<TaskProgressData>(ProgressDataSerializer, it)
            }

            setProgress(workDataOf(KEY_PROGRESS to progress, KEY_DATA to data))

            lastProgressUpdateTime = currentTime
            bytesSinceLastUpdate = 0L
        }
    }

    private suspend fun calculateNetworkSpeed() = coroutineScope {
        while (isActive) {
            val startBytes = transferredBytes
            delay(1000) // Update networkSpeed every second
            val endBytes = transferredBytes
            networkSpeed = endBytes - startBytes
        }
    }

    private fun handleException(e: FlowException): Result {
        val exception = TaskException(e.code, e.description, e.response).let {
            Log.e(TAG, "[${task.id}] ${it.message}", e)
            it.encode()
        }
        return Result.failure(workDataOf(KEY_EXCEPTION to exception))
    }
}
