package com.jillesvangurp.overpasskotlinclient

import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.features.websocket.*

actual fun createHttpClient(): HttpClient {
    return HttpClient(Js) {
        install(WebSockets)
    }
}