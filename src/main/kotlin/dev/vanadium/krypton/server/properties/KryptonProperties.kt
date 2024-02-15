package dev.vanadium.krypton.server.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "krypton")
class KryptonProperties(val registerEnabled: Boolean = true,  val loginEnabled: Boolean = true) {

}