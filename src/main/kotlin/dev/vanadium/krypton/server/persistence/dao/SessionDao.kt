package dev.vanadium.krypton.server.persistence.dao

import dev.vanadium.krypton.server.persistence.model.SessionEntity
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface SessionDao : CrudRepository<SessionEntity, UUID> {
}