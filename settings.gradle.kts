rootProject.name = "overpass-kotlin-client"

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

plugins {
    id("de.fayard.refreshVersions") version "0.40.1"
}

refreshVersions {
    extraArtifactVersionKeyRules(file("version_key_rules.txt"))
}
