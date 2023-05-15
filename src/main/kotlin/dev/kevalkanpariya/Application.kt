package dev.kevalkanpariya

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import dev.kevalkanpariya.plugins.*
import io.ktor.server.application.*
import java.io.InputStream



/**
 * Originally written by Artem Bagritsevich.
 *
 * https://github.com/artem-bagritsevich/WebRTCKtorSignalingServerExample
 */
fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@JvmOverloads
fun Application.module(testing: Boolean = false) {

    FirebaseAdmin.init()
    configureSecurity()
    configureSockets()
    configureRouting()
    configureHTTP()
    configureMonitoring()
    configureSerialization()

}

object FirebaseAdmin {
    private val serviceAccount: InputStream? =
        this::class.java.classLoader.getResourceAsStream("ktor-firebase-auth-adminsdk.json")

    private val options: FirebaseOptions = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .build()

    fun init(): FirebaseApp = FirebaseApp.initializeApp(options)


}