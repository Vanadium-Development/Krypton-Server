package dev.vanadium.krypton.server.persistence.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table("session", schema = "krypton_server")
class SessionEntity : Persistable<String> {

    @Transient
    @JsonIgnore
    var new: Boolean = false

    @Column("token")
    @Id
    lateinit var token: String

    @Column("created_at")
    var createdAt: Instant = Instant.now()

    @Column("invalidate")
    var invalidate: Boolean = false

    @Column("user_id")
    lateinit var userId: UUID

    @Column("accessed_at")
    var accessedAt: Instant = Instant.now()

    @Column("dirty")
    var dirty: Boolean = false;

    override fun getId(): String {
        return token
    }

    /**
     * Sets the value for the "new" property in the SessionEntity.
     *
     * @param b the boolean value to set for the "new" property
     * @return the updated SessionEntity object
     */
    infix fun new(b: Boolean): SessionEntity {
        this.new = b
        return this
    }

    override fun isNew(): Boolean {
        return this.new
    }

}