package com.jillesvangurp.overpasskotlinclient

import com.jillesvangurp.geojson.FeatureCollection
import com.jillesvangurp.overpasskotlinclient.apiresponse.OverpassResponse
import com.jillesvangurp.overpasskotlinclient.apiresponse.toFeatureCollection
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.DefaultJson
import mu.KLogger
import mu.KotlinLogging

private val logger: KLogger = KotlinLogging.logger {  }

class OverpassClient(
    private val httpClient: HttpClient,
    private val overpassEndpoint: String = "https://www.overpass-api.de/api/interpreter") {

    suspend fun call(data: String): String {
        val queryString = listOf("data" to data).formUrlEncode()

        val url = "$overpassEndpoint?$queryString"
        logger.debug { "calling overpass: $url" }
        return httpClient.post(url) {
            setBody(data)
        }.bodyAsText()
    }

    suspend fun callAndParse(data: String): OverpassResponse {
        val queryString = listOf("data" to data).formUrlEncode()

        val url = "$overpassEndpoint?$queryString"
        logger.debug { "calling overpass: $url" }
        return httpClient.post(url) {
            accept(ContentType.Application.Json)
            setBody(data)
        }.let {
            val b = it.bodyAsText()
            DefaultJson.decodeFromString(b)
        }
    }

    suspend fun getGeoJson(data: String): FeatureCollection {
        return callAndParse(data).toFeatureCollection()
    }
}