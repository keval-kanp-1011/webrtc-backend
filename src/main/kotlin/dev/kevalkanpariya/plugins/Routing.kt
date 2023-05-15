package dev.kevalkanpariya.plugins

import com.google.firebase.auth.FirebaseAuth
import dev.kevalkanpariya.SessionManager
import dev.kevalkanpariya.utils.FIREBASE_AUTH
import dev.kevalkanpariya.utils.User
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import java.util.*
import java.util.concurrent.TimeUnit


fun Application.configureRouting() {
    routing {
        rootRoute()
        authenticatedRoute()
        webSocket()
    }

}

fun Route.authenticatedRoute() {
    authenticate(FIREBASE_AUTH) {
        get("/authenticated") {
            val user: User =
                call.principal() ?: return@get call.respond(HttpStatusCode.Unauthorized)
            call.respond("User is authenticated: $user")
        }
    }
}

fun Route.rootRoute() {
    get("/") {
        call.respondText("Welcome to Signaling Server!")
    }
}


fun Route.webSocket() {
    authenticate(FIREBASE_AUTH) {
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
}
