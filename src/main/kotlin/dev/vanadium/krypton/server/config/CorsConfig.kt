package dev.vanadium.krypton.server.config

import dev.vanadium.krypton.server.properties.KryptonProperties
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@EnableWebMvc
class CorsConfig(
    val kryptonProperties: KryptonProperties
) : WebMvcConfigurer {


    override fun addCorsMappings(registry: CorsRegistry) {


        registry.addMapping("/**").allowedOrigins(kryptonProperties.corsEnabledDomain).allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE")

    }
}