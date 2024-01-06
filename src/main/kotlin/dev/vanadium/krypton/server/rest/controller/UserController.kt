package dev.vanadium.krypton.server.rest.controller

import dev.vanadium.krypton.server.openapi.controllers.UserApi
import dev.vanadium.krypton.server.openapi.model.CreateUserRequest
import dev.vanadium.krypton.server.openapi.model.StatusResponse
import dev.vanadium.krypton.server.openapi.model.User
import dev.vanadium.krypton.server.openapi.model.UserUpdate
import dev.vanadium.krypton.server.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(private val userService: UserService) : UserApi {

    override fun createUser(createUserRequest: CreateUserRequest): ResponseEntity<StatusResponse> {
        userService.createUser(
            createUserRequest.firstname,
            createUserRequest.lastname,
            createUserRequest.username,
            createUserRequest.pubKey,
            false
        )

        return ResponseEntity.ok(StatusResponse("User created"))
    }

    override fun getUsers(page: Int, name: String?): ResponseEntity<List<User>> {
        return ResponseEntity.ok(userService.filterUsersByUsernamePaginated(name, page)
            .map { it.toUserDto() })
    }

    override fun updateUser(userUpdate: UserUpdate): ResponseEntity<StatusResponse> {
        userService.updateUser(userUpdate)

        return ResponseEntity.ok(StatusResponse("OK"))
    }
}