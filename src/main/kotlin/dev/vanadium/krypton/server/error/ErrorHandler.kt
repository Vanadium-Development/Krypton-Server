package dev.vanadium.krypton.server.error

import com.google.gson.Gson
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.ConstraintViolationException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.stereotype.Component
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.multipart.support.MissingServletRequestPartException
import org.springframework.web.servlet.NoHandlerFoundException

@Component
@ControllerAdvice
class ErrorHandler(
    private val gson: Gson
) {


    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(Exception::class)
    fun handleException(exception: Exception, response: HttpServletResponse) {
        val responseEntity = when (exception) {
            is KryptonApiException -> createErrorResponse(
                response,
                exception,
                exception.statusCode,
                exception.shouldLog
            )

            is ConstraintViolationException,
            is HttpMessageNotReadableException,
            is MissingServletRequestParameterException,
            is MethodArgumentTypeMismatchException,
            is MissingServletRequestPartException -> createErrorResponse(
                response,
                exception,
                HttpStatus.BAD_REQUEST,
                false
            )

            is NoHandlerFoundException,
            is HttpRequestMethodNotSupportedException -> createErrorResponse(
                response,
                exception,
                HttpStatus.NOT_FOUND,
                false
            )

            else -> createErrorResponse(response, exception, HttpStatus.INTERNAL_SERVER_ERROR, true)
        }

        response.status = responseEntity.statusCode
        response.addHeader("Content-Type", "application/json")
        response.writer.write(gson.toJson(responseEntity))

    }


    private fun createErrorResponse(
        response: HttpServletResponse,
        exception: Exception,
        statusCode: HttpStatus,
        shouldLog: Boolean
    ): ErrorResponse {
        var message = exception.message ?: "No message provided"
        val correlationId = response.getHeader("X-Correlation-Id") ?: "No correlation id"
        if (shouldLog) {
            logger.error("An unexpected error occurred [$correlationId]", exception)
            message = "Unknown Error"
        }



        return ErrorResponse(
            correlationId,
            exception::class.simpleName!!,
            statusCode.name,
            statusCode.value(),
            message
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