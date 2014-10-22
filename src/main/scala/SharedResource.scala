import akka.actor.{Props, ActorLogging, ActorRef, Actor}

object SharedResource {
  def props() = Props[SharedResource]
}

class SharedResource extends Actor with ActorLogging {
  import context._

  def initialState = Message("hello world", 2)

  def receive = notReady

  def notReady = ({
    case Join(user) =>
      user ! Success()
      become(waitingForUser(user))
    case x: Message => sender ! Failure("resource is not ready yet")
  }: Receive) orElse wrongMessageType

  def waitingForUser(FIRST: ActorRef) = ({
    case Join(FIRST) => FIRST ! Failure("you have joined already")
    case Join(user) =>
      user ! Success()
      FIRST ! initialState
      become(working(FIRST, user, initialState) )
    case x: Message => sender ! Failure("resource is not ready yet 3")
  }: Receive) orElse wrongMessageType

  def working(OWNER: ActorRef, WAITER: ActorRef, state: Message):Receive = ({
    case message: Message =>
      sender match {
        case OWNER =>
          OWNER ! Success()
          WAITER ! message
          become(working(WAITER, OWNER, message) )
        case WAITER => sender ! Failure("resource is busy right now, wait for your turn")
      }
    case Join(_) => sender ! Failure("cant join more users")
  }: Receive) orElse wrongMessageType

  def wrongMessageType: Receive = {
    case _ => sender ! Failure("wrong message type")
  }

}
