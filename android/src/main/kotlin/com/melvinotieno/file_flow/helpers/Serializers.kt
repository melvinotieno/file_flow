package com.melvinotieno.file_flow.helpers

import com.melvinotieno.file_flow.pigeons.Task
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer

@ExperimentalSerializationApi
@Serializer(forClass = Task::class)
object TaskSerializer : KSerializer<Task>
