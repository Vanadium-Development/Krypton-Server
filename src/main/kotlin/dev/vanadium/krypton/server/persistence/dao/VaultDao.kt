package dev.vanadium.krypton.server.persistence.dao

import dev.vanadium.krypton.server.persistence.model.VaultEntity
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import java.util.*

interface VaultDao : CrudRepository<VaultEntity, UUID> {

    @Query("""select * from krypton_server.vault where id = :vaultId and user_id = :userId""")
    fun findByIdAndUserId(@Param("vaultId") id: UUID, @Param("userId") user: UUID): VaultEntity?

}