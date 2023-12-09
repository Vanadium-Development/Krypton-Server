package dev.vanadium.krypton.server.error

import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.ConstraintViolationException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.stereotype.Component
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.multipart.support.MissingServletRequestPartException
import org.springframework.web.servlet.NoHandlerFoundException

@Component
@ControllerAdvice
class ErrorHandler {


    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(Exception::class)
    fun handleException(exception: Exception, response: HttpServletResponse): ResponseEntity<ErrorResponse> {
        return when (exception) {
            is KryptonApiException -> createErrorResponse(
                response,
                exception,
                exception.statusCode,
                exception.shouldLog
            )

            is ConstraintViolationException,
            is HttpMessageNotReadableException,
            is MissingServletRequestParameterException,
            is MissingServletRequestPartException -> createErrorResponse(
                response,
                exception,
                HttpStatus.BAD_REQUEST,
                false
            )

            is NoHandlerFoundException -> createErrorResponse(response, exception, HttpStatus.NOT_FOUND, false)
            else -> createErrorResponse(response, exception, HttpStatus.INTERNAL_SERVER_ERROR, true)
        }
    }


    private fun createErrorResponse(
        response: HttpServletResponse,
        exception: Exception,
        statusCode: HttpStatus,
        shouldLog: Boolean
    ): ResponseEntity<ErrorResponse> {
        var message = exception.message ?: "No message provided"
        val correlationId = response.getHeader("X-Correlation-Id") ?: "No correlation id"
        if (shouldLog) {
            logger.error("An unexpected error occurred [$correlationId]", exception)
            message = "Unknown Error"
        }

        return ResponseEntity(
            ErrorResponse(
                correlationId,
                exception::class.simpleName!!,
                statusCode.name,
                statusCode.value(),
                message
            ), statusCode
        )

    }

}


data class ErrorResponse(
    val correlationId: String,
    val exception: String,
    val status: String,
    val statusCode: Int,
    val message: String
)