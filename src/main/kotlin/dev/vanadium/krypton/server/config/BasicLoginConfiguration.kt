package dev.vanadium.krypton.server.config

import dev.vanadium.krypton.server.properties.KryptonDocumentationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager

@Configuration
class BasicLoginConfiguration(
    private var kryptonDocumentationProperties: KryptonDocumentationProperties

) {



    @Bean
    fun swaggerUserDetailService(): UserDetailsService {
        val swaggerUser = User.builder()
            .username(kryptonDocumentationProperties.loginUsername)
            .password(passwordEncoder().encode(kryptonDocumentationProperties.loginPassword))
            .roles("openapi-spec")
            .build()

        return InMemoryUserDetailsManager(swaggerUser)
    }


    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }


}