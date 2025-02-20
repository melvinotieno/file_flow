package com.melvinotieno.file_flow.exceptions

import com.melvinotieno.file_flow.helpers.ErrorCode

/**
 * Exception class for handling task-related errors.
 *
 * @property code the [ErrorCode] associated with the exception.
 * @property description the message describing the exception.
 * @property response optional HTTP response, if any.
 * @property cause the cause of the exception.
 */
class FlowException : Exception {
    val code: ErrorCode
    val description: String
    val response: Map<String, Any>?

    constructor(
        code: ErrorCode, description: String, response: Map<String, Any>?, cause: Throwable?
    ) : super(description, cause) {
        this.code = code
        this.description = description
        this.response = response
    }

    constructor(
        code: ErrorCode, description: String, response: Map<String, Any>?
    ) : this(code, description, response, null)

    constructor(
        code: ErrorCode, description: String, cause: Throwable?
    ) : this(code, description, null, cause)

    constructor(code: ErrorCode, description: String) : this(code, description, null, null)
}
