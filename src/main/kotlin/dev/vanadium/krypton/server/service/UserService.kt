package dev.vanadium.krypton.server.service

import dev.vanadium.krypton.server.authorizedUser
import dev.vanadium.krypton.server.error.ConflictException
import dev.vanadium.krypton.server.error.ForbiddenException
import dev.vanadium.krypton.server.error.NotFoundException
import dev.vanadium.krypton.server.error.UnauthorizedException
import dev.vanadium.krypton.server.openapi.model.UserUpdate
import dev.vanadium.krypton.server.persistence.dao.UserDao
import dev.vanadium.krypton.server.persistence.model.UserEntity
import dev.vanadium.krypton.server.security.KryptonAuthentication
import jakarta.annotation.PostConstruct
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userDao: UserDao,
    private val sessionService: SessionService,
    private val encryptionService: EncryptionService
) {

    // TODO Remove this for production builds
    /**
     * Private key for testing:
     *
     * -----BEGIN RSA PRIVATE KEY-----
     * MIICXQIBAAKBgQCKYMlX2WV6hGJpeQMNMpTM66zrDUpo/2J75tzjuqqSZDfBWwM5
     * adk6M/e4UYEC2Y/uWDKldqu+OrePmOPNZEV3oExtai/yIC0dfvYWDN2zkYoPv0++
     * jsYgJenZKLFDYpKM8QQ5MDZf6+MQQV1to3V5+MbE7fsRh5WIzTqsEXtB1wIDAQAB
     * AoGBAIFqO2O5obqPjSpvTndNUvTDhRjfeTPxhL20D+m7bkMzDyH6aG2NnOdeKtNr
     * BmkP6BhUzCkLb1udtobJymMQ4BW3ueXDlwKMGz63nKoD3IBKXBV787CBarD2NBda
     * BzQkdF3PFkD8PVnwCtjP0fG2ufXdsNPZb9jGdhFe2NYN7GmBAkEA36ymWX0rlSkq
     * ZDh8zh4fXQNxGqvRvtZh28ANqekFA0b3jcdeQhnG+vBZjM7+xgJsHToBpy9Mi40k
     * AJbKVpwuMQJBAJ5gZu1tRBDUjpm3OfroZK3EL3EYOE5gtJzXY9Hr/MQAnDTTXhGn
     * fVXnmZuATy+ZQqclTqLaMeuuelSs4g11xocCQB1Z8ppbqpRwSnfMUdRab5MtGHJ/
     * iY6ZY04K7cAWK+o6LdIVD3FtIIddcuLfZt9lAfrz2bOuqUTGyKqrHvIunIECQGWQ
     * lPE13Syd40UYh4osdkQpR/NTAOjig3EBf/YjTFm1unb2BaF0s5/fglaClkWEF4Zx
     * Gli9bL4jije7Fsxi9wkCQQCiZTOm662Isc1Y2nppJN0XCTlw4UUWawrlD1HdYfss
     * JRfqKHV9il9ZHg7r4HPiEABxEp37znUgVs6/wyaeWt96
     * -----END RSA PRIVATE KEY-----
     */
    @PostConstruct
    @Transactional
    fun createAdminUser() {
        if (userDao.getUserByUsername("admin") == null) {
            createUser(
                "admin", "admin", "admin", "-----BEGIN PUBLIC KEY-----\n" +
                        "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCKYMlX2WV6hGJpeQMNMpTM66zr\n" +
                        "DUpo/2J75tzjuqqSZDfBWwM5adk6M/e4UYEC2Y/uWDKldqu+OrePmOPNZEV3oExt\n" +
                        "ai/yIC0dfvYWDN2zkYoPv0++jsYgJenZKLFDYpKM8QQ5MDZf6+MQQV1to3V5+MbE\n" +
                        "7fsRh5WIzTqsEXtB1wIDAQAB\n" +
                        "-----END PUBLIC KEY-----"
            )
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
    fun createUser(firstname: String, lastname: String, username: String, pubKey: String): UserEntity {

        if (userDao.usernameExists(username))
            throw ConflictException("Username already exists.")

        var entity = UserEntity()
        entity.firstname = firstname
        entity.lastname = lastname
        entity.username = username
        entity.pubKey =
            pubKey.replace("\n", "").replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "")

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

    fun updateUser(userUpdate: UserUpdate) {
        val authUser = authorizedUser()
        if (authUser.id != userUpdate.id && !authUser.admin)
            throw ForbiddenException("Cannot update another user without administrator status")

        val entity = userDao.findById(userUpdate.id)

        if (!entity.isPresent) throw NotFoundException("Could not find the requested user")

        val presentEntity = entity.get()
        presentEntity.firstname = userUpdate.firstName ?: presentEntity.firstname
        presentEntity.lastname = userUpdate.lastName ?: presentEntity.lastname
        presentEntity.username = userUpdate.username ?: presentEntity.username

        if (!authUser.admin && userUpdate.admin != null)
            throw ForbiddenException("Cannot promote/demote user to and from the administrative role without being an administrator.")

        presentEntity.admin = userUpdate.admin ?: presentEntity.admin
        
        userDao.save(presentEntity)
    }

}