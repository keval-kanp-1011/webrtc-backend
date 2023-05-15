package dev.kevalkanpariya

import dev.kevalkanpariya.plugins.*
import io.ktor.server.application.*


/**
 * Originally written by Artem Bagritsevich.
 *
 * https://github.com/artem-bagritsevich/WebRTCKtorSignalingServerExample
 */
fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@JvmOverloads
fun Application.module(testing: Boolean = false) {

    configureSockets()
    configureRouting()
    configureHTTP()
    configureMonitoring()
    configureSerialization()

}
