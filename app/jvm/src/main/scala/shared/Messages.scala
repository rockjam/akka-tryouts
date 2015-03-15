package shared

import akka.actor.ActorRef

case class Join(actor: ActorRef)

case object AcquireResource

case class ResourceAcquired(resource: ActorRef)

case class Success(status: String = "success") extends Exchange

case class Failure(message: String, status: String = "failure") extends Exchange

trait Exchange