package com.melvinotieno.file_flow.helpers

import com.melvinotieno.file_flow.pigeons.FlowTask
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = FlowTask::class)
object TaskSerializer : KSerializer<FlowTask>
