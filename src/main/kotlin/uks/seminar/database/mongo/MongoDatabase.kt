package uks.seminar.database.mongo

import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

object MongoDatabase {

    var database: CoroutineDatabase? = null

    fun connectDb(host: String) {
        val client: CoroutineClient = KMongo
            .createClient("mongodb://admin:123@$host:27017/")
            .coroutine
        val db = client.getDatabase("sine")
        database = db
    }
}
