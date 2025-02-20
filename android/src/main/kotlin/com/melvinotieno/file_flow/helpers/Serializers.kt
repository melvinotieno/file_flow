package com.melvinotieno.file_flow.helpers

import com.melvinotieno.file_flow.pigeons.FlowTask
import com.melvinotieno.file_flow.pigeons.TaskException
import com.melvinotieno.file_flow.pigeons.TaskProgressData
import com.melvinotieno.file_flow.pigeons.TaskStateData
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = FlowTask::class)
object TaskSerializer : KSerializer<FlowTask>

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = TaskStateData::class)
object StateDataSerializer : KSerializer<TaskStateData>

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = TaskProgressData::class)
object ProgressDataSerializer : KSerializer<TaskProgressData>

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = TaskException::class)
object ExceptionSerializer : KSerializer<TaskException>
