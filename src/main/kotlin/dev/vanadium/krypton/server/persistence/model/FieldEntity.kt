package dev.vanadium.krypton.server.persistence.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("cred_field", schema = "krypton_server")
class FieldEntity {

    @Id
    @Column("id")
    lateinit var id: UUID

    @Column("type")
    lateinit var type: String

    @Column("title")
    lateinit var title: String

    @Column("value")
    lateinit var value: String

    @Column("cred_id")
    lateinit var credId: UUID

}