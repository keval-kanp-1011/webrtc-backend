package dev.kevalkanpariya.plugins

import dev.kevalkanpariya.utils.User
import dev.kevalkanpariya.utils.firebase
import io.ktor.server.application.*
import io.ktor.server.auth.*
import javax.naming.AuthenticationException

fun Application.configureSecurity() {

    authentication {

        firebase {
            validate {
                // TODO look up user profile from DB
                User(it.uid, it.name.orEmpty())
            }
        }

    }


}

val ApplicationCall.userId: String
    get() = principal<User>()?.userId ?: throw AuthenticationException()
