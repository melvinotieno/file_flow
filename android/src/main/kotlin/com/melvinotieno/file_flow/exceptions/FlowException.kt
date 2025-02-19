package com.melvinotieno.file_flow.exceptions

import com.melvinotieno.file_flow.pigeons.ErrorCode

/**
 * Custom exception class to handle task-related exceptions.
 *
 * @property code the [ErrorCode] associated with the exception.
 * @property message the detail message describing the exception.
 * @property httpResponseCode optional HTTP response code, if any.
 */
class FlowException(
    val code: ErrorCode,
    override val message: String,
    val httpResponseCode: Long? = null
) : Exception(message)
