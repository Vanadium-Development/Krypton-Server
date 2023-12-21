package dev.vanadium.krypton.server

import dev.vanadium.krypton.server.persistence.model.UserEntity
import dev.vanadium.krypton.server.security.KryptonAuthentication
import org.springframework.security.core.context.SecurityContextHolder

fun authorizedUser(): UserEntity {
    return (SecurityContextHolder.getContext().authentication as KryptonAuthentication).user
}
