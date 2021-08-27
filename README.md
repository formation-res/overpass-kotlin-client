[![](https://jitpack.io/v/jillesvangurp/overpass-kotlin-client.svg)](https://jitpack.io/#jillesvangurp/overpass-kotlin-client)[![Java CI with Gradle](https://github.com/jillesvangurp/overpass-kotlin-client/actions/workflows/gradle.yml/badge.svg)](https://github.com/jillesvangurp/overpass-kotlin-client/actions/workflows/gradle.yml)

# Overpass Kotlin Client

Simple client for overpass that uses `ktor-client` and `kotlinx.serialization` for parsing JSON responses.

# Usage

To use, add the client to your project (use [![](https://jitpack.io/v/jillesvangurp/overpass-kotlin-client.svg)](https://jitpack.io/#jillesvangurp/overpass-kotlin-client)) and then do something like:

```kotlin
// has some optional parameters for the endpoint and other things
val client = OverpassClient()

// define a query
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

// returns a feature collection
val featureCollection = client.getGeoJson(toiletsBboxKoelnCenter)
// or copy it somewhere like geojson.io to view on a map
println(featureCollection)

// you can also get parse overpass response if you want.
val overpassResponse = client.callAndParse(toiletsBboxKoelnCenter)
println(overpassResponse)

val rawString = client.call(toiletsBboxKoelnCenter)
println(rawString)
```

## Multi-Platform

It's a multi-platform project, so you can build it for IOS, Android, JVM, Browser/Node.js, etc. Currently, I only have JVM and JS targets. But adding more platforms should not be hard.

## Development Status

Quick and dirty job as I needed a client and a parser. But it should work fine as long as you ask overpass for json.

I might build out more features here later. But for now this serves my needs.

Limitations:

- Relations are not fully supported for geojson yet (TODO)

## Ideas for More Stuff

- [ ] query DSL
- [ ] XML support
- [ ] Handle relations for geojson conversion

Don't get your hopes up, I might not get around to doing any of this. **But pull requests are welcome**.
