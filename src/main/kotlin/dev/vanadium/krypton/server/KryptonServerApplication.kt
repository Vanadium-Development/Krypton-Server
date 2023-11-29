package dev.vanadium.krypton.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KryptonServerApplication

fun main(args: Array<String>) {
    runApplication<KryptonServerApplication>(*args)
}
