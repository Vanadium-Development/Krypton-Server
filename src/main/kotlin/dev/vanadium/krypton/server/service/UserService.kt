package dev.vanadium.krypton.server.service

import dev.vanadium.krypton.server.error.ConflictException
import dev.vanadium.krypton.server.persistence.dao.UserDao
import dev.vanadium.krypton.server.persistence.model.UserEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(private val userDao: UserDao, private val passwordEncoder: PasswordEncoder) {


    @Transactional
    fun createUser(firstname: String, lastname: String, username: String, password: String): UserEntity {

        if(userDao.usernameExists(username))
            throw ConflictException("Username already exists.")

        var entity = UserEntity()
        entity.firstname = firstname
        entity.lastname = lastname
        entity.username = username
        entity.password = passwordEncoder.encode(password)

        entity = userDao.save(entity)

        return entity
    }

}