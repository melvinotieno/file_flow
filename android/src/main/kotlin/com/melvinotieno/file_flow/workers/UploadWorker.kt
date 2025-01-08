package com.melvinotieno.file_flow.workers

import android.content.Context
import androidx.work.WorkerParameters
import com.melvinotieno.file_flow.pigeons.TaskState
import java.io.File
import java.net.HttpURLConnection

class UploadWorker(context: Context, params: WorkerParameters): TaskWorker(context, params) {
    override suspend fun processRequest(connection: HttpURLConnection): TaskState {
        val file = File(task.directory!!)
        if (!file.exists() || !file.isFile) {
            return TaskState.FAILED
        }

        return TaskState.COMPLETED
    }
}
