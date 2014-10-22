import akka.actor.{ActorRef, Props, Actor}

object ResourcePool {
  case object Show
  def props() = Props[ResourcePool]
}

class ResourcePool extends Actor {

  import ResourcePool._
  def receive = withoutFreeResource(List())

  def withoutFreeResource(busy: List[ActorRef]): Receive = {
    case AcquireResource =>
      val res = context.system.actorOf(SharedResource.props())
      sender ! ResourceAcquired(res)
      res ! Join(sender)
      context become withFreeResource(res, busy)
    case Show => println(busy)
    case _ => sender ! "Failure"
  }

  def withFreeResource(free: ActorRef, busy: List[ActorRef]): Receive = {
    case AcquireResource =>
      sender ! ResourceAcquired(free)
      free ! Join(sender)
      context become withoutFreeResource(free :: busy)
    case Show => println(busy)
    case _ => sender ! "Failure"
  }


}
