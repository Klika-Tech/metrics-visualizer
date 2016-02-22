package server.actors.outgoing

import akka.stream.scaladsl.{Flow, Sink, Source}
import server.domain.Metric
import server.domain.dto.MetricDTO
import server.helpers.json.JsonProtocol
import scala.concurrent.Future


trait MetricFlow extends JsonProtocol {

	type MetricFilter = Metric => Boolean

	def metricFlow(agentName: String, metricFilter: MetricFilter, latestMetricsFuture: Future[List[MetricDTO]]) = {

		val historySource = Source fromFuture latestMetricsFuture mapConcat (_.reverse)

		val streamSource = Source.actorPublisher[Metric](MetricPublisher props agentName)

		val presentSource = streamSource filter metricFilter map (_.toMetricDTO)

		val actualSource = historySource concat presentSource map (toMessage(_))

		Flow.fromSinkAndSource(Sink.ignore, actualSource)
	}
}
