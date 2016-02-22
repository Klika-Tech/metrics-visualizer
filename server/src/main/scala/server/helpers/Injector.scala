package server.helpers

import server.Modules


trait Injector {

	implicit val injector = Modules.injector
}
