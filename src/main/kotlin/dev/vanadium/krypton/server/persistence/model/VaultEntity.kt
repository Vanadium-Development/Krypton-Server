package dev.vanadium.krypton.server.persistence.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

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

}