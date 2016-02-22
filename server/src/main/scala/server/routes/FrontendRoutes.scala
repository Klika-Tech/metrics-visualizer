package server.routes

import akka.http.scaladsl.server.Directives._


class FrontendRoutes {

	def apply() =
		get {
			pathSingleSlash {
				getFromResource("webapp/build/index.html")
			} ~ getFromResourceDirectory("webapp/build")
		}
}
