package uks.seminar.api

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import uks.seminar.database.postgres.PostgresDatabase
import uks.seminar.database.postgres.PostgresSineDao
import uks.seminar.services.SineCalculatorService
import uks.seminar.models.Coordinate
import uks.seminar.models.SineDTO

object PostgresEndpoint {

    private fun Route.sineRoute(postgresDatabase: PostgresDatabase) {
        val postgresSineDao = PostgresSineDao(postgresDatabase)
        route("/postgres/sine") {
            get {
                val coordinates = postgresSineDao.getAllCoordinatesAsync().await()
                if (coordinates.isNotEmpty()) {
                    call.respond(coordinates)
                } else {
                    call.respondText("No sine coordinates added in PostgreSQL.", status = HttpStatusCode.NotFound)
                }
            }
            post {
                val x: Float = call.receive<SineDTO>().x
                val c = Coordinate(x, SineCalculatorService.calculateSine(x))
                postgresSineDao.insertCoordinateAsync(c)
                call.respondText("Coordinate $c stored in PostgreSQL.", status = HttpStatusCode.Created)
            }
            delete {
                postgresSineDao.deleteAllAsync()
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }

    fun Application.postgresEndpoint(postgresDatabase: PostgresDatabase) {
        routing {
            sineRoute(postgresDatabase)
        }
    }
}
