package messagePong

import akka.actor.{Actor, ActorRef, Props}
import shared._

object MessagePong {
  def props() = Props[MessagePong]
}

class MessagePong extends Actor {
  import context._

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
      val initialState = Message("ping", 0)
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
