package server.domain

import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter}
import server.domain.dto.MetricDTO


object Metric {

	implicit class MetricConverter(val metric: Metric) {
		def toMetricDTO = MetricDTO(metric.name, metric.timestamp, metric.value)
	}

	implicit object MetricWriter extends BSONDocumentWriter[Metric] {
		def write(metric: Metric) =
			BSONDocument(
				"agentName" -> metric.agentName,
				"name" -> metric.name,
				"timestamp" -> metric.timestamp,
				"value" -> metric.value)
	}

	implicit object MetricReader extends BSONDocumentReader[Metric] {
		def read(doc: BSONDocument) =
			Metric(
				doc.getAs[String]("agentName").get,
				doc.getAs[String]("name").get,
				doc.getAs[Long]("timestamp").get,
				doc.getAs[Double]("value").get)
	}
}

case class Metric(agentName: String, name: String, timestamp: Long, value: Double)


