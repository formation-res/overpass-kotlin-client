package com.jillesvangurp.overpasskotlinclient.apiresponse

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


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