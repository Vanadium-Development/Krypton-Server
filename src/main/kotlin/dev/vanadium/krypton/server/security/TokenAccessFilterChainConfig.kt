package dev.vanadium.krypton.server.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@EnableWebSecurity
@Configuration
class TokenAccessFilterChainConfig {


    @Bean
    fun tokenAccessFilterChain(http: HttpSecurity): SecurityFilterChain {

        http {

            securityMatcher("/**")
            csrf { disable() }
            cors { disable() }

            authorizeRequests {
                authorize(anyRequest, authenticated)
            }

            addFilterBefore<UsernamePasswordAuthenticationFilter>(TokenFilter())
        }

        return http.build()
    }
}