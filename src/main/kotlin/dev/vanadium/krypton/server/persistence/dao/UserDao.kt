package dev.vanadium.krypton.server.persistence.dao

import dev.vanadium.krypton.server.persistence.model.UserEntity
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import java.util.UUID

interface UserDao : CrudRepository<UserEntity, UUID> {


    @Query("""select (count(*)) > 0 from krypton_server."user" where username = :username """)
    fun usernameExists(@Param("username") username: String): Boolean
}