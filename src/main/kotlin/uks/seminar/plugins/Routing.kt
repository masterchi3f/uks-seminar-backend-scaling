package uks.seminar.plugins

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import uks.seminar.api.MongoEndpoint.mongoEndpoint
import uks.seminar.api.PostgresEndpoint.postgresEndpoint

fun Application.configureRouting(hostname: String) {
    routing {
        route("/") {
            get {
                call.respondText(hostname)
            }
        }
        mongoEndpoint()
        postgresEndpoint()
    }
}
