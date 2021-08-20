# Overpass Kotlin Client

Simple client for overpass that uses `ktor-client` and `kotlinx.serialization` for parsing JSON responses.

For now, simply edit the main file (jvm) or call the client from your own code.

## Multi-Platform

It's a multi-platform project, so you can build it for IOS, Android, JVM, Browser/Node.js, etc. Currently, I only have JVM and JS targets. But adding more platforms should not be hard.

## Development Status

Quick and dirty job as I needed a client and a parser. It should work fine as long as you ask overpass for json.

I might build out more features here later. But for now this serves my needs.

Pull requests welcome of course.

## Ideas for More Stuff

- query DSL
- XML support
- publish the jars somewhere; multi-platform is a PITA with jitpack

Don't get your hopes up, I might not get around to doing any of this. But pull requests are welcome.