package uks.seminar.database.postgres

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import uks.seminar.models.Coordinate

object CoordinateTable : Table("coordinate") {
    val x = float("x").primaryKey()
    val y = float("y")
}

fun toCoordinate(row: ResultRow): Coordinate =
    Coordinate(
        row[CoordinateTable.x],
        row[CoordinateTable.y]
    )