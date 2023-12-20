package dev.vanadium.krypton.server.rest.controller

import dev.vanadium.krypton.server.openapi.controllers.VaultApi
import dev.vanadium.krypton.server.openapi.model.Vault
import dev.vanadium.krypton.server.openapi.model.VaultResponse
import dev.vanadium.krypton.server.service.VaultService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class VaultController(val vaultService: VaultService) : VaultApi {

    override fun createVault(vault: Vault): ResponseEntity<Vault> {
        val entity = vaultService.createVault(vault)
        return ResponseEntity.ok(Vault(entity.title, entity.description, entity.id))
    }

    override fun getVault(vaultUUID: UUID): ResponseEntity<VaultResponse> {
        val vault = vaultService.aggregateCredentials(vaultUUID)

        return ResponseEntity.ok(vault.get())
    }

}