package dev.vanadium.krypton.server.service

import dev.vanadium.krypton.server.error.NotFoundException
import dev.vanadium.krypton.server.error.UnauthorizedException
import dev.vanadium.krypton.server.openapi.model.FieldType
import dev.vanadium.krypton.server.openapi.model.FieldUpdate
import dev.vanadium.krypton.server.persistence.dao.FieldDao
import dev.vanadium.krypton.server.persistence.model.FieldEntity
import dev.vanadium.krypton.server.security.KryptonAuthentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.util.*

@Service
class FieldService(val fieldDao: FieldDao) {

    fun createField(
        type: FieldType, title: String, value: String, credentialId: UUID
    ): FieldEntity {
        val field = FieldEntity()
        field.type = type.toString()
        field.title = title
        field.value = value
        field.credId = credentialId

        fieldDao.save(field)

        return field
    }

    fun updateField(fieldUpdate: FieldUpdate) {
        val entity = fieldDao.findById(fieldUpdate.id)

        if (!entity.isPresent) throw NotFoundException("Could not find the requested field")

        val presentEntity = entity.get()

        val user = (SecurityContextHolder.getContext().authentication as KryptonAuthentication).user
        if (user.id != presentEntity.id && !user.admin)
            throw UnauthorizedException("Admin status is required to modify another user's credential field")

        presentEntity.title = fieldUpdate.title ?: presentEntity.title
        presentEntity.type = (fieldUpdate.fieldType ?: presentEntity.type).toString()
        presentEntity.value = fieldUpdate.value ?: presentEntity.value

        fieldDao.save(presentEntity)
    }

    fun removeField(fieldUUID: UUID) {
        val entity = fieldDao.findById(fieldUUID)

        if (!entity.isPresent)
            throw NotFoundException("Could not find the requested field")

        val presentEntity = entity.get()

        val user = (SecurityContextHolder.getContext().authentication as KryptonAuthentication).user
        if (user.id != presentEntity.id && !user.admin)
            throw UnauthorizedException("Admin status is required to delete another user's credential fields")

        fieldDao.delete(presentEntity)
    }

}