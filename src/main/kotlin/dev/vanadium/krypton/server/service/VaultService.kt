package dev.vanadium.krypton.server.service

import dev.vanadium.krypton.server.openapi.model.*
import dev.vanadium.krypton.server.persistence.dao.CredentialDao
import dev.vanadium.krypton.server.persistence.dao.FieldDao
import dev.vanadium.krypton.server.persistence.dao.VaultDao
import dev.vanadium.krypton.server.persistence.model.UserEntity
import dev.vanadium.krypton.server.persistence.model.VaultEntity
import dev.vanadium.krypton.server.security.KryptonAuthentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.util.*

@Service
class VaultService(val vaultDao: VaultDao, val fieldDao: FieldDao, val credentialDao: CredentialDao) {

    fun createVault(createVaultRequest: CreateVaultRequest) {
        val vaultEntity = VaultEntity()
        vaultEntity.title = createVaultRequest.title
        vaultEntity.description = createVaultRequest.description
        vaultEntity.userId = (SecurityContextHolder.getContext().authentication.principal as UserEntity).id

        vaultDao.save(vaultEntity)
    }

    fun aggregateCredentials(vaultUUID: UUID): Optional<VaultResponse> {

        vaultDao.findByIdAndUserId(
            vaultUUID,
            (SecurityContextHolder.getContext().authentication as KryptonAuthentication).user.id
        ) ?: return Optional.empty()

        val credentials = credentialDao.credentialsOf(vaultUUID)

        val credentialDtos = arrayListOf<Credential>()

        credentials.forEach { cred ->
            val fieldDtos = arrayListOf<CredentialField>()

            val fields = fieldDao.fieldsOf(cred.id)

            fields.forEach { field ->
                fieldDtos.add(CredentialField(FieldType.valueOf(field.type), field.title, field.value))
            }

            credentialDtos.add(Credential(cred.title, cred.vaultId, fields.map { field ->
                CredentialField(FieldType.valueOf(field.type), field.title, field.value)
            }))
        }

        return Optional.of(VaultResponse(vaultUUID, credentialDtos))
    }

}