package dev.vanadium.krypton.server.service

import dev.vanadium.krypton.server.authorizedUser
import dev.vanadium.krypton.server.error.ForbiddenException
import dev.vanadium.krypton.server.error.NotFoundException
import dev.vanadium.krypton.server.openapi.model.*
import dev.vanadium.krypton.server.persistence.dao.CredentialDao
import dev.vanadium.krypton.server.persistence.dao.FieldDao
import dev.vanadium.krypton.server.persistence.dao.VaultDao
import dev.vanadium.krypton.server.persistence.model.VaultEntity
import org.springframework.stereotype.Service
import java.util.*

@Service
class VaultService(val vaultDao: VaultDao, val fieldDao: FieldDao, val credentialDao: CredentialDao, val credentialService: CredentialService) {

    fun createVault(vault: Vault): VaultEntity {
        val vaultEntity = VaultEntity()
        vaultEntity.title = vault.title
        vaultEntity.description = vault.description
        vaultEntity.userId = authorizedUser().id

        vaultDao.save(vaultEntity)
        return vaultEntity
    }

    fun aggregateCredentials(vaultUUID: UUID): Optional<VaultResponse> {
        val vault = vaultDao.findById(vaultUUID)

        if (vault.isEmpty)
            throw NotFoundException("Could not find the requested vault")

        val authorizedUser = authorizedUser()
        if (vault.get().userId != authorizedUser.id && !authorizedUser.admin)
            throw ForbiddenException("Admin status is required to get other user's vaults")

        val credentials = credentialDao.credentialsOf(vaultUUID)

        val credentialDtos = arrayListOf<Credential>()

        credentials.forEach { cred ->
            val fieldDtos = arrayListOf<CredentialField>()

            val fields = fieldDao.fieldsOf(cred.id)

            fields.forEach { field ->
                fieldDtos.add(CredentialField(FieldType.valueOf(field.type), field.title, field.value))
            }

            credentialDtos.add(Credential(cred.title, cred.vaultId, fields.map { field ->
                CredentialField(FieldType.valueOf(field.type), field.title, field.value, field.id)
            }, cred.id))
        }

        return Optional.of(VaultResponse(vaultUUID, credentialDtos))
    }

    fun updateVault(vaultUpdate: VaultUpdate): VaultEntity {
        val entity = vaultDao.findById(vaultUpdate.id)

        if (!entity.isPresent) throw NotFoundException("Could not find the requested vault")

        val presentEntity = entity.get()
        val user = authorizedUser()

        if (user.id != presentEntity.userId && !user.admin)
            throw ForbiddenException("Admin status is required to modify another user's vault")

        presentEntity.title = vaultUpdate.title ?: presentEntity.title
        presentEntity.description = vaultUpdate.description ?: presentEntity.description

        vaultDao.save(presentEntity)

        return presentEntity
    }

    /**
     * Note: This will cascade-delete all credentials and hence also all fields in the vault
     */
    fun removeVault(vaultUUID: UUID) {
        val entity = vaultDao.findById(vaultUUID)

        if (!entity.isPresent)
            throw NotFoundException("Could not find the requested vault")

        val presentEntity = entity.get()

        val user = authorizedUser()
        if (user.id != presentEntity.id && !user.admin)
            throw ForbiddenException("Admin status is required to delete another user's vault")

        // Delete cascade
        val credentials = credentialDao.credentialsOf(vaultUUID)
        credentials.forEach { cred ->
            credentialService.removeCredential(cred.id)
        }

        vaultDao.delete(presentEntity)
    }

}