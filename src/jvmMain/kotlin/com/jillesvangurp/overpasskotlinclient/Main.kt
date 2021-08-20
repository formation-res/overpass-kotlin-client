package com.jillesvangurp.overpasskotlinclient

val toiletsBboxKoelnCenter="""
    |[out:json];
    |(
    |   node[amenity=toilets]
    |       (50.91775326845564,6.9158935546875,50.95410145108779,6.979408264160155);
    |   way[amenity=toilets]
    |       (50.91775326845564,6.9158935546875,50.95410145108779,6.979408264160155);
    |);
    |out body;
    |>;
    |out body;
""".trimMargin()

val rels="""
    |[out:json];
    |(
    |   relation
    |       (50.91775326845564,6.9158935546875,50.95410145108779,6.979408264160155);
    |);
    |out meta;
""".trimMargin()


suspend fun main() {
    val client = OverpassClient()
    println(client.getGeoJson(toiletsBboxKoelnCenter))
}