package dev.vanadium.krypton.server.service

import dev.vanadium.krypton.server.error.NotFoundException
import dev.vanadium.krypton.server.error.UnauthorizedException
import dev.vanadium.krypton.server.openapi.model.CredentialUpdate
import dev.vanadium.krypton.server.persistence.dao.CredentialDao
import dev.vanadium.krypton.server.persistence.dao.VaultDao
import dev.vanadium.krypton.server.persistence.model.CredentialEntity
import dev.vanadium.krypton.server.security.KryptonAuthentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.util.*

@Service
class CredentialService(val credentialDao: CredentialDao, val vaultDao: VaultDao) {

    fun createCredential(title: String, vaultId: UUID): CredentialEntity? {
        if (vaultDao.findById(vaultId).isEmpty) return null

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

}