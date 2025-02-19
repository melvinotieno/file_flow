package com.melvinotieno.file_flow.exceptions

import com.melvinotieno.file_flow.pigeons.TaskErrorCode

/**
 * Exception class for handling task-related errors.
 *
 * @property code the [TaskErrorCode] associated with the error.
 * @property description the message describing the exception.
 * @property httpResponseCode optional HTTP response code, if any.
 * @property cause the cause of the error, if caused by another exception.
 */
class FlowException : Exception {
    val code: TaskErrorCode
    val description: String
    val httpResponseCode: Long?

    constructor(
        code: TaskErrorCode, description: String, httpResponseCode: Long? = null, cause: Throwable?
    ) : super(description, cause) {
        this.code = code
        this.description = description
        this.httpResponseCode = httpResponseCode
    }

    constructor(
        code: TaskErrorCode, description: String, httpResponseCode: Long?
    ) : this(code, description, httpResponseCode, null)

    constructor(
        code: TaskErrorCode, description: String, cause: Throwable?
    ) : this(code, description, null, cause)

    constructor(code: TaskErrorCode, description: String) : this(code, description, null, null)
}
