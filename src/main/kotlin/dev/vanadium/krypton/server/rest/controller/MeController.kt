package dev.vanadium.krypton.server.rest.controller

import com.fasterxml.jackson.databind.ObjectMapper
import dev.vanadium.krypton.server.authorizedUser
import dev.vanadium.krypton.server.openapi.controllers.MeApi
import dev.vanadium.krypton.server.openapi.model.CredentialDump
import dev.vanadium.krypton.server.openapi.model.User
import dev.vanadium.krypton.server.persistence.model.UserEntity
import dev.vanadium.krypton.server.service.UserService
import jakarta.servlet.http.HttpServletResponse
import org.msgpack.jackson.dataformat.MessagePackFactory
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.*

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

    override fun dumpCredentialsJson(): ResponseEntity<CredentialDump> {
        val aggregate = userService.aggregateOwnCredentials()

        return ResponseEntity.ok(aggregate)
    }

    override fun dumpCredentialsMessagePack(): ResponseEntity<Resource> {
        val aggregate = userService.aggregateOwnCredentials()
        val mapper = ObjectMapper(msgPackFactory)
        val resource = ByteArrayResource(Base64.getEncoder().encode(mapper.writeValueAsBytes(aggregate)))

        httpServletResponse.setHeader(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"dump\""
        )

        return ResponseEntity.ok(resource)
    }

}