package dev.vanadium.krypton.server.config

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class KryptonInstance {

    val instanceId = UUID.randomUUID()

}