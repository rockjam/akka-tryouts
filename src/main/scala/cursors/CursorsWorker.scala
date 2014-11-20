package cursors

import akka.actor.{ActorSelection, Props, ActorRef, Actor}
import shared.{Response, WebSocketBase}
import spray.json._
import spray.routing.HttpServiceActor


object CursorsWorker {
  def props(serverConnection: ActorRef) = Props(classOf[CursorsWorker], serverConnection)
}

class CursorsWorker(val serverConnection: ActorRef) extends HttpServiceActor with WebSocketBase {
  override def pool: ActorSelection = context actorSelection "akka://sockets/user/cursors-resources"

  override def businessLogicNoUpgrade: Receive = runRoute {
    pathPrefix("js") {
      get {
        getFromResourceDirectory("js")
      }
    } ~
    pathPrefix("css") {
      get {
        getFromResourceDirectory("css")
      }
    } ~
    pathPrefix("images") {
      get {
        getFromResourceDirectory("images")
      }
    } ~
    path("cursors") {
      getFromResource("cursors.html")
    }
  }

  override def convertRequest[T >: Response](text: String): T = {
    import shared.ResponseJsonProtocol._
    text.parseJson.convertTo[Coordinates]
  }
}
