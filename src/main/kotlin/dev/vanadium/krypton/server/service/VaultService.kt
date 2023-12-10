package dev.vanadium.krypton.server.service

import dev.vanadium.krypton.server.openapi.model.CreateVaultRequest
import dev.vanadium.krypton.server.persistence.dao.VaultDao
import dev.vanadium.krypton.server.persistence.model.UserEntity
import dev.vanadium.krypton.server.persistence.model.VaultEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class VaultService(val vaultDao: VaultDao) {

    fun createVault(createVaultRequest: CreateVaultRequest) {
        val vaultEntity = VaultEntity()
        vaultEntity.title = createVaultRequest.title
        vaultEntity.description = createVaultRequest.description
        vaultEntity.user_id = (SecurityContextHolder.getContext().authentication.principal as UserEntity).id

        vaultDao.save(vaultEntity)
    }

}