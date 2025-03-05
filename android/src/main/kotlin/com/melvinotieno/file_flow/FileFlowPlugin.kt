package com.melvinotieno.file_flow

import android.content.Context
import android.util.Log
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.melvinotieno.file_flow.helpers.decode
import com.melvinotieno.file_flow.pigeons.FileFlowFlutterApi
import com.melvinotieno.file_flow.pigeons.FileFlowHostApi
import com.melvinotieno.file_flow.pigeons.PickerHostApi
import com.melvinotieno.file_flow.pigeons.TaskException
import com.melvinotieno.file_flow.pigeons.TaskProgress
import com.melvinotieno.file_flow.pigeons.TaskProgressData
import com.melvinotieno.file_flow.pigeons.TaskState
import com.melvinotieno.file_flow.pigeons.TaskStatus
import com.melvinotieno.file_flow.workers.TaskWorker
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding

class FileFlowPlugin : FlutterPlugin, ActivityAware {
    companion object {
        const val TAG = "FileFlow"
    }

    private lateinit var context: Context
    private lateinit var flutterApi: FileFlowFlutterApi
    private lateinit var pickerManager: PickerManager

    private val workManager by lazy { WorkManager.getInstance(context) }

    private val workInfoObserver = Observer<List<WorkInfo>> { workInfoList ->
        workInfoList.forEach { handleWorkInfoState(it) }
    }

    private var activityBinding: ActivityPluginBinding? = null

    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        context = binding.applicationContext
        flutterApi = FileFlowFlutterApi(binding.binaryMessenger)
        pickerManager = PickerManager()

        FileFlowHostApi.setUp(binding.binaryMessenger, FlowManager(context))
        PickerHostApi.setUp(binding.binaryMessenger, pickerManager)

        // Add observer to listen to work info changes
        workManager.getWorkInfosByTagLiveData(TAG).observeForever(workInfoObserver)
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        workManager.getWorkInfosByTagLiveData(TAG).removeObserver(workInfoObserver)
        FileFlowHostApi.setUp(binding.binaryMessenger, null)
        PickerHostApi.setUp(binding.binaryMessenger, null)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        pickerManager.setActivity(binding.activity)
        binding.addActivityResultListener(pickerManager)
        activityBinding = binding
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        onAttachedToActivity(binding)
    }

    override fun onDetachedFromActivity() {
        pickerManager.setActivity(null)
        activityBinding?.removeActivityResultListener(pickerManager)
        activityBinding = null
    }

    override fun onDetachedFromActivityForConfigChanges() {
        onDetachedFromActivity()
    }

    private fun handleWorkInfoState(workInfo: WorkInfo?) {
        workInfo ?: return

        val (taskId, taskGroup) = getTaskTags(workInfo.tags) ?: return

        when (workInfo.state) {
            WorkInfo.State.ENQUEUED -> {
                val taskStatus = TaskStatus(taskId, taskGroup, TaskState.PENDING)
                flutterApi.onStatusUpdate(taskStatus) { Result.success(it) }
            }
            WorkInfo.State.RUNNING -> {
                // We expect only a TaskState.RUNNING state
                workInfo.progress.getString(TaskWorker.KEY_STATE)?.let {
                    val taskStatus = TaskStatus(taskId, taskGroup, TaskState.valueOf(it))
                    flutterApi.onStatusUpdate(taskStatus) { Result.success(it) }
                }
                // Handle task progress updates
                workInfo.progress.getString(TaskWorker.KEY_DATA)?.let {
                    val data = TaskProgressData.decode(it)
                    val progress = workInfo.progress.getInt(TaskWorker.KEY_PROGRESS, 0)
                    val taskProgress = TaskProgress(taskId, taskGroup, progress.toLong(), data!!)
                    flutterApi.onProgressUpdate(taskProgress) { Result.success(it) }
                }
            }
            WorkInfo.State.SUCCEEDED -> {
                workInfo.outputData.getString(TaskWorker.KEY_STATE)?.let {
                    val taskStatus = TaskStatus(taskId, taskGroup, TaskState.valueOf(it))
                    flutterApi.onStatusUpdate(taskStatus) { Result.success(it) }
                }
            }
            WorkInfo.State.FAILED -> {
                val exception = workInfo.outputData.getString(TaskWorker.KEY_EXCEPTION)
                handleFailedState(taskId, taskGroup, exception)
            }
            WorkInfo.State.CANCELLED -> {
                Log.d(TAG, "Canceled task $taskId")
            }
            WorkInfo.State.BLOCKED -> {
                // Do nothing since we don't have chainable tasks
                Log.w(TAG, "Task $taskId is blocked but we don't expect this")
            }
        }
    }

    private fun getTaskTags(tags: Set<String>): Pair<String, String>? {
        val taskId = tags.firstOrNull { it.startsWith("taskId=") }?.substringAfter("=")
        val group = tags.firstOrNull { it.startsWith("group=") }?.substringAfter("=") ?: "default"
        return if (taskId != null) taskId to group else null
    }

    private fun handleFailedState(taskId: String, taskGroup: String, exceptionString: String?) {
        val exception = TaskException.decode(exceptionString)
        val taskStatus = TaskStatus(taskId, taskGroup, TaskState.FAILED, null, null, exception)
        flutterApi.onStatusUpdate(taskStatus) { Result.success(it) }
    }
}
