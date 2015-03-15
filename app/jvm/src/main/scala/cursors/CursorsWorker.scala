package cursors

import akka.actor.{ActorSelection, Props, ActorRef}
import shared.{Exchange, WebSocketBase, StaticRoute}
import spray.json._
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

  override def convertRequest[T >: Exchange](text: String): T = {
    import shared.ExchangeJsonProtocol._
    text.parseJson.convertTo[Coordinates]
  }
}
