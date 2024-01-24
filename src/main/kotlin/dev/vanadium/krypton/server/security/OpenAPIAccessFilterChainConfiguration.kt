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
class OpenAPIAccessFilterChainConfiguration(
) {


    @Bean
    @Order(-1)
    fun openApiAccessFilterChain(http: HttpSecurity): SecurityFilterChain {

        http {
            cors { disable() }
            csrf { disable() }
            securityMatcher("/spec.yaml")

            authorizeRequests {
                authorize(anyRequest, authenticated)
                hasRole("openapi-spec")

            }

            httpBasic {

            }

        }



        return http.build()
    }


}