package dev.vanadium.krypton.server.persistence.dao

import dev.vanadium.krypton.server.persistence.model.SessionEntity
import dev.vanadium.krypton.server.persistence.model.UserEntity
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import java.util.UUID

interface SessionDao : CrudRepository<SessionEntity, UUID> {


    @Query("""select u.* from krypton_server.session inner join krypton_server."user" u on u.id = session.user_id where session.invalidate = false and (u.deleted = false or :showDeleted) and token = :token and expires_at > now()""")
    fun getUserBySessionToken(@Param("token") token: String, @Param("showDeleted") showDeleted: Boolean): UserEntity?
}