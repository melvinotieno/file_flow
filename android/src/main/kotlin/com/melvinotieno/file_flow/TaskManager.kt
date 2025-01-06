package com.melvinotieno.file_flow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object TaskManager {
    private val commands = mutableMapOf<String, MutableLiveData<TaskCommand>>()

    fun observe(taskId: String): LiveData<TaskCommand> {
        return commands.getOrPut(taskId) { MutableLiveData() }
    }

    fun pause(taskId: String) {
        commands[taskId]?.postValue(TaskCommand.Pause)
    }

    fun remove(taskId: String) {
        commands.remove(taskId)
    }
}

sealed class TaskCommand {
    object Pause : TaskCommand()
}
