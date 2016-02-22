package server.services

import com.typesafe.config.ConfigObject
import scaldi.Injectable
import server.domain.Agent
import server.helpers.Injector


trait AgentService {

	def findByAgentNameAndSecret(name: String, secret: String): Option[Agent]

	val agents: List[Agent]
}


class AgentServiceImpl extends AgentService with Injectable with Injector {

	def findByAgentNameAndSecret(name: String, secret: String) = agents find (_ == Agent(name, secret))

	val agents = inject[List[ConfigObject]]("agents") map { co =>
		Agent((co get "name" unwrapped).asInstanceOf[String], (co get "secret" unwrapped).asInstanceOf[String])
	}
}