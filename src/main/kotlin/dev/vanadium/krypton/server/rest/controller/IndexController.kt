package dev.vanadium.krypton.server.rest.controller

import dev.vanadium.krypton.server.config.KryptonInstance
import dev.vanadium.krypton.server.openapi.controllers.DefaultApi
import dev.vanadium.krypton.server.openapi.model.Get200Response
import org.springframework.boot.info.BuildProperties
import org.springframework.core.env.Environment
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class IndexController(val environment: Environment, val buildProperties: BuildProperties, val kryptonInstance: KryptonInstance) : DefaultApi {

    override fun rootGet(): ResponseEntity<Get200Response> {

        val isDevelopment = environment.activeProfiles.isEmpty() || environment.activeProfiles.contains("dev")


        return ResponseEntity.ok(Get200Response("Vanadium Krypton", "${buildProperties.version}-${if (isDevelopment) "dev" else "prod"}-${buildProperties.time}",
            isDevelopment, kryptonInstance.instanceId
        ))
    }
}