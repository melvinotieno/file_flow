package com.melvinotieno.file_flow

import android.content.Context
import android.util.Log
import androidx.lifecycle.Observer
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.melvinotieno.file_flow.helpers.TaskSerializer
import com.melvinotieno.file_flow.pigeons.FileFlowHostApi
import com.melvinotieno.file_flow.pigeons.PigeonEventSink
import com.melvinotieno.file_flow.pigeons.StreamTaskEventsStreamHandler
import com.melvinotieno.file_flow.pigeons.Task
import com.melvinotieno.file_flow.pigeons.TaskEvent
import com.melvinotieno.file_flow.pigeons.TaskState
import com.melvinotieno.file_flow.pigeons.TaskStatus
import com.melvinotieno.file_flow.pigeons.TaskType
import com.melvinotieno.file_flow.workers.DownloadWorker
import com.melvinotieno.file_flow.workers.TaskWorker
import com.melvinotieno.file_flow.workers.UploadWorker
import io.flutter.embedding.engine.plugins.FlutterPlugin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

class FileFlowPlugin: FlutterPlugin, FileFlowHostApi, StreamTaskEventsStreamHandler() {
    companion object {
        const val TAG = "FileFlow"
    }

    private lateinit var context: Context
    private var eventSink: PigeonEventSink<TaskEvent>? = null
    private val workManager by lazy { WorkManager.getInstance(context) }

    private val workInfoObserver = Observer<List<WorkInfo>> { workInfoList ->
        workInfoList.forEach { handleWorkInfoState(it) }
    }

    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        context = binding.applicationContext
        FileFlowHostApi.setUp(binding.binaryMessenger, this)
        register(binding.binaryMessenger, this) // StreamTaskEventsStreamHandler
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        FileFlowHostApi.setUp(binding.binaryMessenger, null)
    }

    override fun onListen(p0: Any?, sink: PigeonEventSink<TaskEvent>) {
        Log.i(TAG, "Listening to task events")
        eventSink = sink
        workManager.getWorkInfosByTagLiveData(TAG).observeForever(workInfoObserver)
    }

    override fun onCancel(p0: Any?) {
        eventSink?.endOfStream()
        eventSink = null
        workManager.getWorkInfosByTagLiveData(TAG).removeObserver(workInfoObserver)
    }

    override fun enqueue(task: Task): Boolean {
        val workRequest = try {
            createWorkRequest(task)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create work request", e)
            return false
        }
        workManager.enqueue(workRequest)

        CoroutineScope(Dispatchers.Main).launch {
            workManager.getWorkInfoByIdFlow(workRequest.id).collect { workInfo ->
                Log.i(TAG, "WorkInfo: $workInfo")
            }
        }

        return true
    }

    override fun pause(taskId: String): Boolean {
        TaskManager.pause(taskId)
        return true
    }

    override fun resume(taskId: String): Boolean {
        TODO("Not yet implemented")
    }

    private fun handleWorkInfoState(workInfo: WorkInfo) {
        val taskId = workInfo.tags.firstOrNull { it.startsWith("taskId=") }?.substringAfter("=")

        if (taskId == null) {
            Log.e(TAG, "Task ID not found in tags")
            return
        }

        when (workInfo.state) {
            WorkInfo.State.ENQUEUED -> eventSink?.success(TaskStatus(taskId, TaskState.PENDING))
            WorkInfo.State.RUNNING -> {
                workInfo.progress.getString(TaskWorker.KEY_STATE)?.let { state ->
                    Log.d(TAG, "Task running: $state")
                }
            }
            WorkInfo.State.SUCCEEDED -> Log.d(TAG, "(${workInfo.id}) Task succeeded $taskId")
            WorkInfo.State.FAILED -> Log.d(TAG, "(${workInfo.id}) Task failed $taskId")
            WorkInfo.State.BLOCKED -> Log.d(TAG, "(${workInfo.id}) Task blocked $taskId")
            WorkInfo.State.CANCELLED -> Log.d(TAG, "(${workInfo.id}) Task cancelled $taskId")
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun createWorkRequest(task: Task): OneTimeWorkRequest {
        val data = Data.Builder().apply {
            putString(TaskWorker.KEY_TASK, Json.encodeToString(TaskSerializer, task))
        }.build()

        return when (task.type) {
            TaskType.DOWNLOAD -> OneTimeWorkRequestBuilder<DownloadWorker>()
            TaskType.UPLOAD -> OneTimeWorkRequestBuilder<UploadWorker>()
        }.apply {
            addTag(TAG)
            addTag("taskId=${task.id}")
            addTag("group=${task.group}")
            setInputData(data)
        }.build()
    }
}
