package dev.vanadium.krypton.server.persistence.dao

import dev.vanadium.krypton.server.persistence.model.VaultEntity
import org.springframework.data.repository.CrudRepository
import java.util.*

interface VaultDao : CrudRepository<VaultEntity, UUID> {
}