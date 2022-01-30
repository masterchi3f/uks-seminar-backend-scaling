package uks.seminar.plugins

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import uks.seminar.api.MongoEndpoint.mongoEndpoint
import uks.seminar.api.PostgresEndpoint.postgresEndpoint
import uks.seminar.database.postgres.PostgresDatabase

fun Application.configureRouting(hostname: String, postgresDatabase: PostgresDatabase) {
    routing {
        route("/") {
            get {
                call.respondText(hostname)
            }
        }
        mongoEndpoint()
        postgresEndpoint(postgresDatabase)
    }
}
