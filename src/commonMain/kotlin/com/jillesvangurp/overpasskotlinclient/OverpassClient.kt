package com.jillesvangurp.overpasskotlinclient

import com.jillesvangurp.overpasskotlinclient.apiresponse.OverpassResponse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import mu.KLogger
import mu.KotlinLogging

private val logger: KLogger = KotlinLogging.logger {  }

class OverpassClient(
    private val httpClient: HttpClient = createHttpClient(),
    private val overpassEndpoint: String = "https://www.overpass-api.de/api/interpreter") {

    suspend fun call(data: String): String {
        val queryString = listOf("data" to data).formUrlEncode()

        val url = "$overpassEndpoint?$queryString"
        logger.debug { "calling overpass: $url" }
        return httpClient.post(url) {
            body = data
        }
    }

    suspend fun callAndParse(data: String): OverpassResponse {
        val queryString = listOf("data" to data).formUrlEncode()

        val url = "$overpassEndpoint?$queryString"
        logger.debug { "calling overpass: $url" }
        return httpClient.post(url) {
            accept(ContentType.Application.Json)
            body = data
        }
    }
}

expect fun createHttpClient(): HttpClient