package dev.vanadium.krypton.server.security

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

class KryptonAuthentication : Authentication {
    override fun getName(): String {
        return "Hans Tester"
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf()
    }

    override fun getCredentials(): Any {
        return "Hans Tester"
    }

    override fun getDetails(): Any {
        return "Hans Tester"

    }

    override fun getPrincipal(): Any {
        return "Hans Tester"
    }

    override fun isAuthenticated(): Boolean {
        return true
    }

    override fun setAuthenticated(isAuthenticated: Boolean) {
    }


}