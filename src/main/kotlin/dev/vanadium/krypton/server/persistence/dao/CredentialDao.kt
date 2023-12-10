package dev.vanadium.krypton.server.persistence.dao

import dev.vanadium.krypton.server.persistence.model.CredentialEntity
import org.springframework.data.repository.CrudRepository
import java.util.*

interface CredentialDao : CrudRepository<CredentialEntity, UUID> {
}