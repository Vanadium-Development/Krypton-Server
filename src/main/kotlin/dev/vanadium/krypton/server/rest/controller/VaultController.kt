package dev.vanadium.krypton.server.rest.controller

import dev.vanadium.krypton.server.openapi.controllers.VaultApi
import dev.vanadium.krypton.server.openapi.model.*
import dev.vanadium.krypton.server.service.VaultService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class VaultController(val vaultService: VaultService) : VaultApi {

    override fun createVault(createVaultRequest: CreateVaultRequest): ResponseEntity<Vault> {
        val entity = vaultService.createVault(createVaultRequest)
        return ResponseEntity.ok(Vault(entity.title, entity.description, entity.id))
    }

    override fun getVault(vaultUUID: UUID): ResponseEntity<VaultResponse> {
        val vault = vaultService.aggregateCredentials(vaultUUID)

        return ResponseEntity.ok(vault.get())
    }

    override fun updateVault(vaultUpdate: VaultUpdate): ResponseEntity<Vault> {
        val vault = vaultService.updateVault(vaultUpdate)
        return ResponseEntity.ok(Vault(vault.title, vault.description, vault.id))
    }

    override fun deleteVault(vaultUUID: UUID): ResponseEntity<StatusResponse> {
        vaultService.removeVault(vaultUUID)

        return ResponseEntity.ok(StatusResponse("Vault deleted"))
    }
}