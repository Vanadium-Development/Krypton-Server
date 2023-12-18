package dev.vanadium.krypton.server.persistence.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("credential", schema = "krypton_server")
class CredentialEntity {

    @Id
    @Column("id")
    lateinit var id: UUID

    @Column("title")
    lateinit var title: String

    @Column("vault_id")
    lateinit var vaultId: UUID

}