# Overpass Kotlin Client

Simple client for overpass that uses ktor-client and kotlinx.serialization for parsing json responses.

## Multi platform

It's a multiplatform project so you can build it for IOS, Android, JVM, Browser, etc. Currently I only have jvm and js targets. But adding more should not be hard.

## Development Status

Quick and dirty job as I needed a client and a parser. It should work fine as long as you ask overpass for json.

I might build out more features here later. But for now this serves my needs.

Pull requests welcome of course.

## Ideas for More Stuff

- query DSL
- convert responses to geojson (e.g. using my geogeometry library)
- XML support

Don't get your hopes up, I might not get around to doing any of this. But pull requests are welcome.