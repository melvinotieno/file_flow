package com.melvinotieno.file_flow

import android.content.Context
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.melvinotieno.file_flow.helpers.decode
import com.melvinotieno.file_flow.helpers.encode
import com.melvinotieno.file_flow.pigeons.FileFlowHostApi
import com.melvinotieno.file_flow.pigeons.Task
import com.melvinotieno.file_flow.pigeons.TaskResumeData
import com.melvinotieno.file_flow.pigeons.TaskType
import com.melvinotieno.file_flow.workers.DownloadWorker
import com.melvinotieno.file_flow.workers.ParallelDownloadWorker
import com.melvinotieno.file_flow.workers.TaskWorker
import com.melvinotieno.file_flow.workers.UploadWorker

class FlowManager(context: Context) : FileFlowHostApi {
    private val workManager by lazy { WorkManager.getInstance(context) }

    override fun enqueue(task: Task): Boolean {
        val workRequest = buildWorkRequest(task) ?: return false
        workManager.enqueue(workRequest)
        return true
    }

    override fun pauseWithId(taskId: String): Boolean {
        val workInfos = workManager.getWorkInfosByTag("taskId=$taskId").get()
        val runningWorkInfo = workInfos.firstOrNull { it.state == WorkInfo.State.RUNNING }

        return if (runningWorkInfo != null) {
            TaskWorker.pauseTask(taskId)
            true
        } else {
            false
        }
    }

    override fun resume(resumeData: TaskResumeData): Boolean {
        val task = Task.decode(resumeData.taskString) ?: return false
        val workRequest = buildWorkRequest(task, resumeData) ?: return false
        workManager.enqueue(workRequest)
        return true
    }

    override fun cancelWithId(taskId: String): Boolean {
        TODO("Not yet implemented")
    }

    private fun buildWorkRequest(task: Task, resumeData: TaskResumeData? = null): OneTimeWorkRequest? {
        val taskString = task.encode() ?: return null
        val resumeDataString = resumeData?.encode()

        val dataBuilder = Data.Builder().putString(TaskWorker.KEY_TASK, taskString)

        if (resumeDataString != null) {
            dataBuilder.putString(TaskWorker.KEY_DATA, resumeDataString)
        }

        val constraints = Constraints.Builder()

        return when (task.type) {
            TaskType.DOWNLOAD -> OneTimeWorkRequestBuilder<DownloadWorker>()
            TaskType.UPLOAD -> OneTimeWorkRequestBuilder<UploadWorker>()
            TaskType.MULTI_UPLOAD -> OneTimeWorkRequestBuilder<UploadWorker>()
            TaskType.PARALLEL_DOWNLOAD -> OneTimeWorkRequestBuilder<ParallelDownloadWorker>()
        }.apply {
            addTag(FileFlowPlugin.TAG)
            addTag("taskId=${task.id}")
            addTag("group=${task.group}")
            setInputData(dataBuilder.build())
            setConstraints(constraints.build())
        }.build()
    }
}
