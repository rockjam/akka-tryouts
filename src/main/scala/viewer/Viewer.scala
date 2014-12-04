package viewer

import akka.actor.{ActorRef, Props}
import shared.StaticRoute
import spray.can.websocket.WebSocketServerWorker
import spray.can.websocket.frame.TextFrame
import spray.json._
import spray.routing.HttpServiceActor
import tictactoe.GameView

object Viewer {
  def props(serverConnection: ActorRef) = Props(classOf[Viewer], serverConnection)
}

class Viewer(val serverConnection:ActorRef) extends HttpServiceActor with WebSocketServerWorker with StaticRoute {

  override def receive = handshaking orElse businessLogicNoUpgrade orElse closeLogic

  def businessLogicNoUpgrade = runRoute {
    staticRoutes ~
    path("view") {
      getFromResource("view.html")
    }
  }

  override def businessLogic = {
    case x:GameView =>
      import GameView._
      send(TextFrame(x.toJson.toString))
  }

}
