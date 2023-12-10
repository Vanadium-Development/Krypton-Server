package dev.vanadium.krypton.server.service

import dev.vanadium.krypton.server.openapi.model.Credential
import dev.vanadium.krypton.server.persistence.dao.FieldDao
import dev.vanadium.krypton.server.persistence.model.FieldEntity
import org.springframework.stereotype.Service
import java.util.*

@Service
class FieldService(val fieldDao: FieldDao) {

    fun createField(
        type: Credential.FieldType,
        title: String,
        value: String,
        credentialId: UUID
    ): FieldEntity {
        val field = FieldEntity()
        field.type = type.toString()
        field.title = title
        field.value = value
        field.credId = credentialId

        fieldDao.save(field)

        return field
    }

}