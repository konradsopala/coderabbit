package com.calendar

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import java.time.Instant

// Review fix: @RestControllerAdvice replaces @ControllerAdvice + @ResponseBody
@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(EventNotFoundException::class)
    fun handleNotFound(ex: EventNotFoundException): ResponseEntity<Map<String, Any>> =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            mapOf(
                "error" to "Not Found",
                "message" to (ex.message ?: "Resource not found"),
                "timestamp" to Instant.now().toString()
            )
        )

    // Review fix: handle only application-specific bad request exception
    @ExceptionHandler(ApplicationBadRequestException::class)
    fun handleBadRequest(ex: ApplicationBadRequestException): ResponseEntity<Map<String, Any>> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            mapOf(
                "error" to "Bad Request",
                "message" to (ex.message ?: "Invalid request"),
                "timestamp" to Instant.now().toString()
            )
        )

    // Review fix: handle framework exceptions with a generic message to avoid leaking internals
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(ex: MethodArgumentTypeMismatchException): ResponseEntity<Map<String, Any>> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            mapOf(
                "error" to "Bad Request",
                "message" to "Invalid parameter value for '${ex.name}'",
                "timestamp" to Instant.now().toString()
            )
        )

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException): ResponseEntity<Map<String, Any>> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            mapOf(
                "error" to "Bad Request",
                "message" to "Invalid request parameters",
                "timestamp" to Instant.now().toString()
            )
        )
}
