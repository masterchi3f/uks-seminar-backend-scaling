val ktor_version: String by project
val logback_version: String by project
val prometheus_version: String by project
val kmongo_version: String by project
val exposed_version: String by project
val postgresql_version: String by project

plugins {
    application
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "uks.seminar"
version = "0.0.1"
application {
    mainClass.set("uks.seminar.ApplicationKt")
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

dependencies {
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-serialization:$ktor_version")
    implementation("io.ktor:ktor-metrics-micrometer:$ktor_version")
    implementation("io.micrometer:micrometer-registry-prometheus:$prometheus_version")
    implementation("org.litote.kmongo:kmongo-coroutine-serialization:$kmongo_version")
    implementation("org.jetbrains.exposed:exposed:$exposed_version")
    implementation("org.postgresql:postgresql:$postgresql_version")
}

tasks{
    shadowJar {
        manifest {
            attributes(Pair("Main-Class", "uks.seminar.ApplicationKt"))
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "12"
    }
}
