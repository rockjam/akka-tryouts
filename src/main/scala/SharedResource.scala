import akka.actor.{Props, ActorLogging, ActorRef, Actor}

object SharedResource {
  def props() = Props[SharedResource]
}

class SharedResource extends Actor with ActorLogging {
  import context._

  def initialState = Message("hello world", 2)

  def receive = notReady orElse wrongMessageType

  def notReady: Receive = {
    case Join(user) =>
      user ! Success()
      become(waitingForUser(user) orElse wrongMessageType)
    case x: Message => sender ! Failure("resource is not ready yet")
  }

  def waitingForUser(FIRST: ActorRef): Receive = {
    case Join(FIRST) => FIRST ! Failure("you have joined already")
    case Join(user) =>
      user ! Success()
      FIRST ! initialState
      become(working(FIRST, user, initialState) orElse wrongMessageType)
    case x: Message => sender ! Failure("resource is not ready yet 3")
  }

  def working(OWNER: ActorRef, WAITER: ActorRef, state: Message): Receive = {
    case message: Message =>
      sender match {
        case OWNER =>
          OWNER ! Success()
          WAITER ! message
          become(working(WAITER, OWNER, message) orElse wrongMessageType)
        case WAITER => sender ! Failure("resource is busy right now, wait for your turn")
      }
    case x: Join => sender ! Failure("cant join more users")
  }

  //how to make this usage better?? desired - orElse on declaration of notReady, waitingForUser and working
  def wrongMessageType: Receive = {
    case _ => sender ! Failure("wrong message type")
  }

}
