package uks.seminar.database.postgres

import kotlinx.coroutines.Deferred
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import uks.seminar.models.Coordinate

class PostgresSineDao(private val postgresDatabase: PostgresDatabase) {

    suspend fun getAllCoordinatesAsync(): Deferred<List<Coordinate>> = postgresDatabase.queryAsync {
        CoordinateTable.selectAll().mapNotNull { toCoordinate(it) }
    }

    suspend fun insertCoordinateAsync(c: Coordinate) {
        postgresDatabase.queryAsync {
            CoordinateTable.insert {
                it[x] = c.x
                it[y] = c.y
            }
        }
    }

    suspend fun deleteAllAsync() {
        postgresDatabase.queryAsync {
            CoordinateTable.deleteAll()
        }
    }
}
