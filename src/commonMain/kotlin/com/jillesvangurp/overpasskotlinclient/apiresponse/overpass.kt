package com.jillesvangurp.overpasskotlinclient.apiresponse

import com.jillesvangurp.geojson.FeatureCollection
import com.jillesvangurp.geojson.Geometry
import com.jillesvangurp.geojson.asFeature
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import mu.KotlinLogging

private val logger = KotlinLogging.logger { }

enum class OsmElementType {
    node,
    way,
    relation
}

@Serializable
sealed class OsmElement {
    abstract val id: Long
    abstract val type: String

    @Serializable
    @SerialName("node")
    data class Node(
        override val id: Long,
        override val type: String = OsmElementType.node.name,
        val lat: Double,
        val lon: Double,
        val timestamp: String? = null,
        val version: Long? = null,
        val changeset: Long? = null,
        val user: String? = null,
        val uid: Long? = null,
        val tags: Map<String, String>? = null,

        ) : OsmElement()

    @Serializable
    data class RelationMember(val type: String, val ref: Long, val role: String)

    @Serializable
    @SerialName("way")
    data class Way(
        override val id: Long,
        override val type: String = OsmElementType.way.name,
        val timestamp: String? = null,
        val version: Long? = null,
        val changeset: Long? = null,
        val user: String? = null,
        val uid: Long? = null,
        val tags: Map<String, String>? = null,
        val nodes: List<Long>,
    ) : OsmElement()

    @Serializable
    @SerialName("relation")
    data class Relation(
        override val id: Long,
        override val type: String = OsmElementType.way.name,
        val timestamp: String? = null,
        val version: Long? = null,
        val changeset: Long? = null,
        val user: String? = null,
        val uid: Long? = null,
        val tags: Map<String, String>? = null,
        val members: List<RelationMember>,
    ) : OsmElement()

}

@Serializable
data class Osm3s(
    val timestamp_osm_base: String,
    val copyright: String,
)

@Serializable
data class OverpassResponse(
    val version: Double,
    val generator: String,
    val osm3s: Osm3s,
    val elements: List<OsmElement>,
)

private fun Map<String, String>?.toJsonObject(id: String?): JsonObject {
    val mapWithId = this?.let {
        val m = it.toMutableMap()
        m["@id"] = id.toString()
        m
    }

    return JsonObject(mapWithId?.entries?.associate { (k, v) -> k to JsonPrimitive(v) } ?: JsonObject(mapOf()))
}

private fun OsmElement.Node.coordinates() = doubleArrayOf(lon, lat)

fun OverpassResponse.toFeatureCollection(): FeatureCollection {
    val nodes = this.elements.filter { it.type == "node" }.map { it as OsmElement.Node }.associateBy { it.id }
    val ways = this.elements.filter { it.type == "way" }.map { it as OsmElement.Way }.associateBy { it.id }
    val relations =
        this.elements.filter { it.type == "relation" }.map { it as OsmElement.Relation }.associateBy { it.id }
    val referredNodes = (ways.map { (_, v) -> v }.flatMap { it.nodes } +
            relations.map { (_, r) -> r }.flatMap { it.members }.filter { it.type == "node" }
                .map { it.ref }).toSet()
    val referredWays =
        relations.map { (_, v) -> v }.flatMap { it.members }.filter { it.type == "way" }
            .map { it.ref }.toSet()
    val features = nodes
        .filter { !referredNodes.contains(it.key) }
        .map { (id, node) ->
            val tags = node.tags.toJsonObject("node/$id")

            Geometry.Point(coordinates = doubleArrayOf(node.lon, node.lat)).asFeature(tags)
        }
    ways.filter { !referredWays.contains(it.key) }.map { (id, way) ->
        val line =
            way.nodes.map { nodes[it]?.coordinates() ?: error("node $it not found in results") }.toTypedArray()
        // if the first and last coordinates are the same, it's a polygon
        val tags = way.tags.toJsonObject("way/$id")
        if (way.nodes.size > 1 && way.nodes.first() == way.nodes.last()) {

            Geometry.Polygon(coordinates = arrayOf(line)).asFeature(tags)
        } else {
            Geometry.LineString(coordinates = line).asFeature(tags)
        }
    }
    if (relations.isNotEmpty()) {
        logger.warn { "relations are not supported yet (FIXME)" }
    }

    return FeatureCollection(features)
}