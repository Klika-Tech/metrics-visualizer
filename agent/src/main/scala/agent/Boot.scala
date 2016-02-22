package agent

import agent.Generator._
import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.MediaTypes._
import akka.http.scaladsl.model.{HttpResponse, HttpEntity, HttpRequest}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._
import spray.json._
import DefaultJsonProtocol._


case class MetricDTO(name: String, timestamp: Long, value: Double)


object Boot extends App {

	implicit val system = ActorSystem("metrics-agent")
	implicit val materializer = ActorMaterializer()

	val log = Logging(system, getClass)

	val conf = ConfigFactory.load()
	val serverHost = conf.getString("server.host")
	val serverPort = conf.getInt("server.port")

	val agentName = conf.getString("agent.name")
	val agentSecret = conf.getString("agent.secret")

	implicit val metricDTOFormat = jsonFormat3(MetricDTO.apply)

	val source = Source.tick(Duration.Zero, 1 second, "tick") map { _ =>

		val jsonString =
			List(
				metricCPU("system.cpu.load"),
				metricMemory("system.memory.total", Total),
				metricMemory("system.memory.used", Used)
			).toJson.toString

		HttpRequest(
			POST,
			uri = s"/api/v1/agents/$agentName/metrics/gauges?secret=$agentSecret",
			entity =
			  HttpEntity(
				  `application/json`,
				  jsonString
			  )
		)
	}

	val connectionFlow = Http().outgoingConnection(serverHost, serverPort)

	val postResponse = Sink.foreach[HttpResponse] { res =>
		log.debug(s"Status - ${res.status}")
	}

	source.via(connectionFlow).runWith(postResponse)
}
