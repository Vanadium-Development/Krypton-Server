package dev.vanadium.krypton.server.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class TokenFilter : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
//        response.status = HttpStatus.UNAUTHORIZED.value()
//        response.addHeader("Content-Type", "text/plain")
//        response.writer.write("Unauthorized")

        SecurityContextHolder.getContext().authentication = KryptonAuthentication()
        filterChain.doFilter(request, response) // TODO: Implement correctly working filter to validate the token
    }
}