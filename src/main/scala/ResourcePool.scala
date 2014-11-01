import akka.actor.{ActorRef, Props, Actor}

object ResourcePool {
  def props(resourceProps: Props) = Props(classOf[ResourcePool], resourceProps)
}

class ResourcePool(resourceProps: Props) extends Actor {

  def receive = withoutFreeResource(Set())

  def withoutFreeResource(busy: Set[ActorRef]): Receive = {
    case AcquireResource =>
      val res = context.system.actorOf(resourceProps)
      sender ! ResourceAcquired(res)
      res ! Join(sender)
      context become withFreeResource(res, busy)
    case ShowResources => println(busy)
    case _ => sender ! Failure("some message")//эти сообщения никак не обрабатываются
  }

  def withFreeResource(free: ActorRef, busy: Set[ActorRef]): Receive = {
    case AcquireResource =>
      sender ! ResourceAcquired(free)
      free ! Join(sender)
      context become withoutFreeResource(busy + free)
    case ShowResources => println(busy)
    case _ => sender ! Failure("some message")//эти сообщения никак не обрабатываются
  }


}
