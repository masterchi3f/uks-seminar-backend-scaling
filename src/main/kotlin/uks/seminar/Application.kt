package uks.seminar

import io.ktor.application.*
import io.ktor.server.netty.*
import uks.seminar.database.mongo.MongoDatabase
import uks.seminar.database.postgres.PostgresDatabase
import uks.seminar.plugins.*
import java.lang.IllegalArgumentException

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    val dbHost: String = environment.config
        .propertyOrNull("ktor.deployment.dbHost")
        ?.getString() ?: throw IllegalArgumentException("Database host is null!")
    val hostname: String = environment.config
        .propertyOrNull("ktor.deployment.hostname")
        ?.getString() ?: throw IllegalArgumentException("hostname is null!")
    configureRouting(hostname)
    configureHTTP()
    configureAdministration()
    configureMetrics()
    PostgresDatabase.connectDb(dbHost)
    MongoDatabase.connectDb(dbHost)
}
