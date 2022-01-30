package uks.seminar.database.postgres

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.jetbrains.exposed.sql.transactions.transaction

class PostgresDatabase(private val host: String) {

    private var database: Database = Database.connect(
        "jdbc:postgresql://$host:5432/sine",
        driver = "org.postgresql.Driver",
        user = "admin",
        password = "123"
    )

    fun createTables() : PostgresDatabase {
        transaction {
            create(CoordinateTable)
        }
        return this
    }

    suspend fun <T> queryAsync(block: () -> T): Deferred<T> =
        suspendedTransactionAsync(Dispatchers.IO, db = database) {
            block()
        }
}
