package dev.vanadium.krypton.server.config

import org.msgpack.jackson.dataformat.MessagePackFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MsgPack {

    @Bean
    fun msgPackFactory(): MessagePackFactory {
        return MessagePackFactory()
    }

}