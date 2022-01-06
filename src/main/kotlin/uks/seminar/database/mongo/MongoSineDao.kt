package uks.seminar.database.mongo

import org.litote.kmongo.coroutine.CoroutineCollection
import uks.seminar.models.Coordinate
import java.lang.IllegalArgumentException

object MongoSineDao {

    private var collectionHolder: CoroutineCollection<Coordinate>? = null
    private var collection: CoroutineCollection<Coordinate> = getCol()

    private fun getCol(): CoroutineCollection<Coordinate> {
        return collectionHolder ?: run {
            val col: CoroutineCollection<Coordinate>? = MongoDatabase
                .database
                ?.getCollection("coordinates")
            col ?: throw IllegalArgumentException("database object was null!")
            collectionHolder = col
            col
        }
    }

    suspend fun insertCoordinate(coordinate: Coordinate) {
        collection.insertOne(coordinate)
    }

    suspend fun getAllCoordinates(): List<Coordinate> = collection.find().toList()

    suspend fun deleteAll() {
        collection.deleteMany()
    }
}
