package dev.vanadium.krypton.server.service

import dev.vanadium.krypton.server.authorizedUser
import dev.vanadium.krypton.server.error.NotFoundException
import dev.vanadium.krypton.server.error.UnauthorizedException
import dev.vanadium.krypton.server.openapi.model.CredentialUpdate
import dev.vanadium.krypton.server.persistence.dao.CredentialDao
import dev.vanadium.krypton.server.persistence.dao.FieldDao
import dev.vanadium.krypton.server.persistence.dao.VaultDao
import dev.vanadium.krypton.server.persistence.model.CredentialEntity
import dev.vanadium.krypton.server.security.KryptonAuthentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.util.*

@Service
class CredentialService(val credentialDao: CredentialDao, val vaultDao: VaultDao, val fieldDao: FieldDao) {

    fun createCredential(title: String, vaultId: UUID): CredentialEntity {
        val vault = vaultDao.findById(vaultId)

        if (vault.isEmpty)
            throw NotFoundException("Could not find the requested vault")

        if (vault.get().userId != authorizedUser().id && !authorizedUser().admin)
            throw UnauthorizedException("Cannot create a credential for another user without having an admin status")

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

        val user = (SecurityContextHolder.getContext().authentication as KryptonAuthentication).user
        if (user.id != presentEntity.id && !user.admin)
            throw UnauthorizedException("Admin status is required to modify another user's credentials")

        presentEntity.title = credentialUpdate.title ?: presentEntity.title

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

        val user = (SecurityContextHolder.getContext().authentication as KryptonAuthentication).user
        if (user.id != presentEntity.id && !user.admin)
            throw UnauthorizedException("Admin status is required to delete another user's credentials")

        // Delete cascade
        val fields = fieldDao.fieldsOf(credentialUUID)
        fieldDao.deleteAll(fields)

        credentialDao.delete(presentEntity)
    }

}