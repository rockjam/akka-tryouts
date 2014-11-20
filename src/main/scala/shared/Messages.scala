package shared

import akka.actor.ActorRef

@deprecated
case object ShowResources

case class Join(actor: ActorRef)

case object AcquireResource

case class ResourceAcquired(resource: ActorRef)

case class Success(status: String = "success") extends Response

case class Failure(message: String, status: String = "failure") extends Response

trait Response//TODO: rename to Exchange