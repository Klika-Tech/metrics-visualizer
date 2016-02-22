package server.helpers.json

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.ws.TextMessage
import akka.stream.Materializer
import server.domain.dto.{AgentDTO, MetricDTO}
import server.domain.{Agent, Metric}
import spray.json._


trait JsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {

	implicit val agentFormat = jsonFormat2(Agent.apply)
	implicit val agentDTOFormat = jsonFormat1(AgentDTO.apply)

	implicit val metricFormat = jsonFormat4(Metric.apply)
	implicit val metricDTOFormat = jsonFormat3(MetricDTO.apply)

	implicit val metricDTOListMarshaller =
		SprayJsonSupport.sprayJsonMarshaller[List[MetricDTO]]

	implicit val agentListMarshaller =
		SprayJsonSupport.sprayJsonMarshaller[List[Agent]]

	implicit val agentDTOListMarshaller =
		SprayJsonSupport.sprayJsonMarshaller[List[AgentDTO]]

	implicit def agentDTOUnmarshaller(implicit materializer: Materializer) =
		SprayJsonSupport.sprayJsonUnmarshaller[AgentDTO]

	import spray.json._

	def toMessage(metric: Metric) = TextMessage.Strict(metric.toJson.compactPrint)

	def toMessage(metricDTO: MetricDTO) = TextMessage.Strict(metricDTO.toJson.compactPrint)
}
