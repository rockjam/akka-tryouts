package shared

import akka.actor.ActorRef


case object AcquireResource

case class Join(actor: ActorRef)

case class ResourceAcquired(resource: ActorRef)
