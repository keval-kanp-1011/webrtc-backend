package dev.kevalkanpariya.plugins

import dev.kevalkanpariya.SessionManager
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import java.util.*


fun Application.configureRouting() {
    routing {
        rootRoute()
        webSocket()
    }

}



fun Route.rootRoute() {
    get("/") {
        call.respondText("Welcome to Signaling Server!")
    }
}


fun Route.webSocket() {
    webSocket("/rtc") {
        val sessionID = UUID.randomUUID()
        try {
            SessionManager.onSessionStarted(sessionID, this)

            for (frame in incoming) {
                when (frame) {
                    is Frame.Text -> {
                        SessionManager.onMessage(sessionID, frame.readText())
                    }

                    else -> Unit
                }
            }
            println("Exiting incoming loop, closing session: $sessionID")
            SessionManager.onSessionClose(sessionID)
        } catch (e: ClosedReceiveChannelException) {
            println("onClose $sessionID")
            SessionManager.onSessionClose(sessionID)
        } catch (e: Throwable) {
            println("onError $sessionID $e")
            SessionManager.onSessionClose(sessionID)
        }
    }
}
