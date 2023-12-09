package dev.vanadium.krypton.server.persistence.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.net.InetAddress
import java.time.Duration
import java.time.Instant
import java.util.UUID

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

    @Column("expires_at")
    var expiresAt: Instant? = Instant.now() + Duration.ofDays(7)

    @Column("invalidate")
    var invalidate: Boolean = false

    @Column("user_id")
    lateinit var userId: UUID

    /**
     * Specifies whether the current session token was already successfully used.
     * If this is the case, it implies that the client was successfully able to decrypt the
     * session token and use it for authorization
     */
    @Column("authorized")
    var authorized: Boolean = false

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

    fun authorize() {
        this.authorized = true
    }

}