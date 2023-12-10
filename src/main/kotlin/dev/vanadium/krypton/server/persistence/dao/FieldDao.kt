package dev.vanadium.krypton.server.persistence.dao

import dev.vanadium.krypton.server.persistence.model.FieldEntity
import org.springframework.data.repository.CrudRepository
import java.util.*

interface FieldDao : CrudRepository<FieldEntity, UUID> {
}