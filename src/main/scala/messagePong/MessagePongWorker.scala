package messagePong

import akka.actor.{ActorRef, Props}
import shared.{WebSocketBase, Response}
import spray.json._
import spray.routing.HttpServiceActor


object MessagePongWorker {
  def props(serverConnection: ActorRef) = Props(classOf[MessagePongWorker], serverConnection)
}

class MessagePongWorker(val serverConnection: ActorRef) extends HttpServiceActor with WebSocketBase {
  override def pool = context actorSelection "akka://sockets/user/message-pong-resources"

  override def businessLogicNoUpgrade = runRoute {
    path("messagePong") {
      getFromResource("messagePong.html")
    }
  }

  override def convertRequest[T >: Response](text: String): T = {
    import shared.ResponseJsonProtocol._
    text.parseJson.convertTo[Message]
  }

}
