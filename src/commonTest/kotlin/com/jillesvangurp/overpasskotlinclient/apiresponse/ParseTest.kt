package com.jillesvangurp.overpasskotlinclient.apiresponse

import kotlinx.serialization.json.Json
import kotlin.test.Test

class ParseTest {
    @Test
    fun shouldParse() {
        Json.decodeFromString(OverpassResponse.serializer(), toiletsJson)
        Json.decodeFromString(OverpassResponse.serializer(), relationsJson)
    }
}