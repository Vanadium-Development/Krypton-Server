package dev.vanadium.krypton.server.rest.controller

import dev.vanadium.krypton.server.authorizedSession
import dev.vanadium.krypton.server.authorizedUser
import dev.vanadium.krypton.server.openapi.controllers.MeApi
import dev.vanadium.krypton.server.openapi.model.GetAesKey200Response
import dev.vanadium.krypton.server.openapi.model.StatusResponse
import dev.vanadium.krypton.server.openapi.model.User
import dev.vanadium.krypton.server.openapi.model.Vault
import dev.vanadium.krypton.server.persistence.model.UserEntity
import dev.vanadium.krypton.server.service.SessionService
import dev.vanadium.krypton.server.service.UserService
import dev.vanadium.krypton.server.service.VaultService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class MeController(
    val vaultService: VaultService,
    val sessionService: SessionService,
    val userService: UserService
) : MeApi {

    override fun getOwnUser(): ResponseEntity<User> {
        val user: UserEntity = authorizedUser()

        return ResponseEntity.ok(user.toUserDto())
    }

    override fun getAllVaults(): ResponseEntity<List<Vault>> {
        val user = authorizedUser()

        return ResponseEntity.ok(this.vaultService.getVaultsByUser(user.id).map { it.toDto() })
    }

    override fun getAesKey(): ResponseEntity<GetAesKey200Response> {
        val user = authorizedUser()

        return ResponseEntity.ok(GetAesKey200Response(user.aesKey))
    }

    override fun updateAesKey(getAesKey200Response: GetAesKey200Response): ResponseEntity<StatusResponse> {

        val user = authorizedUser()

        this.userService.updateAesKey(user.id, getAesKey200Response.aesKey)

        return ResponseEntity.ok(StatusResponse("AES-Key updated"))
    }


    override fun logout(): ResponseEntity<StatusResponse> {
        val token = authorizedSession().token

        sessionService.invalidate(token)

        return ResponseEntity.ok(StatusResponse("Logged out"))
    }
}