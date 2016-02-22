package server.domain

import reactivemongo.bson._
import server.domain.dto.AgentDTO


object Agent {

	implicit class AgentConverter(val agent: Agent) {
		implicit def toAgentDTO = AgentDTO(agent.name)
	}

	implicit object AgentWriter extends BSONDocumentWriter[Agent] {
		def write(agent: Agent) =
			BSONDocument(
				"name" -> agent.name,
				"secret" -> agent.secret
			)
	}

	implicit object AgentReader extends BSONDocumentReader[Agent] {
		def read(doc: BSONDocument): Agent =
			Agent(
				doc.getAs[String]("name").get,
				doc.getAs[String]("secret").get
			)
	}
}

case class Agent(name: String, secret: String)
