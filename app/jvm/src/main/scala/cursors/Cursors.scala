package cursors

import akka.actor.{ActorRef, Actor, Props}
import shared.{Coordinates, Failure, Success, Join}

object Cursors {
  def props() = Props[Cursors]
}

class Cursors extends Actor{
  import context._
  
  def receive = notReady

  def notReady = ({
    case Join(user) =>
      user ! Success()
      become(waitingForUser(user))
    case x: Coordinates => sender ! Failure("resource is not ready yet")
  }: Receive) orElse wrongMessageType

  def waitingForUser(FIRST: ActorRef) = ({
    case Join(FIRST) => FIRST ! Failure("you have joined already")
    case Join(user) =>
      user ! Success()
      become(working(FIRST, user))
    case x: Coordinates => sender ! Failure("resource is not ready yet 3")
  }: Receive) orElse wrongMessageType

  def working(FIRST: ActorRef, SECOND: ActorRef): Receive = ({
    case coord: Coordinates =>
      sender match {
        case FIRST =>
          SECOND ! coord
        case SECOND =>
          FIRST ! coord
      }
    case Join(_) => sender ! Failure("cant join more users")
  }: Receive) orElse wrongMessageType

  def wrongMessageType: Receive = {
    case _ => sender ! Failure("wrong message type")
  }

}
