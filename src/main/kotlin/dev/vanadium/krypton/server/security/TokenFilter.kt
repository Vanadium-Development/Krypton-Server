package dev.vanadium.krypton.server.security

import dev.vanadium.krypton.server.persistence.dao.SessionDao
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import java.time.Instant

class TokenFilter(private var sessionDao: SessionDao) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        if(request.method.equals("OPTIONS")) {
            SecurityContextHolder.getContext().authentication = CorsAuthentication()
            SecurityContextHolder.getContext().authentication.isAuthenticated = true

            filterChain.doFilter(request,response)
            return
        }

        val header = request.getHeader("Authorization")

        if (header == null) {
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

        val session = sessionDao.getSessionEntityByToken(token)

        if (user == null || session == null) {
            sendUnauthorized(response)
            return
        }

        session.accessedAt = Instant.now()

        if(!session.dirty)
            session.dirty = true

        sessionDao.save(session)

        SecurityContextHolder.getContext().authentication = KryptonAuthentication(user, session, token)
        filterChain.doFilter(request, response)
    }

    private fun sendUnauthorized(response: HttpServletResponse) {
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.addHeader("Content-Type", "text/plain")
        response.writer.write("Unauthorized")
    }
}