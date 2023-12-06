package dev.vanadium.krypton.server.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.web.SecurityFilterChain

@EnableWebSecurity
@Configuration
class PublicAccessFilterChainConfig {

    @Bean
    @Order(1)
    fun publicAccessFilterChain(http: HttpSecurity): SecurityFilterChain {

        http {
            cors { disable() }
            csrf { disable() }

            securityMatcher("/public/**", "/", "/error", "/auth", "/auth/**", "/swagger-ui/**", "/v3/api-docs/**")

            authorizeRequests {
                authorize(anyRequest, permitAll)
            }
        }

        return http.build()
    }
}