package dev.kevalkanpariya

import dev.kevalkanpariya.plugins.authenticatedRoute
import dev.kevalkanpariya.utils.FIREBASE_AUTH
import dev.kevalkanpariya.utils.FirebaseJWTAuthKey
import dev.kevalkanpariya.utils.User
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.testing.*
import junit.framework.TestCase.assertEquals
import org.junit.Test

class FirebaseAuthTestProvider(config: FirebaseTestConfig) : AuthenticationProvider(config) {

    private val authFunction: () -> User? = config.mockAuthProvider

    override suspend fun onAuthenticate(context: AuthenticationContext) {
        val mockUser: User? = authFunction()
        if (mockUser != null) {
            context.principal(mockUser)
        } else {
            context.error(
                FirebaseJWTAuthKey,
                AuthenticationFailedCause.Error("User was mocked to be unauthenticated")
            )
        }
    }
}

class FirebaseTestConfig(name: String?) : AuthenticationProvider.Config(name) {

    var mockAuthProvider: () -> User? = { null }

}

val defaultTestUser = User(userId = "some-user-id", displayName = "Darth Vader")

fun ApplicationTestBuilder.mockAuthentication(mockAuth: () -> User? = { defaultTestUser }) {
    install(Authentication) {
        val provider = FirebaseAuthTestProvider(FirebaseTestConfig(FIREBASE_AUTH).apply {
            mockAuthProvider = mockAuth
        })
        register(provider)
    }
}

class AuthenticatedRouteTest {
    @Test
    fun `authenticated route - is authenticated`() = testApplication {
        mockAuthentication()
        routing { authenticatedRoute() }

        client.get("/authenticated").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("User is authenticated: $defaultTestUser", bodyAsText())
        }
    }

    @Test
    fun `authenticated route - is unauthorized`() = testApplication {
        mockAuthentication { null }
        routing { authenticatedRoute() }

        client.get("/authenticated").apply {
            assertEquals(HttpStatusCode.Unauthorized, status)
        }
    }
}