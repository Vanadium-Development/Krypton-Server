package dev.vanadium.krypton.server.service

import dev.vanadium.krypton.server.error.NotFoundException
import dev.vanadium.krypton.server.openapi.model.CredentialUpdate
import dev.vanadium.krypton.server.persistence.dao.CredentialDao
import dev.vanadium.krypton.server.persistence.dao.VaultDao
import dev.vanadium.krypton.server.persistence.model.CredentialEntity
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
        presentEntity.title = credentialUpdate.title ?: presentEntity.title

        credentialDao.save(presentEntity)
    }

}