package com.jillesvangurp.overpasskotlinclient.apiresponse

import com.jillesvangurp.geojson.FeatureCollection
import com.jillesvangurp.geojson.Geometry
import com.jillesvangurp.geojson.asFeature
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import mu.KotlinLogging

private val logger= KotlinLogging.logger {  }
enum class OsmElementType {
    node,
    way,
    relation
}

@Serializable
sealed class OsmElement{
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
        val tags: Map<String,String>? = null,

    ): OsmElement()

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
        val tags: Map<String,String>? = null,
        val nodes: List<Long>
    ): OsmElement()

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
        val tags: Map<String,String>? = null,
        val members: List<RelationMember>
    ): OsmElement()

}

@Serializable
data class Osm3s(
    val timestamp_osm_base: String,
    val copyright: String
)
@Serializable
data class OverpassResponse(
    val version: Double,
    val generator: String,
    val osm3s: Osm3s,
    val elements: List<OsmElement>
)

private fun Map<String,String>?.toJsonObject() =
    JsonObject(this?.entries?.associate { (k, v) -> k to JsonPrimitive(v) } ?: JsonObject(mapOf()))

private fun OsmElement.Node.coordinates() = doubleArrayOf(lon, lat)

fun OverpassResponse.toFeatureCollection(): FeatureCollection {
    val nodes = this.elements.filter { it.type == "node" }.map { it as OsmElement.Node }.associateBy { it.id }
    val ways = this.elements.filter { it.type == "way" }.map { it as OsmElement.Way }.associateBy { it.id }
    val relations = this.elements.filter { it.type == "relation" }.map { it as OsmElement.Relation }.associateBy { it.id }
    val referredNodes = (ways.map { (_,v)-> v as OsmElement.Way }.flatMap { it.nodes } +
                                    relations.map { (_,r)-> r as OsmElement.Relation }.flatMap { it.members }.filter { it.type == "node" }.map { it.ref }).toSet()
    val referredWays = relations.map { (_,v)-> v as OsmElement.Relation }.flatMap { it.members }.filter { it.type == "way" }.map { it.ref }.toSet()
    val features = nodes.filter { !referredNodes.contains(it.key) }.map { (_, node)->
        Geometry.Point(coordinates = doubleArrayOf(node.lon, node.lat)).asFeature(node.tags?.toJsonObject())
    }
    ways.filter { !referredWays.contains(it.key) }.map { (_, way) ->
        val line =
            way.nodes.map { nodes[it]?.coordinates() ?: error("node $it not found in results") }.toTypedArray()
        // if the first and last coordinates are the same, it's a polygon
        if(way.nodes.size>1 && way.nodes.first() == way.nodes.last()) {
            Geometry.Polygon(coordinates = arrayOf(line)).asFeature(way.tags.toJsonObject())
        } else {
            Geometry.LineString(coordinates = line).asFeature(way.tags.toJsonObject())
        }
    }
    if(relations.isNotEmpty()) {
        logger.warn { "relations are not supported yet (FIXME)" }
    }

    return FeatureCollection(features)
}