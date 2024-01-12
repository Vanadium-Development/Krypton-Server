package dev.vanadium.krypton.server.rest.controller

import dev.vanadium.krypton.server.openapi.controllers.CredentialApi
import dev.vanadium.krypton.server.openapi.model.*
import dev.vanadium.krypton.server.service.CredentialService
import dev.vanadium.krypton.server.service.FieldService
import dev.vanadium.krypton.server.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class CredentialController(val credentialService: CredentialService, val fieldService: FieldService, val userService: UserService) : CredentialApi {

    override fun createCredential(credential: Credential): ResponseEntity<Credential> {
        val cred = credentialService.createCredential(credential.title, credential.vault)

        credential.body.forEach { field ->
            fieldService.createField(field.fieldType, field.title, field.value, cred.id)
        }

        return ResponseEntity.ok(Credential(cred.title, cred.vaultId, emptyList(), cred.id))
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

    override fun deleteCredentialField(fieldUUID: UUID): ResponseEntity<StatusResponse> {
        fieldService.removeField(fieldUUID)

        return ResponseEntity.ok(StatusResponse("Field deleted."))
    }
}