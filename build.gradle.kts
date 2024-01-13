import com.google.common.io.Files
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.4"
    id("org.openapi.generator") version "6.6.0"
    id("org.springdoc.openapi-gradle-plugin") version "1.6.0"
    kotlin("jvm") version "1.9.20"
    kotlin("plugin.spring") version "1.9.20"
}

group = "dev.vanadium.krypton"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

sourceSets {
    main {
        kotlin.srcDirs("$rootDir/build/generate-resources/main/src")
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.flywaydb:flyway-core")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    implementation("org.msgpack:jackson-dataformat-msgpack:0.9.7")
}

openApiGenerate {
    generatorName = "kotlin-spring"
    inputSpec = "submodule/openapi/spec.yaml"
    invokerPackage = "dev.vanadium.krypton.server"
    apiPackage = "dev.vanadium.krypton.server.openapi.controllers"
    modelPackage = "dev.vanadium.krypton.server.openapi.model"
    groupId = "dev.vanadium.krypton"
    id = "server"
    openApiGenerate.configOptions.put("useSpringBoot3", "true")
    openApiGenerate.configOptions.put("interfaceOnly", "true")
}


tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }

}


tasks.compileKotlin {
    doFirst {
        Files.copy(File("./submodule/openapi/spec.yaml"), File("./src/main/resources/static/spec.yaml"))
    }
    dependsOn("openApiGenerate")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
