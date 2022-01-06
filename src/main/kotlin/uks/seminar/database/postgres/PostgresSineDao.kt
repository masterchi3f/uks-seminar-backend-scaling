package uks.seminar.database.postgres

import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import uks.seminar.models.Coordinate

object PostgresSineDao {

    suspend fun getAllCoordinates(): List<Coordinate> = PostgresDatabase.query {
        CoordinateTable.selectAll().mapNotNull { toCoordinate(it) }
    }

    suspend fun insertCoordinate(c: Coordinate) {
        PostgresDatabase.query {
            CoordinateTable.insert {
                it[x] = c.x
                it[y] = c.y
            }
        }
    }

    suspend fun deleteAll() {
        PostgresDatabase.query {
            CoordinateTable.deleteAll()
        }
    }
}
