package dev.vanadium.krypton.server.rest.controller

import dev.vanadium.krypton.server.openapi.controllers.AuthApi
import dev.vanadium.krypton.server.openapi.model.Login200Response
import dev.vanadium.krypton.server.openapi.model.LoginRequest
import dev.vanadium.krypton.server.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(private val userService: UserService) : AuthApi {

    override fun login(loginRequest: LoginRequest): ResponseEntity<Login200Response> {
        val token = userService.login(loginRequest.username, loginRequest.password)

        return ResponseEntity.ok(Login200Response(token))
    }
}