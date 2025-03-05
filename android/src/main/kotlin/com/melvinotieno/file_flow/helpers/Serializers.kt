package com.melvinotieno.file_flow.helpers

import android.util.Log
import com.melvinotieno.file_flow.FileFlowPlugin
import com.melvinotieno.file_flow.pigeons.Task
import com.melvinotieno.file_flow.pigeons.TaskCompleteData
import com.melvinotieno.file_flow.pigeons.TaskException
import com.melvinotieno.file_flow.pigeons.TaskProgressData
import com.melvinotieno.file_flow.pigeons.TaskResumeData
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = Task::class)
object TaskSerializer : KSerializer<Task>

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = TaskCompleteData::class)
object CompleteDataSerializer : KSerializer<TaskCompleteData>

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = TaskProgressData::class)
object ProgressDataSerializer : KSerializer<TaskProgressData>

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = TaskResumeData::class)
object ResumeDataSerializer : KSerializer<TaskResumeData>

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = TaskException::class)
object ExceptionSerializer : KSerializer<TaskException>

fun Task.encode(): String? = try {
    Json.encodeToString<Task>(TaskSerializer, this)
} catch (e: Exception) {
    Log.e(FileFlowPlugin.TAG, "Failed to encode Task: $this", e)
    null
}

fun Task.Companion.decode(data: String?): Task? = data?.let {
    try {
        Json.decodeFromString<Task>(TaskSerializer, data)
    } catch (e: Exception) {
        Log.e(FileFlowPlugin.TAG, "Failed to decode Task: $data", e)
        null
    }
}

fun TaskCompleteData.encode(): String? = try {
    Json.encodeToString<TaskCompleteData>(CompleteDataSerializer, this)
} catch (e: Exception) {
    Log.e(FileFlowPlugin.TAG, "Failed to encode TaskCompleteData: $this", e)
    null
}

fun TaskCompleteData.Companion.decode(data: String?): TaskCompleteData? = data?.let {
    try {
        Json.decodeFromString<TaskCompleteData>(CompleteDataSerializer, data)
    } catch (e: Exception) {
        Log.e(FileFlowPlugin.TAG, "Failed to decode TaskCompleteData: $data", e)
        null
    }
}

fun TaskProgressData.encode(): String? = try {
    Json.encodeToString<TaskProgressData>(ProgressDataSerializer, this)
} catch (e: Exception) {
    Log.e(FileFlowPlugin.TAG, "Failed to encode TaskProgressData: $this", e)
    null
}

fun TaskProgressData.Companion.decode(data: String?): TaskProgressData? = data?.let {
    try {
        Json.decodeFromString<TaskProgressData>(ProgressDataSerializer, data)
    } catch (e: Exception) {
        Log.e(FileFlowPlugin.TAG, "Failed to decode TaskProgressData: $data", e)
        null
    }
}

fun TaskResumeData.encode(): String? = try {
    Json.encodeToString<TaskResumeData>(ResumeDataSerializer, this)
} catch (e: Exception) {
    Log.e(FileFlowPlugin.TAG, "Failed to encode TaskResumeData: $this", e)
    null
}

fun TaskResumeData.Companion.decode(data: String?): TaskResumeData? = data?.let {
    try {
        Json.decodeFromString<TaskResumeData>(ResumeDataSerializer, data)
    } catch (e: Exception) {
        Log.e(FileFlowPlugin.TAG, "Failed to decode TaskResumeData: $data", e)
        null
    }
}

fun TaskException.encode(): String? = try {
    Json.encodeToString<TaskException>(ExceptionSerializer, this)
} catch (e: Exception) {
    Log.e(FileFlowPlugin.TAG, "Failed to encode TaskException: $this", e)
    null
}

fun TaskException.Companion.decode(data: String?): TaskException? = data?.let {
    try {
        Json.decodeFromString<TaskException>(ExceptionSerializer, data)
    } catch (e: Exception) {
        Log.e(FileFlowPlugin.TAG, "Failed to decode TaskException: $data", e)
        null
    }
}
