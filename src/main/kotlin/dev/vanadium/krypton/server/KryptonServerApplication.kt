package dev.vanadium.krypton.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@SpringBootApplication
@EnableWebMvc
@ConfigurationPropertiesScan(basePackages = ["dev.vanadium.krypton.server.properties"])
class KryptonServerApplication

fun main(args: Array<String>) {
    runApplication<KryptonServerApplication>(*args)
}
