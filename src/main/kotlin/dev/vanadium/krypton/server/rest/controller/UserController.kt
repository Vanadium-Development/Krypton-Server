package dev.vanadium.krypton.server.rest.controller

import dev.vanadium.krypton.server.openapi.controllers.UserApi
import dev.vanadium.krypton.server.openapi.model.CreateUserRequest
import dev.vanadium.krypton.server.openapi.model.StatusResponse
import dev.vanadium.krypton.server.openapi.model.User
import dev.vanadium.krypton.server.persistence.model.UserAuthMethod
import dev.vanadium.krypton.server.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(private val userService: UserService) : UserApi {

    override fun createUser(createUserRequest: CreateUserRequest): ResponseEntity<StatusResponse> {
        val method: UserAuthMethod
        try {
            method = UserAuthMethod.valueOf(createUserRequest.authMethod)
        } catch (e: Exception) {
            return ResponseEntity.ok(StatusResponse("Invalid authentication method \"${createUserRequest.authMethod}\""))
        }

        userService.createUser(
            createUserRequest.firstname,
            createUserRequest.lastname,
            createUserRequest.username,
            method,
            createUserRequest.auth
        )

        return ResponseEntity.ok(StatusResponse("User created"))
    }

    override fun getUsers(page: Int, name: String?): ResponseEntity<List<User>> {
        return ResponseEntity.ok(userService.filterUsersByUsernamePaginated(name, page)
            .map { it.toUserDto() })
    }
}