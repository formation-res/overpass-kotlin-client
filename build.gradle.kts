plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    `maven-publish`
}

repositories {
    mavenCentral()
    maven("https://www.jillesvangurp.com/maven")
}

kotlin {
    jvm {
        val main by compilations.getting {
            kotlinOptions {
                // Setup the Kotlin compiler options for the 'main' compilation:
                jvmTarget = "1.8"
            }
        }
        val test by compilations.getting {
            kotlinOptions {
                // Setup the Kotlin compiler options for the 'main' compilation:
                jvmTarget = "1.8"
            }
        }
    }
    js(BOTH) {
        nodejs {
            testTask {
                useMocha {
                    // javascript is a lot slower than Java, we hit the default timeout of 2000
                    timeout = "20000"
                }
            }
        }
    }

    sourceSets {

        val commonMain by getting {
                dependencies {
                    implementation(kotlin("stdlib-common"))
                    api("org.jetbrains.kotlinx:kotlinx-serialization-json:_")

                    api("io.ktor:ktor-client-core:_")
                    implementation("io.ktor:ktor-client-logging:_")
                    implementation("io.ktor:ktor-client-serialization:_")
                    implementation("io.github.microutils:kotlin-logging:_")

                    api("com.github.jillesvangurp:geogeometry:_")

                    api("org.jetbrains.kotlinx:kotlinx-serialization-json:_")
                    api("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:_")
                    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:_")
                    api("org.jetbrains.kotlinx:kotlinx-datetime:_")

                }
            }

        val commonTest by getting {
                dependencies {
                    implementation(kotlin("test-common"))
                    implementation(kotlin("test-annotations-common"))
                    implementation("io.kotest:kotest-assertions-core:_")
                }
            }

        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                api("org.jetbrains.kotlinx:kotlinx-serialization-json:_")
                api("io.ktor:ktor-client-cio:_")
                implementation("io.ktor:ktor-client-logging-jvm:_")
                implementation(kotlin("reflect"))

                implementation("org.slf4j:slf4j-api:_")
                implementation("org.slf4j:jcl-over-slf4j:_")
                implementation("org.slf4j:log4j-over-slf4j:_")
                implementation("org.slf4j:jul-to-slf4j:_")
                implementation("ch.qos.logback:logback-classic:_")
            }
        }
        val jvmTest by getting {
            dependencies {
                runtimeOnly("org.junit.jupiter:junit-jupiter:_")
                implementation(kotlin("test-junit"))
            }
        }

        val jsMain by getting {
                dependencies {
                    implementation(kotlin("stdlib-js"))
                    api("org.jetbrains.kotlinx:kotlinx-serialization-json:_")
                }
        }

        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}
