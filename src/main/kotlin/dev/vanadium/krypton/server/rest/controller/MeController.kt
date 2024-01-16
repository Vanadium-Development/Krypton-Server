package dev.vanadium.krypton.server.rest.controller

import dev.vanadium.krypton.server.authorizedUser
import dev.vanadium.krypton.server.openapi.controllers.MeApi
import dev.vanadium.krypton.server.openapi.model.User
import dev.vanadium.krypton.server.persistence.model.UserEntity
import dev.vanadium.krypton.server.service.UserService
import jakarta.servlet.http.HttpServletResponse
import org.msgpack.jackson.dataformat.MessagePackFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class MeController(
    val userService: UserService,
    val msgPackFactory: MessagePackFactory,
    val httpServletResponse: HttpServletResponse
) : MeApi {

    override fun getOwnUser(): ResponseEntity<User> {
        val user: UserEntity = authorizedUser()

        return ResponseEntity.ok(user.toUserDto())
    }

}