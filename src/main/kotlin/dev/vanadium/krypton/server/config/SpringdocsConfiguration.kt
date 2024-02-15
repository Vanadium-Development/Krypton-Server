package dev.vanadium.krypton.server.config

import dev.vanadium.krypton.server.SpringDocConfiguration
import dev.vanadium.krypton.server.error.NotFoundException
import org.springdoc.core.configuration.SpringDocUIConfiguration
import org.springdoc.core.properties.SpringDocConfigProperties
import org.springdoc.core.properties.SwaggerUiConfigProperties
import org.springdoc.core.providers.ObjectMapperProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.env.Environment
import org.springframework.core.io.ClassPathResource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*


@Configuration
@RestController
class SpringdocsConfiguration(val environment: Environment) {

    @Bean
    fun springDocConfiguration(): SpringDocConfiguration {
        return SpringDocConfiguration()
    }

    @Bean
    fun springDocConfigProperties(): SpringDocConfigProperties {
        return SpringDocConfigProperties()
    }

    @Bean
    fun objectMapperProvider(springDocConfigProperties: SpringDocConfigProperties?): ObjectMapperProvider {
        return ObjectMapperProvider(springDocConfigProperties)
    }

    @Bean
    fun springDocUIConfiguration(optionalSwaggerUiConfigProperties: Optional<SwaggerUiConfigProperties?>?): SpringDocUIConfiguration {
        return SpringDocUIConfiguration(optionalSwaggerUiConfigProperties)
    }


    @GetMapping("spec.yaml")
    fun getSpecYaml(): ResponseEntity<String> {

        if(!(environment.activeProfiles.contains("dev") || environment.activeProfiles.isEmpty())) {
            return ResponseEntity.ok("Documentation not available outside development mode.")
        }

        val file = ClassPathResource("static/spec.yaml")

        if(!file.exists()) {
            throw NotFoundException("OpenAPI specification was not found")
        }

        return ResponseEntity.ok()
            .header("Content-Type", "text/yaml")
            .contentLength(file.contentLength())
            .body(file.file.readText())
    }
}