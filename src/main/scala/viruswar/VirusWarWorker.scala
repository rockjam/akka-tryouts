package viruswar

import akka.actor.{Props, ActorRef}
import shared.{StaticRoute, GameMove, Exchange, WebSocketBase}
import spray.routing.HttpServiceActor
import spray.json._

object VirusWarWorker {
  def props(serverConnection: ActorRef) = Props(classOf[VirusWarWorker], serverConnection)
}

class VirusWarWorker(val serverConnection: ActorRef) extends HttpServiceActor with WebSocketBase with StaticRoute {
  override def pool = context actorSelection "akka://sockets/user/virus-war-resources"

  override def businessLogicNoUpgrade = runRoute {
    staticRoutes ~
    path("viruswar") {
      getFromResource("viruswar.html")
    }
  }
  override def convertRequest[T >: Exchange](text: String): T = {
    import shared.ExchangeJsonProtocol._
    text.parseJson.convertTo[GameMove]
  }

}
