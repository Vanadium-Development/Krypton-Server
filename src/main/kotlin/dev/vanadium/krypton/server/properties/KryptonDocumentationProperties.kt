package dev.vanadium.krypton.server.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "krypton.documentation")
class KryptonDocumentationProperties(
    val loginUsername: String,
    val loginPassword: String
)