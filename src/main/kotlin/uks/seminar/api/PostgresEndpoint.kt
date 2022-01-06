package uks.seminar.api

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import uks.seminar.database.postgres.PostgresSineDao
import uks.seminar.services.SineCalculatorService
import uks.seminar.models.Coordinate
import uks.seminar.models.SineDTO

object PostgresEndpoint {

    private fun Route.sineRoute() {
        route("/postgres/sine") {
            get {
                val coordinates = PostgresSineDao.getAllCoordinates()
                if (coordinates.isNotEmpty()) {
                    call.respond(coordinates)
                } else {
                    call.respondText("No sine coordinates added in PostgreSQL.", status = HttpStatusCode.NotFound)
                }
            }
            post {
                val x: Float = call.receive<SineDTO>().x
                val c = Coordinate(x, SineCalculatorService.calculateSine(x))
                PostgresSineDao.insertCoordinate(c)
                call.respondText("Coordinate $c stored in PostgreSQL.", status = HttpStatusCode.Created)
            }
            delete {
                PostgresSineDao.deleteAll()
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }

    fun Application.postgresEndpoint() {
        routing {
            sineRoute()
        }
    }
}
