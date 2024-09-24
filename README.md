[![](https://jitpack.io/v/jillesvangurp/overpass-kotlin-client.svg)](https://jitpack.io/#jillesvangurp/overpass-kotlin-client)[![Java CI with Gradle](https://github.com/jillesvangurp/overpass-kotlin-client/actions/workflows/gradle.yml/badge.svg)](https://github.com/jillesvangurp/overpass-kotlin-client/actions/workflows/gradle.yml)

# Overpass Kotlin Client

A simple client for overpass that uses `ktor-client` and `kotlinx.serialization` for parsing JSON responses.
We are using this at FORMATION to be able to run some simple queries against OpenStreetMap.

## Gradle

This library is published to our own maven repository.

```kotlin
repositories {
    mavenCentral()
    maven("https://maven.tryformation.com/releases") {
        // optional but it speeds up the gradle dependency resolution
        content {
            includeGroup("com.jillesvangurp")
            includeGroup("com.github.jillesvangurp")
            includeGroup("com.tryformation")
        }
    }
}
```

And then you can add the dependency:

```kotlin
    // check the latest release tag for the latest version
    implementation("com.jillesvangurp:overpass-kotlin-client:x.y.z")
```

Look up the latest release from the releases on Github.

# Usage

To use, add the client dependency to your project. If you are not using Java, also add a suitable ktor client implementation for your platform.

```kotlin
import java.net.http.HttpClient

// has some optional parameters for the endpoint and other things
val client = OverpassClient(
    // pick a ktor client for your platform and make sure to add the dependencies for that
    // see here for selecting the right client for your platform
    // https://ktor.io/docs/eap/client-engines.html#java
    httpClient = HttpClient(Java),
    overpassEndpoint = "https://overpass.kumi.systems/api/interpreter"
)

// define a query
val toiletsBboxKoelnCenter = """
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

Please be mindful of not running expensive queries needlessly when testing against the public overpass servers or run your own server if possible.

## Multi-Platform

It's a multi-platform project, so you can build it for IOS, Android, JVM, Browser/Node.js, etc. Wasm support is currently blocked on a few libraries not having wasm builds yet. 

## Development Status

Quick and dirty job as I needed a client and a parser. But it should work fine as long as you ask overpass for json.

I might build out more features here later. But for now this serves my needs.

Limitations:

- Relations are not fully supported for geojson yet (TODO)

# About FORMATION Gmbh

[FORMATION](https://tryformation.com) is a Berlin based company that is empowering workforces around the world with maps.




