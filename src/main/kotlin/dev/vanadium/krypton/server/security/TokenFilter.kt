package dev.vanadium.krypton.server.security

import dev.vanadium.krypton.server.persistence.dao.SessionDao
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class TokenFilter(private var sessionDao: SessionDao) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val header = request.getHeader("Authorization")

        if(header == null) {
            sendUnauthorized(response)
            return
        }

        val token: String

        try {
            token = header.split(" ").last()
        } catch (e: NoSuchElementException) {
            sendUnauthorized(response)
            return
        }

        val user = sessionDao.getUserBySessionToken(token, false)

        if(user == null) {
            sendUnauthorized(response)
            return
        }

        // Flag the session as authorized. Note that this flag is not really necessary for the authentication process,
        // yet we'd still like to keep track of whether the user was able to use the token to authenticate
        val session = sessionDao.getSessionEntityByToken(token)!!
        session.authorize()

        SecurityContextHolder.getContext().authentication = KryptonAuthentication(user, token)
        filterChain.doFilter(request, response)
    }

    private fun sendUnauthorized(response: HttpServletResponse) {
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.addHeader("Content-Type", "text/plain")
        response.writer.write("Unauthorized")
    }
}