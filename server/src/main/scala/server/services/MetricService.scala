package server.services

import akka.actor.ActorSystem
import server.dao.MetricDAO
import server.domain.Metric
import scala.concurrent.Future


trait MetricService {

	def latestMetricsOfAgent(agentName: String, count: Int): Future[List[Metric]]

	def addMetric(metric: Metric): Unit
}


class MetricServiceImpl(metricDAO: MetricDAO, system: ActorSystem) extends MetricService {

	def latestMetricsOfAgent(agentName: String, count: Int) = metricDAO.getLastN(agentName, count)

	def addMetric(metric: Metric) {
		metricDAO.insert(metric)
		system.eventStream.publish(metric)
	}
}
