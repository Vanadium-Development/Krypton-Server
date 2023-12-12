package dev.vanadium.krypton.server.rest.controller

import dev.vanadium.krypton.server.openapi.controllers.VaultApi
import dev.vanadium.krypton.server.openapi.model.CreateVaultRequest
import dev.vanadium.krypton.server.openapi.model.StatusResponse
import dev.vanadium.krypton.server.openapi.model.Vault
import dev.vanadium.krypton.server.service.VaultService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class VaultController(val vaultService: VaultService) : VaultApi {

    override fun createVault(createVaultRequest: CreateVaultRequest): ResponseEntity<StatusResponse> {
        vaultService.createVault(createVaultRequest)
        return ResponseEntity.ok(StatusResponse("Vault created."))
    }

    override fun getVault(vaultId: UUID): ResponseEntity<Vault> {

    }
}