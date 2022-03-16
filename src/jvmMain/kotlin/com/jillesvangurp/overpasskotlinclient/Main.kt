package com.jillesvangurp.overpasskotlinclient

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Files

val ukraineBL="45.178431819346656, 27.29264978684341"
val ukraineTR="51.91283087517542, 38.82273252665817"

val ukraineBbox="$ukraineBL,$ukraineTR"
val koelnBbox = "50.91775326845564,6.9158935546875,50.95410145108779,6.979408264160155"

val bbox= ukraineBbox

val hospitalQuery="""
    |[out:json];
    |(
    |   node[amenity=hospital]
    |       ($bbox);
    |   way[amenity=hospital]
    |       ($bbox);
    |);
    |out body;
    |>;
    |out body;
""".trimMargin()

val everythingQuery="""
    |[out:json];
    |(
    |   relation
    |       ($bbox);
    |);
    |out meta;
""".trimMargin()


suspend fun main() {
    val client = OverpassClient()
    val jsonPretty = Json {
        encodeDefaults=true
        explicitNulls=false
        prettyPrint=true
    }
    val result = client.getGeoJson(hospitalQuery)
    val json = jsonPretty.encodeToString(result)
    File("out.geojson").writeText(json)
    println(json)
}