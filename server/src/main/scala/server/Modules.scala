package server

import scaldi.{Module, TypesafeConfigInjector}
import server.controllers.{AgentRestController, AgentRestControllerImpl}
import server.dao._
import server.routes._
import server.services._


object Modules {

	class AkkaModule extends Module {

		import akka.actor.ActorSystem
		import akka.stream.ActorMaterializer
		import scala.concurrent.ExecutionContext

		implicit val actorSystem = ActorSystem("metrics-akka-system")
		bind[ActorSystem] to actorSystem destroyWith (_.terminate())
		bind[ExecutionContext] to actorSystem.dispatcher
		bind[ActorMaterializer] to ActorMaterializer()
	}

	class ControllersModule extends Module {
		bind[AgentRestController] to injected[AgentRestControllerImpl]
	}

	class ServicesModule extends Module {
		bind[AgentService] to injected[AgentServiceImpl]

		bind[MetricService] to injected[MetricServiceImpl]
		bind[MetricDAO] to new MetricDAOImpl
	}

	class RoutesModule extends Module {
		bind[AgentRoutes] to injected[AgentRoutes]
		bind[FrontendRoutes] to injected[FrontendRoutes]
		bind[WSRoutes] to injected[WSRoutes]
		bind[Routes] to injected[Routes]
	}

	val injector =
		TypesafeConfigInjector() ::
		  new AkkaModule ::
		  new ControllersModule ::
		  new ServicesModule ::
		  new RoutesModule
}
