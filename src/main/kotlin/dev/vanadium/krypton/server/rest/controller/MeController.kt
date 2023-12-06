package dev.vanadium.krypton.server.rest.controller

import dev.vanadium.krypton.server.openapi.controllers.MeApi
import dev.vanadium.krypton.server.openapi.model.User
import dev.vanadium.krypton.server.persistence.model.UserEntity
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.RestController

@RestController
class MeController : MeApi {

    override fun getOwnUser(): ResponseEntity<User> {
        val user: UserEntity = SecurityContextHolder.getContext().authentication.principal as UserEntity

        return ResponseEntity.ok(user.toUserDto())
    }
}