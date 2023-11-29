package dev.vanadium.krypton.server.persistence.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

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

    @Column("password")
    lateinit var password: String

    @Column("admin")
    var admin: Boolean = false

    @Column("deleted")
    var deleted: Boolean = false

    @Column("created_at")
    @CreatedDate
    var createdAt: Instant = Instant.now()

    @Column("deleted_at")
    var deletedAt: Instant? = null


}