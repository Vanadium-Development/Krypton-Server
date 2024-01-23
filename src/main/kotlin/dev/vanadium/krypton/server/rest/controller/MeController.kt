package dev.vanadium.krypton.server.rest.controller

import dev.vanadium.krypton.server.authorizedUser
import dev.vanadium.krypton.server.openapi.controllers.MeApi
import dev.vanadium.krypton.server.openapi.model.User
import dev.vanadium.krypton.server.openapi.model.Vault
import dev.vanadium.krypton.server.persistence.model.UserEntity
import dev.vanadium.krypton.server.service.UserService
import dev.vanadium.krypton.server.service.VaultService
import jakarta.servlet.http.HttpServletResponse
import org.msgpack.jackson.dataformat.MessagePackFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class MeController(
    val vaultService: VaultService
) : MeApi {

    override fun getOwnUser(): ResponseEntity<User> {
        val user: UserEntity = authorizedUser()

        return ResponseEntity.ok(user.toUserDto())
    }

    override fun getAllVaults(): ResponseEntity<List<Vault>> {
        val user = authorizedUser()

        return ResponseEntity.ok(this.vaultService.getVaultsByUser(user.id).map { it.toDto() })
    }

}