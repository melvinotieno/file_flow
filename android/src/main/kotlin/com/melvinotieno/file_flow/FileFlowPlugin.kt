package com.melvinotieno.file_flow

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.melvinotieno.file_flow.helpers.TaskSerializer
import com.melvinotieno.file_flow.pigeons.FileFlowHostApi
import com.melvinotieno.file_flow.pigeons.Task
import com.melvinotieno.file_flow.pigeons.TaskType
import com.melvinotieno.file_flow.workers.DownloadWorker
import com.melvinotieno.file_flow.workers.TaskWorker
import com.melvinotieno.file_flow.workers.UploadWorker
import io.flutter.embedding.engine.plugins.FlutterPlugin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancel
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

class FileFlowPlugin: FlutterPlugin, FileFlowHostApi {
    private lateinit var context: Context

    private var scope: CoroutineScope? = null

    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        context = binding.applicationContext
        scope = CoroutineScope(Dispatchers.Main)
        FileFlowHostApi.setUp(binding.binaryMessenger, this)
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        scope?.cancel()
        scope = null
        FileFlowHostApi.setUp(binding.binaryMessenger, null)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun enqueue(task: Task): Boolean {
        val dataBuilder = Data.Builder()
        dataBuilder.putString(TaskWorker.KEY_TASK, Json.encodeToString(TaskSerializer, task))

        val workRequestBuilder = when (task.type) {
            TaskType.DOWNLOAD -> OneTimeWorkRequestBuilder<DownloadWorker>()
            TaskType.UPLOAD -> OneTimeWorkRequestBuilder<UploadWorker>()
        }

        workRequestBuilder.setInputData(dataBuilder.build())

        val workRequest = workRequestBuilder.build()

        val workManager = WorkManager.getInstance(context)

        workManager.enqueue(workRequest)

        scope?.launch {
            workManager.getWorkInfoByIdFlow(workRequest.id).collect { workInfo ->
                if (workInfo != null) {
                    when (workInfo.state) {
                        WorkInfo.State.RUNNING -> {
                            Log.i("FileFlowPlugin", "Task ${task.id} is running")
                        }
                        WorkInfo.State.ENQUEUED -> {
                            Log.i("FileFlowPlugin", "Task ${task.id} is enqueued")
                        }
                        WorkInfo.State.SUCCEEDED -> {
                            Log.i("FileFlowPlugin", "Task ${task.id} is succeeded")
                        }
                        WorkInfo.State.FAILED -> {
                            Log.i("FileFlowPlugin", "Task ${task.id} is failed")
                        }
                        WorkInfo.State.BLOCKED -> {
                            Log.i("FileFlowPlugin", "Task ${task.id} is blocked")
                        }
                        WorkInfo.State.CANCELLED -> {
                            Log.i("FileFlowPlugin", "Task ${task.id} is cancelled")
                        }
                    }
                }
            }
        }

        return true
    }

    override fun pause(taskId: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun resume(taskId: String): Boolean {
        TODO("Not yet implemented")
    }
}
