package dev.vanadium.krypton.server.service

import dev.vanadium.krypton.server.authorizedUser
import dev.vanadium.krypton.server.error.NotFoundException
import dev.vanadium.krypton.server.error.UnauthorizedException
import dev.vanadium.krypton.server.openapi.model.CredentialUpdate
import dev.vanadium.krypton.server.persistence.dao.CredentialDao
import dev.vanadium.krypton.server.persistence.dao.VaultDao
import dev.vanadium.krypton.server.persistence.model.CredentialEntity
import org.springframework.stereotype.Service
import java.util.*

@Service
class CredentialService(val credentialDao: CredentialDao, val vaultDao: VaultDao) {

    fun createCredential(title: String, vaultId: UUID): CredentialEntity? {
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
        presentEntity.title = credentialUpdate.title ?: presentEntity.title

        credentialDao.save(presentEntity)
    }

}