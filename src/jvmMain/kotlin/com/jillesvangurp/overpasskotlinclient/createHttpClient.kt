package com.jillesvangurp.overpasskotlinclient

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.features.websocket.*

actual fun createHttpClient(): HttpClient {
    return HttpClient(CIO) {
        engine {
            maxConnectionsCount = 10
            endpoint {
                keepAliveTime = 100_000
                connectTimeout = 5_000
                requestTimeout = 300_000
                connectAttempts = 3
            }
        }
        install(JsonFeature) {
            serializer = defaultSerializer()
        }
    }
}