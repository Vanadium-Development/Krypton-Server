package dev.vanadium.krypton.server.rest.controller

import dev.vanadium.krypton.server.error.NotFoundException
import dev.vanadium.krypton.server.openapi.controllers.CredentialsApi
import dev.vanadium.krypton.server.openapi.model.CreateCredentialRequest
import dev.vanadium.krypton.server.openapi.model.StatusResponse
import dev.vanadium.krypton.server.service.CredentialService
import dev.vanadium.krypton.server.service.FieldService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class CredentialController(val credentialService: CredentialService, val fieldService: FieldService) : CredentialsApi {

    override fun createCredential(createCredentialRequest: CreateCredentialRequest): ResponseEntity<StatusResponse> {
        val cred = credentialService.createCredential(createCredentialRequest.title, createCredentialRequest.vault)
        cred ?: throw NotFoundException("No vault was found with the given ID.")

        createCredentialRequest.body.forEach { field ->
            fieldService.createField(field.fieldType, field.title, field.value, cred.id)
        }

        return ResponseEntity.ok(StatusResponse("Credential created."))
    }

}