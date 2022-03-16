@file:OptIn(ExperimentalSerializationApi::class)

package com.jillesvangurp.overpasskotlinclient

import com.jillesvangurp.geojson.FeatureCollection
import com.jillesvangurp.geojson.latitude
import com.jillesvangurp.geojson.longitude
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonPrimitive
import java.io.File
import java.util.*

val ukraineBL = "45.178431819346656, 27.29264978684341"
val ukraineTR = "51.91283087517542, 38.82273252665817"

val ukraineBbox = "$ukraineBL,$ukraineTR"
val koelnBbox = "50.91775326845564,6.9158935546875,50.95410145108779,6.979408264160155"

val bbox = ukraineBbox

fun amenityQuery(amenity: String) = """
    |[out:json];
    |(
    |   node[amenity=$amenity]
    |       ($bbox);
    |   way[amenity=$amenity]
    |       ($bbox);
    |);
    |out body;
    |>;
    |out body;
""".trimMargin()

val everythingQuery = """
    |[out:json];
    |(
    |   relation
    |       ($bbox);
    |);
    |out meta;
""".trimMargin()


suspend fun main() {
    // more here: https://wiki.openstreetmap.org/wiki/Overpass_API
//    val endpoint = "https://z.overpass-api.de/api/interpreter"
//    val endpoint="https://www.overpass-api.de/api/interpreter"
    val endpoint ="https://overpass.kumi.systems/api/interpreter"

    val client = OverpassClient(overpassEndpoint = endpoint)
    val jsonPretty = Json {
        encodeDefaults = true
        explicitNulls = false
        prettyPrint = true
    }
    val amenity = "hospital"
    val result = client.getGeoJson(amenityQuery(amenity))
    writeGeoJson(jsonPretty, result)

    writeFormationCsv(result, amenity)
}

private fun writeGeoJson(
    jsonPretty: Json,
    result: FeatureCollection,
) {
    val json = jsonPretty.encodeToString(result)
    File("out.geojson").writeText(json)
    println(json)
}

private fun writeFormationCsv(result: FeatureCollection, amenity: String) {
    val osmPropertyKeys = result.features.flatMap { it.properties?.keys ?: emptySet() }.toSet()
    val columns = listOf("externalId","name", "lat", "lon", "objectType", "attribution", "keyword") + osmPropertyKeys.map { "extra-${it.replace(':','-')}" }
    val entries = result.features.map { feature ->
        val name: String = feature.properties?.get("name:ua")?.jsonPrimitive?.content
            ?: feature.properties?.get("name:ru")?.jsonPrimitive?.content
            ?: feature.properties?.get("name")?.jsonPrimitive?.content
            ?: amenity
        val centroid = feature.geometry?.centroid()!!
        val id = feature.properties?.get("@id")?.jsonPrimitive?.content ?: centroid.toString() // fallback should not be needed but just in case

        val cols = listOf(
            id,
            name,
            centroid.latitude.toString(),
            centroid.longitude.toString(),
            "poi",
            "© contributors of OpenStreetMap",
            "osm,${
                amenity.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                }
            }"
        ) + osmPropertyKeys.map {
            feature.properties?.get(it)?.jsonPrimitive?.content ?: ""
        }
        cols.joinToString("\t") {
            it.replace('\t', ' ').replace('\n', ' ') // we export tsv, so no tabs/newlines in columns please
        }
    }
    val buf = StringBuilder()
    buf.append(columns.joinToString("\t"))
    buf.append('\n')
    entries.forEach {
        buf.append(it)
        buf.append('\n')
    }
    println(buf.toString())
    File("out.tsv").writeText(buf.toString())
}