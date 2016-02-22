package server.dao

import reactivemongo.api.ReadPreference
import reactivemongo.bson._
import scaldi.Injector
import server.domain.Metric
import scala.concurrent.Future


trait MetricDAO extends MongoDAO[Metric] {

	override val collectionName = "metrics"

	def getLastN(agentName: String, lastN: Int, priority: Int = -1)(implicit reader: BSONDocumentReader[Metric]): Future[List[Metric]]
}


class MetricDAOImpl(implicit inj: Injector) extends MetricDAO {

	def getLastN(agentName: String, lastN: Int, priority: Int)(implicit reader: BSONDocumentReader[Metric]) =
		collection.
		  find(BSONDocument("agentName" -> agentName)).
		  sort(BSONDocument("_id" -> priority)).
		  cursor[Metric](readPreference = ReadPreference.primary).
		  collect[List](lastN)
}
