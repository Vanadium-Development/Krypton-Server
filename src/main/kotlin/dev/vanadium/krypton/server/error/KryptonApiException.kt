package dev.vanadium.krypton.server.error

import org.springframework.http.HttpStatus

open class KryptonApiException(val statusCode: HttpStatus, message: String?, val shouldLog: Boolean = true) :
    RuntimeException(message)


class NotFoundException(message: String) : KryptonApiException(HttpStatus.NOT_FOUND, message, false)
class ConflictException(message: String) : KryptonApiException(HttpStatus.CONFLICT, message, false)
class UnauthorizedException(message: String) : KryptonApiException(HttpStatus.UNAUTHORIZED, message, false)