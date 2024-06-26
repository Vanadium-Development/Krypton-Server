package dev.vanadium.krypton.server.persistence.dao

import dev.vanadium.krypton.server.persistence.model.SessionEntity
import dev.vanadium.krypton.server.persistence.model.UserEntity
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import java.util.*

interface SessionDao : CrudRepository<SessionEntity, UUID> {
    @Query("""select u.* from krypton_server.session inner join krypton_server."user" u on u.id = session.user_id where session.invalidate = false and (u.deleted = false or :showDeleted) and token = :token""")
    fun getUserBySessionToken(@Param("token") token: String, @Param("showDeleted") showDeleted: Boolean): UserEntity?

    @Query("""select * from krypton_server.session where token = :token and invalidate = false""")
    fun getSessionEntityByToken(@Param("token") token: String): SessionEntity?

    @Query("""select * from krypton_server.session where invalidate = true""")
    fun getSessionsFlaggedForRemoval(): List<SessionEntity>

    @Query("""select * from krypton_server.session where accessed_at < now() - interval '1 hour'""")
    fun getAbandonedSessions(): List<SessionEntity>

    @Query("""select * from krypton_server.session where user_id = :user and invalidate = false and krypton_server.session.dirty = false""")
    fun getRedundantSession(@Param("user") user: UUID): SessionEntity?

}