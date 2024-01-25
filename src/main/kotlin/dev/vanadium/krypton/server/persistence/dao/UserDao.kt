package dev.vanadium.krypton.server.persistence.dao

import dev.vanadium.krypton.server.persistence.model.UserEntity
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface UserDao : CrudRepository<UserEntity, UUID> {
    @Query("""select (count(*)) > 0 from krypton_server."user" where username = :username """)
    fun usernameExists(@Param("username") username: String): Boolean

    @Query("""select * from krypton_server."user" where username = :username limit 10 offset (10 * :page) rows""")
    fun filterByUsernamePaginated(@Param("page") page: Int, @Param("username") username: String): List<UserEntity>

    @Query("""select  * from krypton_server."user" limit 10 offset (10 * :page) rows""")
    fun findAllPaginated(@Param("page") page: Int): List<UserEntity>

    @Query("""select * from krypton_server."user" where username = :username""")
    fun getUserByUsername(@Param("username") username: String): UserEntity?

    @Modifying
    @Transactional
    @Query("""update krypton_server."user" set aes_key = :aesKey where id = :user""")
    fun updateAesKey(@Param("aesKey") aesKey: String, @Param("user") user: UUID)
}