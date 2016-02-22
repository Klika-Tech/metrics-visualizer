package server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream._
import scaldi.Injectable
import server.helpers.{Injector, Logger}
import server.helpers.json.JsonProtocol
import server.routes.Routes
import server.services.AgentService
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}


object Boot extends App with Injectable with Injector with JsonProtocol with Logger {

	implicit val system = inject[ActorSystem]
	implicit val executor = inject[ExecutionContext]
	implicit val materializer = inject[ActorMaterializer]
	val routes = inject[Routes]
	val interface = inject[String]("http.interface")
	val port = inject[Int]("http.port")
	val agentService = inject[AgentService]

	val binding = Http().bindAndHandle(routes(), interface, port)

	binding onComplete {
		case Success(bind) =>
			val localAddress = bind.localAddress
			log.info(s"Server is listening on ${localAddress.getHostName}:${localAddress.getPort}")

		case Failure(e) =>
			log.info(s"Binding failed with ${e.getMessage}")
			system.terminate()
	}
}