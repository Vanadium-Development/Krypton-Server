package dev.vanadium.krypton.server.rest.controller

import dev.vanadium.krypton.server.error.NotFoundException
import dev.vanadium.krypton.server.openapi.controllers.CredentialApi
import dev.vanadium.krypton.server.openapi.model.Credential
import dev.vanadium.krypton.server.openapi.model.CredentialUpdate
import dev.vanadium.krypton.server.openapi.model.FieldUpdate
import dev.vanadium.krypton.server.openapi.model.StatusResponse
import dev.vanadium.krypton.server.service.CredentialService
import dev.vanadium.krypton.server.service.FieldService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class CredentialController(val credentialService: CredentialService, val fieldService: FieldService) : CredentialApi {

    override fun createCredential(credential: Credential): ResponseEntity<StatusResponse> {
        val cred = credentialService.createCredential(credential.title, credential.vault)

        credential.body.forEach { field ->
            fieldService.createField(field.fieldType, field.title, field.value, cred.id)
        }

        return ResponseEntity.ok(StatusResponse("OK"))
    }

    override fun updateCredential(credentialUpdate: CredentialUpdate): ResponseEntity<StatusResponse> {
        credentialService.updateCredential(credentialUpdate)

        return ResponseEntity.ok(StatusResponse("Credential updated"))
    }

    override fun updateCredentialField(fieldUpdate: FieldUpdate): ResponseEntity<StatusResponse> {
        fieldService.updateField(fieldUpdate)

        return ResponseEntity.ok(StatusResponse("Credential Field updated"))
    }

    override fun deleteCredential(credentialUUID: UUID): ResponseEntity<StatusResponse> {
        credentialService.removeCredential(credentialUUID)

        return ResponseEntity.ok(StatusResponse("Credential deleted"))
    }
}