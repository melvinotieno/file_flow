package com.melvinotieno.file_flow.exceptions

import com.melvinotieno.file_flow.pigeons.ErrorCode

/**
 * Exception class for handling task-related errors.
 *
 * @property code the [ErrorCode] associated with the error.
 * @property description the message describing the exception.
 * @property httpResponseCode optional HTTP response code, if any.
 * @property cause the cause of the error, if caused by another exception.
 */
class FlowException : Exception {
    val code: ErrorCode
    val description: String
    val httpResponseCode: Long?

    constructor(
        code: ErrorCode, description: String, httpResponseCode: Long? = null, cause: Throwable?
    ) : super(description, cause) {
        this.code = code
        this.description = description
        this.httpResponseCode = httpResponseCode
    }

    constructor(
        code: ErrorCode, description: String, httpResponseCode: Long?
    ) : this(code, description, httpResponseCode, null)

    constructor(
        code: ErrorCode, description: String, cause: Throwable?
    ) : this(code, description, null, cause)

    constructor(code: ErrorCode, description: String) : this(code, description, null, null)
}
