package dev.vanadium.krypton.server.service

import dev.vanadium.krypton.server.persistence.dao.SessionDao
import dev.vanadium.krypton.server.persistence.model.SessionEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.random.Random

@Service
@EnableScheduling
class SessionService(
    private var sessionDao: SessionDao
) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Creates a new session for the given user.
     *
     * @param user The UUID of the user for whom the session is being created.
     * @return The created SessionEntity object.
     */
    @Transactional
    fun createSession(user: UUID): SessionEntity {

        val redundantSession = sessionDao.getRedundantSession(user)

        if(redundantSession != null) {
            return redundantSession
        }

        var entity = SessionEntity() new true
        entity.token = this.generateSessionToken()
        entity.userId = user

        entity = sessionDao.save(entity)

        return entity
    }

    /**
     * This runs every 20s to remove any sessions that are
     * - Explicitly flagged for removal
     * - Expired
     */
    @Scheduled(cron = "*/20 * * * * *")
    fun expireSessions() {
        val abandoned = sessionDao.getAbandonedSessions()
        val flagged = sessionDao.getSessionsFlaggedForRemoval()
        val count = abandoned.size + flagged.size
        if (count == 0)
            return
        sessionDao.deleteAll(abandoned)
        sessionDao.deleteAll(flagged)
        logger.info("Removed ${count} outdated and/or flagged sessions.")
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