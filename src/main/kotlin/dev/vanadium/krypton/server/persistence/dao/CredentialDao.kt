package dev.vanadium.krypton.server.persistence.dao

import dev.vanadium.krypton.server.persistence.model.CredentialEntity
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import java.util.*

interface CredentialDao : CrudRepository<CredentialEntity, UUID> {

    @Query("""select * from krypton_server.credential where vault_id = :vaultUUID""")
    fun credentialsOf(@Param("vaultUUID") vaultUUID: UUID): List<CredentialEntity>

}