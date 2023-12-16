package dev.vanadium.krypton.server.service

import dev.vanadium.krypton.server.error.NotFoundException
import dev.vanadium.krypton.server.openapi.model.CreateVaultRequest
import dev.vanadium.krypton.server.openapi.model.Vault
import dev.vanadium.krypton.server.persistence.dao.FieldDao
import dev.vanadium.krypton.server.persistence.dao.VaultDao
import dev.vanadium.krypton.server.persistence.model.UserEntity
import dev.vanadium.krypton.server.persistence.model.VaultEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class VaultService(val vaultDao: VaultDao, val fieldDao: FieldDao) {

    fun createVault(createVaultRequest: CreateVaultRequest) {
        val vaultEntity = VaultEntity()
        vaultEntity.title = createVaultRequest.title
        vaultEntity.description = createVaultRequest.description
        vaultEntity.user_id = (SecurityContextHolder.getContext().authentication.principal as UserEntity).id

        vaultDao.save(vaultEntity)
    }

//    fun getVault(vaultId: UUID): Vault {
//        val vault = vaultDao.findById(vaultId)
//
//        if (!vault.isPresent)
//            throw NotFoundException("Could not find vault")
//
//
//
//    }

}