package com.melvinotieno.file_flow.helpers

import com.melvinotieno.file_flow.pigeons.TaskExceptionCode

/**
 * Custom exception class to handle task exceptions.
 */
class FlowTaskException(
    val code: TaskExceptionCode, override val message: String
) : Exception(message)
