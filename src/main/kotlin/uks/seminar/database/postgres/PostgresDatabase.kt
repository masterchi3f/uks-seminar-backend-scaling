package uks.seminar.database.postgres

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.transactions.transaction

object PostgresDatabase {

    fun connectDb(host: String) {
        Database.connect(
            "jdbc:postgresql://$host:5432/sine",
            driver = "org.postgresql.Driver",
            user = "admin",
            password = "123"
        )
        transaction {
            create(CoordinateTable)
        }
    }

    suspend fun <T> query(block: () -> T): T =
        withContext(Dispatchers.IO) {
            transaction { block() }
        }
}
