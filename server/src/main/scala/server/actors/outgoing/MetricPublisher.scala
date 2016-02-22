package server.actors.outgoing

import akka.actor._
import akka.event.Logging
import akka.stream.actor.ActorPublisher
import server.domain.Metric
import scala.annotation.tailrec

class MetricPublisher(agentName: String) extends ActorPublisher[Metric] {

	case class QueueUpdated()

	import akka.stream.actor.ActorPublisherMessage._
	import scala.collection.mutable

	val MaxBufferSize = 50
	val queue = mutable.Queue[Metric]()
	var queueUpdated = false
	val log = Logging(context.system, getClass)

	override def preStart() {
		context.system.eventStream.subscribe(self, classOf[Metric])
	}

	override def postStop() {
		context.system.eventStream.unsubscribe(self, classOf[Metric])
	}

	def receive = {

		case metric: Metric =>
			if (queue.size == MaxBufferSize) queue.dequeue()

			queue += metric

			if (!queueUpdated) {
				queueUpdated = true
				self ! QueueUpdated
			}

		// we receive this message if there are new items in the
		// queue. If we have a demand for messages send the requested
		// demand.
		case QueueUpdated => deliver()

		// the connected subscriber requests n messages, we don't need
		// to explicitly check the amount, we use totalDemand property for this
		case Request(_) => deliver()

		// subscriber stops, so we stop as well
		case Cancel => context stop self
	}

	/**
	  * Deliver the message to the subscriber. In the case of websockets over TCP, note
	  * that even if we have a slow consumer, we won't notice that immediately. First the
	  * buffers will fill up before we get feedback.
	  */
	@tailrec private def deliver() {

		if (totalDemand == 0) log.debug(s"No more demand for $agentName")

		if (queue.isEmpty && totalDemand != 0) {
			// we can response to QueueUpdated messages again, since
			// we can't do anything until our queue contains stuff again.
			queueUpdated = false
		} else if (totalDemand > 0 && queue.nonEmpty) {
			// also send a message to the counter
			onNext(queue.dequeue())
			deliver()
		}
	}
}

object MetricPublisher {
	def props(agentName: String) = Props(new MetricPublisher(agentName))
}
