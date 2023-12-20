package dev.vanadium.krypton.server.service

import dev.vanadium.krypton.server.error.NotFoundException
import dev.vanadium.krypton.server.error.UnauthorizedException
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

    fun createVault(vault: Vault) {
        val vaultEntity = VaultEntity()
        vaultEntity.title = vault.title
        vaultEntity.description = vault.description
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
            }, cred.id))
        }

        return Optional.of(VaultResponse(vaultUUID, credentialDtos))
    }

    fun updateVault(vaultUpdate: VaultUpdate) {
        val entity = vaultDao.findById(vaultUpdate.id)

        if (!entity.isPresent) throw NotFoundException("Could not find the requested vault")

        val presentEntity = entity.get()
        val user = (SecurityContextHolder.getContext().authentication as KryptonAuthentication).user

        if (user.id != presentEntity.userId && !user.admin)
            throw UnauthorizedException("Admin status is required to modify another user's vault")

        presentEntity.title = vaultUpdate.title ?: presentEntity.title
        presentEntity.description = vaultUpdate.description ?: presentEntity.description

        vaultDao.save(presentEntity)
    }

}