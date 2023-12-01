package dev.vanadium.krypton.server.rest.controller

import dev.vanadium.krypton.server.openapi.controllers.UserApi
import dev.vanadium.krypton.server.openapi.model.CreateUserRequest
import dev.vanadium.krypton.server.openapi.model.StatusResponse
import dev.vanadium.krypton.server.openapi.model.User
import dev.vanadium.krypton.server.persistence.model.UserEntity
import dev.vanadium.krypton.server.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@RestController
class UserController(private val userService: UserService) : UserApi {


    override fun createUser(createUserRequest: CreateUserRequest): ResponseEntity<StatusResponse> {
        userService.createUser(createUserRequest.firstname, createUserRequest.lastname, createUserRequest.username, createUserRequest.password)

        return ResponseEntity.ok(StatusResponse("User created"))
    }

    override fun getUsers(page: Int, name: String?): ResponseEntity<List<User>> {
        return ResponseEntity.ok(userService.filterUsersByUsernamePaginated(name, page)
            .map { it.toUserDto() })
    }
}