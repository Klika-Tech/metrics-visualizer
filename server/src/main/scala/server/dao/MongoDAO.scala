package server.dao

import reactivemongo.api.{ReadPreference, MongoDriver}
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter}
import scaldi.{Injectable, Injector}
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}


abstract class MongoDAO[T](implicit inj: Injector) extends Injectable {

	val collectionName: String
	implicit val ec = inject[ExecutionContext]
	val mongoHost = inject[String]("mongo.host")
	val mongoDb = inject[String]("mongo.db")
	val driver = new MongoDriver
	val connection = driver.connection(List(mongoHost))
	val db = connection.database(mongoDb)

	def collection: BSONCollection = Await.result(db, 5.seconds)(collectionName)

	def insert(document: T)(implicit writer: BSONDocumentWriter[T]) = collection.insert(document)

	def findAll(implicit reader: BSONDocumentReader[T]) =
		collection.
		  find(BSONDocument()).
		  cursor[T](readPreference = ReadPreference.primary).
		  collect[List]()

	def findAll(sort: BSONDocument)(implicit reader: BSONDocumentReader[T]) =
		collection.
		  find(BSONDocument()).
		  sort(sort).
		  cursor[T](readPreference = ReadPreference.primary).
		  collect[List]()
}
