package dev.vanadium.krypton.server.persistence.model

import dev.vanadium.krypton.server.openapi.model.Vault
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table("vault", schema = "krypton_server")
class VaultEntity {

    @Id
    @Column("id")
    lateinit var id: UUID

    @Column("title")
    lateinit var title: String

    @Column("description")
    lateinit var description: String

    @Column("user_id")
    lateinit var userId: UUID


    fun toDto(): Vault {
        return Vault(this.title, this.description, this.id)
    }

}