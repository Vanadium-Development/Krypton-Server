package dev.vanadium.krypton.server.service

import dev.vanadium.krypton.server.authorizedUser
import dev.vanadium.krypton.server.error.ConflictException
import dev.vanadium.krypton.server.error.ForbiddenException
import dev.vanadium.krypton.server.error.NotFoundException
import dev.vanadium.krypton.server.error.UnauthorizedException
import dev.vanadium.krypton.server.openapi.model.CredentialDump
import dev.vanadium.krypton.server.openapi.model.UserUpdate
import dev.vanadium.krypton.server.openapi.model.VaultResponse
import dev.vanadium.krypton.server.persistence.dao.UserDao
import dev.vanadium.krypton.server.persistence.dao.VaultDao
import dev.vanadium.krypton.server.persistence.model.UserEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.jvm.optionals.getOrElse

@Service
class UserService(
    private val userDao: UserDao,
    private val sessionService: SessionService,
    private val encryptionService: EncryptionService,
    private val vaultService: VaultService,
    private val vaultDao: VaultDao
) {


    /**
     * Creates a new user and saves it in the database.
     *
     * @param firstname the firstname of the user
     * @param lastname the lastname of the user
     * @param username the username of the user
     * @param pubKey the password of the user
     * @return the created UserEntity object
     * @throws ConflictException if the username already exists in the database
     */
    @Transactional
    fun createUser(firstname: String, lastname: String, username: String, pubKey: String, aesKey: String, admin: Boolean): UserEntity {

        if (userDao.usernameExists(username))
            throw ConflictException("Username already exists.")

        var entity = UserEntity()
        entity.firstname = firstname
        entity.lastname = lastname
        entity.username = username
        entity.admin = admin
        entity.aesKey = aesKey
        entity.pubKey =
            pubKey.replace("\n", "").replace("\r", "").replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")


        entity = userDao.save(entity)

        return entity
    }

    /**
     * Aggregate all credentials of the logged-in user
     */
    fun aggregateCredentialsOf(userUUID: UUID): CredentialDump {
        val authUser = authorizedUser()
        if (userUUID != authUser.id && !authUser.admin)
            throw ForbiddenException("This operation requires admin privileges")

        if (userDao.findById(userUUID).isEmpty)
            throw NotFoundException("No user exists with this UUID.")

        val vaults = vaultDao.findByUserId(userUUID)
        val responseVaults: ArrayList<VaultResponse> = arrayListOf()

        vaults.forEach { vault ->
            val fullVault = vaultService.aggregateCredentials(vault.id)
                .getOrElse { throw NotFoundException("A vault that is linked to the user is not accessible: ${vault.id}") }
            responseVaults.add(fullVault)
        }

        return CredentialDump(userUUID, responseVaults)
    }

    /**
     * Authenticates a user by verifying their username and password.
     *
     * @param username The username of the user to authenticate.
     *
     * @return The authentication token for the user's session.
     *
     * @throws UnauthorizedException if the username or password is incorrect, or if the user is deleted.
     */
    @Transactional
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

    @Transactional
    fun updateAesKey(userId: UUID, aesKey: String) {
        this.userDao.updateAesKey(aesKey, userId)
    }
}