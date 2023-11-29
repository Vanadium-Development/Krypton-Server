package dev.vanadium.krypton.server.error

import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.lang.Exception

@Component
@ControllerAdvice
class ErrorHandler {


    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(Exception::class)
    fun handleException(exception: Exception, response: HttpServletResponse): ErrorResponse {
        return when(exception) {
            is KryptonApiException -> createErrorResponse(response.getHeader("X-Correlation-Id") ?: "No correlation id", exception, exception.statusCode, exception.shouldLog)
            else -> createErrorResponse(response.getHeader(response.getHeader("X-Correlation-Id") ?: "No correlation id"), exception, HttpStatus.INTERNAL_SERVER_ERROR, true)
        }
    }


    private fun createErrorResponse(correlationId: String, exception: Exception, statusCode: HttpStatus, shouldLog: Boolean): ErrorResponse {
        var message = exception.message ?: "No message provided"
        if(shouldLog) {
            logger.error("An unexpected error occurred [$correlationId]", exception)
            message = "Unknown Error"
        }

        return ErrorResponse(correlationId, exception::class.simpleName!!, statusCode.name, statusCode.value(), message)

    }

}


data class ErrorResponse(
    val correlationId: String,
    val exception: String,
    val status: String,
    val statusCode: Int,
    val message: String
)