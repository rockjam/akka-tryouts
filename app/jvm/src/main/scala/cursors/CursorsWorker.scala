package cursors

import akka.actor.{ActorRef, ActorSelection, Props}
import shared.{Coordinates, SharedExchange, StaticRoute, WebSocketBase}
import spray.routing.HttpServiceActor

object CursorsWorker {
  def props(serverConnection: ActorRef) = Props(classOf[CursorsWorker], serverConnection)
}

class CursorsWorker(val serverConnection: ActorRef) extends HttpServiceActor with WebSocketBase with StaticRoute {
  override def pool: ActorSelection = context actorSelection "akka://sockets/user/cursors-resources"

  override def businessLogicNoUpgrade: Receive = runRoute {
    staticRoutes ~
    path("cursors") {
      getFromResource("cursors.html")
    }
  }

  override def convertRequestFromClient[T >: SharedExchange](text: String): T = upickle.read[Coordinates](text)

}
