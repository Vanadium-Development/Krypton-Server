package dev.vanadium.krypton.server.service

import dev.vanadium.krypton.server.persistence.dao.CredentialDao
import dev.vanadium.krypton.server.persistence.dao.VaultDao
import dev.vanadium.krypton.server.persistence.model.CredentialEntity
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CredentialService(val credentialDao: CredentialDao, val vaultDao: VaultDao) {

    fun createCredential(title: String, vault_id: UUID): CredentialEntity? {
        if (vaultDao.findById(vault_id).isEmpty)
            return null

        val credential = CredentialEntity()
        credential.title = title
        credential.vault_id = vault_id

        credentialDao.save(credential)

        return credential
    }

}