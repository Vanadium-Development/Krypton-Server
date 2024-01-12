package dev.vanadium.krypton.server.rest.controller

import dev.vanadium.krypton.server.authorizedUser
import dev.vanadium.krypton.server.openapi.controllers.MeApi
import dev.vanadium.krypton.server.openapi.model.CredentialDump
import dev.vanadium.krypton.server.openapi.model.User
import dev.vanadium.krypton.server.persistence.model.UserEntity
import dev.vanadium.krypton.server.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class MeController(
    val userService: UserService
) : MeApi {

    override fun getOwnUser(): ResponseEntity<User> {
        val user: UserEntity = authorizedUser()

        return ResponseEntity.ok(user.toUserDto())
    }

    override fun dumpCredentials(): ResponseEntity<CredentialDump> {
        val aggregate = userService.aggregateOwnCredentials().get()

        return ResponseEntity.ok(aggregate)
    }

}