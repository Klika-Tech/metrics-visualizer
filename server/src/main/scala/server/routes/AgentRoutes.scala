package server.routes

import akka.http.scaladsl.server.Directives._
import server.controllers.AgentRestController
import server.domain.dto.MetricDTO
import server.helpers.json.JsonProtocol


class AgentRoutes(agentRestController: AgentRestController) extends JsonProtocol {

	def apply() =
	// REST endpoints
		pathPrefix("api" / "v1") {
			authV1Routes
		}

	def authV1Routes =
		Seq(
			path("agents") & get & complete(agentRestController.agents),

			(path("agents" / Segment / "metrics") & get & parameter("count".as[Int])) {
				(agentName, count) => complete(agentRestController.latestMetricsOfAgent(agentName, count))
			},

			(path("agents" / Segment / "metrics" / "gauges") & post & parameter("secret")) {
				(agentName, secret) =>
					validate(agentRestController.validateAgent(agentName, secret).nonEmpty, s"Agent is not valid") {
						entity(as[List[MetricDTO]]) { metricDTOs =>
							complete(agentRestController.addMetrics(agentName, metricDTOs))
						}
					}
			}
		).reduceLeft(_ ~ _)
}
