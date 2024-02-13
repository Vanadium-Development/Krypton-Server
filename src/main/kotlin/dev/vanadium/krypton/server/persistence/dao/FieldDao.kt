package dev.vanadium.krypton.server.persistence.dao

import dev.vanadium.krypton.server.persistence.model.FieldEntity
import dev.vanadium.krypton.server.persistence.model.UserEntity
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import java.util.*

interface FieldDao : CrudRepository<FieldEntity, UUID> {

    @Query("""select * from krypton_server.cred_field where cred_id = :credUUID""")
    fun fieldsOf(@Param("credUUID") credential: UUID): List<FieldEntity>

    @Query("""delete from krypton_server.cred_field where cred_id = :credUUID""")
    fun deleteAllFieldsOf(@Param("credUUID") credential: UUID)


}