package viewer

import akka.actor.{ActorRef, Props}
import shared.{GameView, StaticRoute}
import spray.can.websocket.WebSocketServerWorker
import spray.can.websocket.frame.TextFrame
import spray.routing.HttpServiceActor

object Viewer {
  def props(serverConnection: ActorRef) = Props(classOf[Viewer], serverConnection)
}

class Viewer(val serverConnection:ActorRef) extends HttpServiceActor with WebSocketServerWorker with StaticRoute {

  override def receive = handshaking orElse businessLogicNoUpgrade orElse closeLogic

  def businessLogicNoUpgrade = runRoute {
    pathPrefix("js") {
      get {
        getFromResource("scala-ws-fastopt.js")
      }
    } ~ staticRoutes ~
    path("view") {
      getFromResource("view.html")
    }
  }

  override def businessLogic = {case x:GameView => send(TextFrame(upickle.write(x)))}

}
