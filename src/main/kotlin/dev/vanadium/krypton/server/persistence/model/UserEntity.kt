package dev.vanadium.krypton.server.persistence.model

import dev.vanadium.krypton.server.openapi.model.User
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table("user", schema = "krypton_server")
class UserEntity {

    @Id
    @Column("id")
    lateinit var id: UUID

    @Column("firstname")
    lateinit var firstname: String

    @Column("lastname")
    lateinit var lastname: String

    @Column("username")
    lateinit var username: String

    @Column("pub_key")
    lateinit var pubKey: String

    @Column("admin")
    var admin: Boolean = false

    @Column("deleted")
    var deleted: Boolean = false

    @Column("created_at")
    @CreatedDate
    var createdAt: Instant = Instant.now()

    @Column("deleted_at")
    var deletedAt: Instant? = null

    @Column("aes_key")
    lateinit var aesKey: String

    fun toUserDto(): User {
        return User(this.id, this.firstname, this.lastname, this.username, this.admin)
    }

}