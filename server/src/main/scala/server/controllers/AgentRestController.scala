package server.controllers

import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import server.domain.dto.{AgentDTO, MetricDTO}
import server.domain.{Agent, Metric}
import server.services.{AgentService, MetricService}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


trait AgentRestController {

	def validateAgent(name: String, secret: String): Option[Agent]

	val agents: List[AgentDTO]

	def latestMetricsOfAgent(agentName: String, count: Int): Future[List[MetricDTO]]

	def addMetrics(agentName: String, metricDTOs: List[MetricDTO]): StatusCode
}


class AgentRestControllerImpl(agentService: AgentService, metricService: MetricService) extends AgentRestController {


	def validateAgent(name: String, secret: String) = agentService.findByAgentNameAndSecret(name, secret)

	val agents = agentService.agents map (_.toAgentDTO)

	def latestMetricsOfAgent(agentName: String, count: Int) =
		metricService.latestMetricsOfAgent(agentName, count) map (_ map (_.toMetricDTO))


	def addMetrics(agentName: String, metricDTOs: List[MetricDTO]) = {

		metricDTOs foreach { metricDTO =>
			metricService.addMetric(Metric(agentName, metricDTO.name, metricDTO.timestamp, metricDTO.value))
		}
		StatusCodes.Created
	}
}
