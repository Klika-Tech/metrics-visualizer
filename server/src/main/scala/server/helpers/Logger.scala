package server.helpers

import akka.actor.ActorSystem
import akka.event.Logging
import scaldi.Injectable


trait Logger extends Injectable with Injector {

	implicit val log = Logging(inject[ActorSystem], getClass)
}
