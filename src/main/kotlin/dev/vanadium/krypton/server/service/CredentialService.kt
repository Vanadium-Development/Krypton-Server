package dev.vanadium.krypton.server.service

import dev.vanadium.krypton.server.authorizedUser
import dev.vanadium.krypton.server.error.ForbiddenException
import dev.vanadium.krypton.server.error.InternalServerErrorException
import dev.vanadium.krypton.server.error.NotFoundException
import dev.vanadium.krypton.server.openapi.model.CredentialUpdate
import dev.vanadium.krypton.server.persistence.dao.CredentialDao
import dev.vanadium.krypton.server.persistence.dao.FieldDao
import dev.vanadium.krypton.server.persistence.dao.VaultDao
import dev.vanadium.krypton.server.persistence.model.CredentialEntity
import org.springframework.stereotype.Service
import java.util.*

@Service
class CredentialService(val credentialDao: CredentialDao, val vaultDao: VaultDao, val fieldDao: FieldDao, val fieldService: FieldService) {

    fun createCredential(title: String, vaultId: UUID): CredentialEntity {
        val vault = vaultDao.findById(vaultId)

        if (vault.isEmpty)
            throw NotFoundException("Could not find the requested vault")

        val authorizedUser = authorizedUser()
        if (vault.get().userId != authorizedUser.id && !authorizedUser.admin)
            throw ForbiddenException("Cannot create a credential for another user without having an admin status")

        val credential = CredentialEntity()
        credential.title = title
        credential.vaultId = vaultId

        credentialDao.save(credential)

        return credential
    }

    fun updateCredential(credentialUpdate: CredentialUpdate) {
        val entity = credentialDao.findById(credentialUpdate.id)

        if (!entity.isPresent) throw NotFoundException("Could not find the requested credential")

        val presentEntity = entity.get()

        val credentialUser = credentialDao.ownerOf(credentialUpdate.id) ?: throw InternalServerErrorException("Owner of valid credential not found.")

        val user = authorizedUser()
        if (user.id != credentialUser.id && !user.admin)
            throw ForbiddenException("Admin status is required to modify another user's credentials")

        val fields = fieldDao.fieldsOf(credentialUpdate.id)

        fields.forEach {
            fieldService.removeField(it.id)
        }

        credentialUpdate.body.forEach {
            fieldService.createField(it.fieldType, it.title, it.value, credentialUpdate.id)
        }

        credentialDao.save(presentEntity)
    }

    /**
     * Note: This will cascade-delete all fields bound to this credential
     */
    fun removeCredential(credentialUUID: UUID) {
        val entity = credentialDao.findById(credentialUUID)

        if (!entity.isPresent)
            throw NotFoundException("Could not find the requested credential")

        val presentEntity = entity.get()

        val user = authorizedUser()
        val owner = credentialDao.ownerOf(presentEntity.id)  ?: throw InternalServerErrorException("Owner of valid credential not found.")

        if (user.id != owner.id && !user.admin)
            throw ForbiddenException("Admin status is required to delete another user's credentials")

        // Delete cascade
        val fields = fieldDao.fieldsOf(credentialUUID)
        fieldDao.deleteAll(fields)

        credentialDao.delete(presentEntity)
    }

}