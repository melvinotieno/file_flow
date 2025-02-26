package com.melvinotieno.file_flow.exceptions

import com.melvinotieno.file_flow.pigeons.TaskErrorCode

/**
 * Exception class for handling task-related errors.
 *
 * @property code the [TaskErrorCode] associated with the exception.
 * @property description the message describing the exception.
 * @property response optional HTTP response, if any.
 * @property cause the cause of the exception.
 */
class FlowException : Exception {
    val code: TaskErrorCode
    val description: String
    val response: Map<String, Any>?

    constructor(
        code: TaskErrorCode, description: String, response: Map<String, Any>?, cause: Throwable?
    ) : super(description, cause) {
        this.code = code
        this.description = description
        this.response = response
    }

    constructor(
        code: TaskErrorCode, description: String, response: Map<String, Any>?
    ) : this(code, description, response, null)

    constructor(
        code: TaskErrorCode, description: String, cause: Throwable?
    ) : this(code, description, null, cause)

    constructor(code: TaskErrorCode, description: String) : this(code, description, null, null)
}
