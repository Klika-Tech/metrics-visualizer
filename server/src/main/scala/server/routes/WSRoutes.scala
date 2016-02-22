package server.routes

import akka.http.scaladsl.server.Directives._
import server.actors.outgoing.MetricFlow
import server.controllers.AgentRestController


class WSRoutes(agentRestController: AgentRestController) extends MetricFlow {

	def apply() =
	// Websocket endpoints
		(path("ws" / "agents" / Segment / "metrics") & parameter("numberOfLastMetrics".as[Int])) {
			(agentName, numberOfLastMetrics) =>
				val futureMetricsOfAgent = agentRestController.latestMetricsOfAgent(agentName, numberOfLastMetrics)
				//flow of metric map to messages
				handleWebSocketMessages(metricFlow(agentName, _.agentName == agentName, futureMetricsOfAgent))
		}
}
