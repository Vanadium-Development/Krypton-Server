package dev.vanadium.krypton.server.rest.controller

import com.fasterxml.jackson.databind.ObjectMapper
import dev.vanadium.krypton.server.authorizedUser
import dev.vanadium.krypton.server.error.ForbiddenException
import dev.vanadium.krypton.server.error.NotFoundException
import dev.vanadium.krypton.server.openapi.controllers.CredentialApi
import dev.vanadium.krypton.server.openapi.model.*
import dev.vanadium.krypton.server.persistence.dao.CredentialDao
import dev.vanadium.krypton.server.persistence.dao.FieldDao
import dev.vanadium.krypton.server.service.CredentialService
import dev.vanadium.krypton.server.service.FieldService
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
class CredentialController(
    val credentialService: CredentialService,
    val fieldService: FieldService,
    val userService: UserService,
    val msgPackFactory: MessagePackFactory,
    val httpServletResponse: HttpServletResponse,
    val fieldDao: FieldDao,
    var credentialDao: CredentialDao
) : CredentialApi {

    override fun createCredential(
        vaultId: UUID,
        createCredentialRequest: CreateCredentialRequest
    ): ResponseEntity<Credential> {
        val cred = credentialService.createCredential(createCredentialRequest.title, vaultId)

        createCredentialRequest.body.forEach { field ->
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

    override fun dumpCredentialsJson(userUUID: UUID): ResponseEntity<CredentialDump> {
        val aggregate = userService.aggregateCredentialsOf(userUUID)

        return ResponseEntity.ok(aggregate)
    }

    override fun dumpCredentialsMessagePack(userUUID: UUID): ResponseEntity<Resource> {
        val aggregate = userService.aggregateCredentialsOf(userUUID)
        val mapper = ObjectMapper(msgPackFactory)
        val resource = ByteArrayResource(mapper.writeValueAsBytes(aggregate))

        httpServletResponse.setHeader(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"dump\""
        )

        return ResponseEntity.ok(resource)
    }

    override fun addCredentialField(
        uuid: UUID,
        fieldCreateRequest: FieldCreateRequest
    ): ResponseEntity<StatusResponse> {
        val user = credentialDao.ownerOf(uuid) ?: throw NotFoundException("Credential field not found.")

        val auth = authorizedUser()

        if (user.id != authorizedUser().id && !auth.admin)
            throw ForbiddenException("Operation required administrator access")

        fieldService.createField(fieldCreateRequest.fieldType, fieldCreateRequest.title, fieldCreateRequest.value, uuid)
        return ResponseEntity.ok(StatusResponse("OK"))
    }
}