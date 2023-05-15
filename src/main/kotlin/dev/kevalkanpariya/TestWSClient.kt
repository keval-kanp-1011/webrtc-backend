package dev.kevalkanpariya




/**
 * Originally written by Artem Bagritsevich.
 *
 * https://github.com/artem-bagritsevich/WebRTCKtorSignalingServerExample
 */


object TestWsClient1 {
    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking {
            val client = HttpClient(CIO).config { install(WebSockets) }
            client.ws(method = HttpMethod.Get, host = "127.0.0.1", port = 8080, path = "/rtc") {
                launch(Dispatchers.Default) {
                    delay(15000L)
                    send("${SessionManager.MessageType.OFFER} SDP asdaskfslkdfnlskdnfglksdnfklnsdkf")
                }
                incoming.consumeEach { frame ->
                    when (frame) {
                        is Frame.Text -> {
                            println(frame.readText())
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    /**
     * Used only for testing, subscribes for the offer and sends answer if offer received
     */
    object TestWsClient2 {
        @JvmStatic
        fun main(args: Array<String>) {
            runBlocking {
                val client = HttpClient(CIO).config { install(WebSockets) }
                client.ws(method = HttpMethod.Get, host = "127.0.0.1", port = 8080, path = "/rtc") {
                    incoming.consumeEach { frame ->
                        when (frame) {
                            is Frame.Text -> {
                                val text = frame.readText()
                                println(text)
                                if (text.startsWith("offer", true)) {
                                    send("${SessionManager.MessageType.ANSWER} SDP saknfaslkdjflskdjfklnsdfasdasdsd")
                                }
                            }

                            else -> Unit
                        }
                    }
                }
            }
        }
    }
}



