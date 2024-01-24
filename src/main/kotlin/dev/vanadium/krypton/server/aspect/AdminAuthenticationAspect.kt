package dev.vanadium.krypton.server.aspect

import dev.vanadium.krypton.server.authorizedUser
import dev.vanadium.krypton.server.error.ForbiddenException
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.stereotype.Component

@Aspect
@Component
class AdminAuthenticationAspect {

    @Before("@annotation(RequireAdminAuthentication)")
    fun adminAuthenticationAnnotationPresent() {
        val user = authorizedUser()

        if(!user.admin) {
            throw ForbiddenException("Insufficient permissions")
        }
    }

}

annotation class RequireAdminAuthentication