package viruswar

import akka.actor.{Props, ActorRef}
import shared._
import spray.routing.HttpServiceActor

object VirusWarWorker {
  def props(serverConnection: ActorRef) = Props(classOf[VirusWarWorker], serverConnection)
}

class VirusWarWorker(val serverConnection: ActorRef) extends HttpServiceActor with WebSocketBase with StaticRoute {
  override def pool = context actorSelection "akka://sockets/user/virus-war-resources"

  override def businessLogicNoUpgrade = runRoute {
    path("viruswar") {
      getFromResource("viruswar.html")
    } ~ staticRoutes
  }

}
