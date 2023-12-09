package dev.vanadium.krypton.server.service

import dev.vanadium.krypton.server.error.ConflictException
import dev.vanadium.krypton.server.error.UnauthorizedException
import dev.vanadium.krypton.server.persistence.dao.UserDao
import dev.vanadium.krypton.server.persistence.model.UserEntity
import jakarta.annotation.PostConstruct
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userDao: UserDao,
    private val passwordEncoder: PasswordEncoder,
    private val sessionService: SessionService,
    private val encryptionService: EncryptionService
) {

    // TODO Remove this for production builds
    @PostConstruct
    @Transactional
    fun createAdminUser() {
        if (userDao.getUserByUsername("admin") == null) {
            createUser("admin", "admin", "admin", "lfgowieiugfgonib")
            sessionService.createSession(userDao.getUserByUsername("admin")!!.id)
        }
    }

    /**
     * Creates a new user and saves it in the database.
     *
     * @param firstname the firstname of the user
     * @param lastname the lastname of the user
     * @param username the username of the user
     * @param auth the password of the user
     * @return the created UserEntity object
     * @throws ConflictException if the username already exists in the database
     */
    @Transactional
    fun createUser(firstname: String, lastname: String, username: String, auth: String): UserEntity {

        if (userDao.usernameExists(username))
            throw ConflictException("Username already exists.")

        var entity = UserEntity()
        entity.firstname = firstname
        entity.lastname = lastname
        entity.username = username
        entity.pubKey = passwordEncoder.encode(auth)

        entity = userDao.save(entity)

        return entity
    }

    /**
     * Authenticates a user by verifying their username and password.
     *
     * @param username The username of the user to authenticate.
     * @param password The password of the user to authenticate.
     *
     * @return The authentication token for the user's session.
     *
     * @throws UnauthorizedException if the username or password is incorrect, or if the user is deleted.
     */
    fun login(username: String): String {
        val unauthorizedException = UnauthorizedException("Username incorrect.")
        val user = userDao.getUserByUsername(username) ?: throw unauthorizedException

        if (user.deleted)
            throw unauthorizedException

        val session = sessionService.createSession(user.id)
        val encryptedToken = encryptionService.encryptToken(user.pubKey, session.token)

        return encryptedToken
    }

    /**
     * Filters users by username and returns a paginated list of user entities.
     *
     * @param username The username to filter by. Can be null to return all users.
     * @param page The page number of the paginated result.
     *
     * @return A paginated list of user entities filtered by the given username.
     */
    fun filterUsersByUsernamePaginated(username: String?, page: Int): List<UserEntity> {
        if (username != null) {
            return userDao.filterByUsernamePaginated(page, username).filter { !it.deleted }
        }

        return userDao.findAllPaginated(page).filter { !it.deleted }
    }
}