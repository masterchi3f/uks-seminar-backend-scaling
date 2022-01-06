package uks.seminar.plugins

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.serialization.*

fun Application.configureHTTP() {
    install(ContentNegotiation) {
        json()
    }
    install(CORS) {
        anyHost()
    }
}
