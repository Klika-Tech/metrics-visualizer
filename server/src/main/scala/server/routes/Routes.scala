package server.routes

import akka.http.scaladsl.server.Directives._


class Routes(frontendRoutes: FrontendRoutes, agentRoutes: AgentRoutes, wsRoutes: WSRoutes) {

	def apply() = frontendRoutes() ~ agentRoutes() ~ wsRoutes()
}
