package com.melvinotieno.file_flow.helpers

import com.melvinotieno.file_flow.pigeons.FlowTask
import com.melvinotieno.file_flow.pigeons.TaskException
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = FlowTask::class)
object TaskSerializer : KSerializer<FlowTask>

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = TaskException::class)
object TaskExceptionSerializer : KSerializer<TaskException>
