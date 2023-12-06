package dev.vanadium.krypton.server.security

import dev.vanadium.krypton.server.persistence.model.UserEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

class KryptonAuthentication(val user: UserEntity, val token: String) : Authentication {
    override fun getName(): String {
        return user.username
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf()
    }

    override fun getCredentials(): Any {
        return token
    }

    override fun getDetails(): Any {
        return "N/a"

    }

    override fun getPrincipal(): UserEntity {
        return user
    }

    override fun isAuthenticated(): Boolean {
        return true
    }

    override fun setAuthenticated(isAuthenticated: Boolean) {
    }


}