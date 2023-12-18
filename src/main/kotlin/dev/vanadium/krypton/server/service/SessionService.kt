package dev.vanadium.krypton.server.service

import dev.vanadium.krypton.server.persistence.dao.SessionDao
import dev.vanadium.krypton.server.persistence.dao.UserDao
import dev.vanadium.krypton.server.persistence.model.SessionEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.random.Random

@Service
class SessionService(
    private var sessionDao: SessionDao,
    private var userDao: UserDao,
) {
    /**
     * Creates a new session for the given user.
     *
     * @param user The UUID of the user for whom the session is being created.
     * @return The created SessionEntity object.
     */
    @Transactional
    fun createSession(user: UUID): SessionEntity {
        var entity = SessionEntity() new true
        entity.token = this.generateSessionToken()
        entity.userId = user

        entity = sessionDao.save(entity)

        return entity
    }


    /**
     * Generates a session token.
     *
     * @return a randomly generated session token as a string.
     */
    private fun generateSessionToken(): String {
        val bytes = ByteArray(32)

        Random.Default.nextBytes(bytes)

        return Base64.getEncoder().encodeToString(bytes)
    }

}