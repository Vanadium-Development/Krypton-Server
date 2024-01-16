package dev.vanadium.krypton.server.config

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GsonConfig {

    @Bean
    fun gson(): Gson {
        return GsonBuilder().setPrettyPrinting().create()
    }
}