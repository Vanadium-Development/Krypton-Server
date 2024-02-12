package dev.vanadium.krypton.server.persistence.dao

import dev.vanadium.krypton.server.persistence.model.CredentialEntity
import dev.vanadium.krypton.server.persistence.model.UserEntity
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import java.util.*

interface CredentialDao : CrudRepository<CredentialEntity, UUID> {

    @Query("""select * from krypton_server.credential where vault_id = :vaultUUID""")
    fun credentialsOf(@Param("vaultUUID") vaultUUID: UUID): List<CredentialEntity>
    @Query("""select u.* from krypton_server.credential as c inner join krypton_server.vault v on v.id = c.vault_id inner join krypton_server."user" u on u.id = v.user_id where c.id = :credUUID""")
    fun ownerOf(@Param("credUUID") credUUID: UUID): UserEntity?

}