package uks.seminar.api

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import uks.seminar.database.mongo.MongoSineDao
import uks.seminar.services.SineCalculatorService
import uks.seminar.models.Coordinate
import uks.seminar.models.SineDTO

object MongoEndpoint {

    private fun Route.sineRoute() {
        route("/mongo/sine") {
            get {
                val coordinates = MongoSineDao.getAllCoordinates()
                if (coordinates.isNotEmpty()) {
                    call.respond(coordinates)
                } else {
                    call.respondText("No sine coordinates added in MongoDB.", status = HttpStatusCode.NotFound)
                }
            }
            post {
                val x: Float = call.receive<SineDTO>().x
                val c = Coordinate(x, SineCalculatorService.calculateSine(x))
                MongoSineDao.insertCoordinate(c)
                call.respondText("Coordinate $c stored in MongoDB.", status = HttpStatusCode.Created)
            }
            delete {
                MongoSineDao.deleteAll()
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }

    fun Application.mongoEndpoint() {
        routing {
            sineRoute()
        }
    }
}
